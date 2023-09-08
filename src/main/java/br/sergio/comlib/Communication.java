package br.sergio.comlib;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class Communication {
    
    private static final int DEFAULT_PORT = 8196;
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

    public static ServerHandler getServer() {
        if(server == null) {
            SocketInfo info = INFO instanceof SocketInfo socket ? socket : new SocketInfo(INFO.getMap());
            server = ServerSocketHandler.get(info);
        }
        return server;
    }

    public static ServerHandler getServer(Logger logger) {
        if(server == null) {
            SocketInfo info = INFO instanceof SocketInfo socket ? socket : new SocketInfo(INFO.getMap());
            server = ServerSocketHandler.get(info, logger);
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

    public static void build(ConnectionInfo info) {
        INFO.setIP(info.getIP());
        INFO.setPort(info.getPort());
        INFO.setTimeout(info.getTimeout());
        if(INFO instanceof SocketInfo socketInfo) {
            socketInfo.setSoTimeout(info instanceof SocketInfo replacer ? replacer.getSoTimeout() : 0);
        }
    }

    static {
        try {
            Path infoPath = Paths.get("communication-info.properties");
            INFO = Files.exists(infoPath) ? new SocketInfo(infoPath) : new SocketInfo("localhost", DEFAULT_PORT);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
