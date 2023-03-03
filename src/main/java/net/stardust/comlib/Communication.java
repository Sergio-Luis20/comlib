package net.stardust.comlib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public final class Communication {
    
    private static final CommunicationInfo info;

    private Communication() {}

    @SuppressWarnings("unchecked")
    public static <T, U> Response<T> send(Request<U> request) throws IOException {
        try(Socket socket = new Socket()) {
            socket.connect(info.getInetSocketAddress(), info.getTimeout());
            socket.setSoTimeout(info.getSoTimeout());
            BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
            ByteArrayOutputStream outputArray = new ByteArrayOutputStream(1024);
            if(request != null) {
                ObjectOutputStream obj = new ObjectOutputStream(outputArray);
                obj.writeObject(request);
                obj.flush();
            }
            output.write(outputArray.toByteArray());
            output.flush();
            if(request.getMethod() != null) {
                BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
                ObjectInputStream obj = new ObjectInputStream(input);
                try {
                    return (Response<T>) obj.readObject();
                } catch(ClassNotFoundException e) {
                    throw new Error(e);
                }
            }
            return new Response<>() {
                
                @Override
                public int getStatus() {
                    return ResponseStatus.OK;
                }

                @Override
                public Optional<T> getContent() {
                    return Optional.empty();
                }

            };
        }
    }

    static {
        try {
            String location = Files.readString(Paths.get("info-location.txt"));
            info = new CommunicationInfo(Paths.get(location));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
