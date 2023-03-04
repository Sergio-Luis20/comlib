package net.stardust.comlib;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public abstract class Channel implements Runnable, Closeable {
    
    protected final ConnectionHandler handler;
    protected final Thread worker;
    protected Queue<Serializable> queue;
    protected boolean closed;

    public Channel(ConnectionHandler handler) {
        this.handler = Objects.requireNonNull(handler);
        queue = new LinkedList<>();
        worker = new Thread(this);
        worker.setDaemon(true);
    }

    public void start() {
        worker.start();
    }

    protected abstract void work() throws Exception;

    protected boolean printStackTrace() {
        return false;
    }

    protected void catchWorkerException(Exception e) {}
    
    public boolean isClosed() {
        return handler.isClosed();
    }

    @Override
    public void close() throws IOException {
        handler.close();
    }

    @Override
    public void run() {
        try(this) {
            work();
        } catch(Exception e) {
            catchWorkerException(e);
        }
    }
 
}
