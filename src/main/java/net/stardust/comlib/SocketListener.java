package net.stardust.comlib;

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
    public final void run() {
        if(!handler.isConnected()) {
            String message = "cannot init socket listener because socket isn't connected";
            SocketException socketException = new SocketException(message);
            throw new RuntimeException(socketException);
        }
        try {
            ObjectOutputStream output = handler.getOutputStream();
            ObjectInputStream input = handler.getInputStream();
            output.writeUTF("register=" + id);
            output.flush();
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
                }
            }
        } catch(ConnectionException e) {
            catchConnectionException(e);
        } catch(IOException e) {
            catchIOException(e);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
