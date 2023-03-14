package net.stardust.comlib;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;

public final class Communication {
    
    private static final ConnectionInfo INFO;

    private Communication() {}

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Response<T> send(Request<?> request) throws IOException {
        if(request != null) {
            try(ConnectionHandler handler = newConnectionHandler()) {
                handler.connect(INFO);
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
        }
        return Response.emptyResponse();
    }

    public static RequestListener newRequestListener(String id, RequestMapper mapper) throws ConnectionException {
        return new SocketListener(id, mapper, newDefaultHandler());
    }

    public static ReadChannel newReadChannel(String id) throws ConnectionException {
        return new ReadChannel(newDefaultHandler(), id);
    }

    public static WriteChannel newWriteChannel(String id, String receiver) throws ConnectionException {
        return new WriteChannel(newDefaultHandler(), id, receiver);
    }

    public static ConnectionInfo newInfoCopy() {
        return new SocketInfo(INFO.getMap());
    }

    public static ConnectionHandler newConnectionHandler() {
        return new SocketConnectionHandler();
    }

    private static SocketConnectionHandler newDefaultHandler() throws ConnectionException {
        SocketConnectionHandler handler = (SocketConnectionHandler) newConnectionHandler();
        SocketInfo info = (SocketInfo) newInfoCopy();
        info.setSoTimeout(0);
        handler.connect(info);
        return handler;
    }

    static {
        try {
            INFO = new SocketInfo(Paths.get("lib/communication-info.properties"));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
