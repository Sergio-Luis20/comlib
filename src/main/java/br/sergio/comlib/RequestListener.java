package br.sergio.comlib;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class RequestListener implements Runnable, Closeable {
    
    protected final String id;
    protected RequestMapper mapper;
    protected ConnectionHandler handler;
    private ExecutorService service;

    public RequestListener(String id, RequestMapper mapper, ConnectionHandler handler) {
        this(id, mapper, handler, Executors.newVirtualThreadPerTaskExecutor());
    }

    public RequestListener(String id, RequestMapper mapper, ConnectionHandler handler, ExecutorService service) {
        this.id = Objects.requireNonNull(id, "id");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.handler = Objects.requireNonNull(handler, "handler");
        this.service = Objects.requireNonNull(service, "service");
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
        handler.close();
        service.shutdown();
        try {
            service.awaitTermination(5, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void start();

    public String getId() {
        return id;
    }

    public RequestMapper getMapper() {
        return mapper;
    }

    protected ExecutorService getExecutorService() {
        return service;
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
                && handler.equals(listener.handler) && service.equals(listener.service);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mapper, handler, service);
    }

}
