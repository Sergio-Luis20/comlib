package net.stardust.comlib;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class SocketListener extends SocketConnectionHandler implements Runnable {

    protected final String id;
    protected RequestMapper mapper;
    private Thread thread;

    public SocketListener(String id, RequestMapper mapper, Socket socket) {
        super(socket);
        this.id = Objects.requireNonNull(id);
        this.mapper = Objects.requireNonNull(mapper);
        thread = new Thread(this);
        thread.setDaemon(true);
    }

    public SocketListener(String id, RequestMapper mapper) {
        this(id, mapper, new Socket());
    }

    public void start() {
        thread.start();
    }

    @Override
    public final void run() {
        if(!socket.isConnected()) {
            String message = "cannot init socket listener because socket isn't connected";
            SocketException socketException = new SocketException(message);
            throw new RuntimeException(socketException);
        }
        try {
            output.writeUTF("register=" + id);
            output.flush();
            while(!isClosed()) {
                Object obj = input.readObject();
                if(obj instanceof Request<?> request) {
                    Response<?> response;
                    try {
                        response = mapper.handle(request);
                    } catch(MappingException e) {
                        response = Response.emptyResponse(ResponseStatus.BAD_REQUEST);
                        e.printStackTrace();
                    }
                    output.writeObject(response);
                    output.flush();
                }
            }
        } catch(IOException e) {
            catchIOException(e);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void catchIOException(IOException e) {}

    public String getID() {
        return id;
    }

    public RequestMapper getMapper() {
        return mapper;
    }

}
