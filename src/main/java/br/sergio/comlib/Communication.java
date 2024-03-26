package br.sergio.comlib;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class Communication {
    
    private static final ConnectionInfo INFO;
    private static ServerHandler server;

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

    public static <T extends Serializable> Response<T> sendUnsafe(Request<?> request) {
        try {
            return send(request);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean send(String command) throws IOException, ClassNotFoundException {
        if(command != null) {
            try(ConnectionHandler handler = newConnectionHandler()) {
                handler.connect(INFO);
                ObjectOutputStream output = handler.getOutputStream();
                output.writeObject(command);
                output.flush();
                return handler.getInputStream().readBoolean();
            }
        }
        return false;
    }

    public static RequestListener newRequestListener(String id, RequestMapper mapper) throws ConnectionException {
        return new SocketListener(id, mapper, newDefaultHandler());
    }

    public static ConnectionInfo newInfoCopy() {
        return new SocketInfo(INFO.getMap());
    }

    public static ConnectionHandler newConnectionHandler() {
        return new SocketConnectionHandler();
    }

    public static ServerHandler getServer() {
        if(server == null) {
            server = ServerSocketHandler.get();
        }
        return server;
    }

    public static ServerHandler getServer(Logger logger) {
        if(server == null) {
            server = ServerSocketHandler.get(logger);
        }
        return server;
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
