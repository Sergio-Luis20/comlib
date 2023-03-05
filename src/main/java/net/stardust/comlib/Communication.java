package net.stardust.comlib;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;

public final class Communication {
    
    public static final ConnectionInfo INFO;

    private Communication() {}

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Response<T> send(Request<?> request) throws IOException {
        try(ConnectionHandler handler = new SocketConnectionHandler()) {
            handler.connect(INFO);
            if(request != null) {
                ObjectOutputStream output = handler.getOutputStream();
                output.writeObject(request);
                output.flush();
                if(request.getMethod() != null) {
                    try {
                        return (Response<T>) handler.getInputStream().readObject();
                    } catch(ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return Response.emptyResponse();
        }
    }

    public static RequestListener newRequestListener(String id, RequestMapper mapper) throws ConnectionException {
        SocketConnectionHandler handler = new SocketConnectionHandler();
        handler.connect(INFO);
        return new SocketListener(id, mapper, handler);
    }

    static {
        try {
            INFO = new SocketInfo(Paths.get("lib/communication-info.properties"));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
