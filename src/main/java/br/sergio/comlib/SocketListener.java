package br.sergio.comlib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class SocketListener extends RequestListener {

    public SocketListener(String id, RequestMapper mapper) {
        this(id, mapper, new Socket());
    }

    public SocketListener(String id, RequestMapper mapper, Socket socket) {
        this(id, mapper, new SocketConnectionHandler(socket));
    }

    public SocketListener(String id, RequestMapper mapper, SocketConnectionHandler handler) {
        super(id, mapper, handler);
    }

    @Override
    public void start() {
        if(!handler.isConnected()) {
            String message = "Cannot init socket listener because socket is not connected";
            SocketException socketException = new SocketException(message);
            throw new RuntimeException(socketException);
        }
        try {
            ObjectOutputStream output = handler.getOutputStream();
            output.writeObject("register=" + id);
            output.flush();
            if(handler.getInputStream().readBoolean()) {
                super.start();
            } else {
                registrationFailed();
            }
        } catch(IOException e) {
            catchIOException(e);
        }
    }

    @Override
    public final void run() {
        try {
            ObjectOutputStream output = handler.getOutputStream();
            ObjectInputStream input = handler.getInputStream();
            while(!handler.isClosed()) {
                Object obj = input.readObject();
                if(obj instanceof Request<?> request) {
                    Response<?> response;
                    try {
                        response = mapper.handle(request);
                    } catch(MappingException e) {
                        response = Response.emptyResponse(ResponseStatus.BAD_REQUEST);
                        e.printStackTrace();
                    } catch(Exception e) {
                        response = Response.emptyResponse(ResponseStatus.INTERNAL_SERVER_ERROR);
                        e.printStackTrace();
                    }
                    output.writeObject(response);
                    output.flush();
                } else {
                    throw new IOException(obj + " from " + obj.getClass() + " is not an implementation of " + Request.class);
                }
            }
        } catch(IOException e) {
            catchIOException(e);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void catchIOException(IOException e) {}
    protected void registrationFailed() {}

}
