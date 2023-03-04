package net.stardust.comlib;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Communication {
    
    private static final ConnectionInfo INFO;

    private Communication() {}

    @SuppressWarnings("unchecked")
    public static <T extends Serializable, U extends Serializable> Response<T> send(Request<U> request) throws Exception {
        try(SocketConnectionHandler handler = new SocketConnectionHandler()) {
            handler.connect(INFO);
            if(request != null) {
                ObjectOutputStream output = handler.getOutputStream();
                output.writeObject(request);
                output.flush();
                if(request.getMethod() != null) {
                    ObjectInputStream input = handler.getInputStream();
                    try {
                        return (Response<T>) input.readObject();
                    } catch(ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return Response.emptyResponse();
        }
    }

    static {
        try {
            String location = Files.readString(Paths.get("info-location.txt"));
            INFO = new SocketInfo(Paths.get(location));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
