package net.stardust.comlib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

public class SocketConnectionHandler implements ConnectionHandler {

    protected Socket socket;
    protected ObjectInputStream input;
    protected ObjectOutputStream output;

    public SocketConnectionHandler() {
        this(new Socket());
    }

    public SocketConnectionHandler(Socket socket) {
        this.socket = Objects.requireNonNull(socket);
    }
    
    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public synchronized void connect(ConnectionInfo info) throws IOException {
        if(info instanceof SocketInfo socketInfo) {
            socket.setSoTimeout(socketInfo.getSoTimeout());
        }
        socket.connect(new InetSocketAddress(info.getIP(), info.getPort()), info.getTimeout());
        input = new ObjectInputStream(socket.getInputStream());
        output = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public ObjectInputStream getInputStream() {
        return input;
    }

    @Override
    public ObjectOutputStream getOutputStream() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        if(o instanceof SocketConnectionHandler handler) {
            return socket.equals(handler.socket);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return socket.hashCode();
    }
    
}
