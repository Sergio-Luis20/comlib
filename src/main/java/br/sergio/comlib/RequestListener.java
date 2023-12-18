package br.sergio.comlib;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class RequestListener implements Runnable, Closeable {
    
    protected final String id;
    protected final ConnectionHandler handler;
    protected RequestMapper mapper;
    private ExecutorService executor;

    public RequestListener(String id, RequestMapper mapper, ConnectionHandler handler) {
        this.id = Objects.requireNonNull(id, "id");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.handler = Objects.requireNonNull(handler, "handler");
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void connect(ConnectionInfo info) throws ConnectionException {
        handler.connect(info);
    }

    public boolean isConnected() {
        return handler.isConnected();
    }

    public boolean isClosed() {
        return handler.isClosed();
    }

    @Override
    public void close() throws IOException {
        try(handler) {
        	executor.shutdown();
        }
    }

    public void start() {
        executor.execute(this);
    }

    public String getId() {
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
            return id.equals(listener.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
