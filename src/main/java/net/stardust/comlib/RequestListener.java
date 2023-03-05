package net.stardust.comlib;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

public abstract class RequestListener implements Runnable, Closeable {
    
    protected final String id;
    protected RequestMapper mapper;
    protected ConnectionHandler handler;
    private Thread thread;

    public RequestListener(String id, RequestMapper mapper, ConnectionHandler handler) {
        this.id = Objects.requireNonNull(id);
        this.mapper = Objects.requireNonNull(mapper);
        this.handler = Objects.requireNonNull(handler);
        thread = new Thread(this);
        thread.setDaemon(true);
    }

    public void start() {
        thread.start();
    }

    public boolean isConnected() {
        return handler.isConnected();
    }

    public boolean isClosed() {
        return handler.isClosed();
    }

    @Override
    public void close() throws IOException {
        handler.close();
    }

    protected void catchConnectionException(ConnectionException e) {}
    protected void catchIOException(IOException e) {}

    public String getID() {
        return id;
    }

    public RequestMapper getMapper() {
        return mapper;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        if(o instanceof RequestListener listener) {
            return id.equals(listener.id) && mapper.equals(listener.mapper) 
                && handler.equals(listener.handler) && thread.equals(listener.thread);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mapper, handler, thread);
    }

}
