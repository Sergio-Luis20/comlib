package net.stardust.comlib;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Channel implements Runnable, Closeable {
    
    protected final ConnectionHandler handler;
    protected final Thread worker;
    protected final String id;
    protected LinkedBlockingQueue<Serializable> queue;
    protected Thread currentThread;

    public Channel(ConnectionHandler handler, String id) {
        this.handler = Objects.requireNonNull(handler);
        this.id = Objects.requireNonNull(id);
        queue = new LinkedBlockingQueue<>();
        worker = new Thread(this);
        worker.setDaemon(true);
    }

    public void start() {
        worker.start();
    }

    protected abstract void work() throws Exception;

    protected void catchWorkerException(Exception e) {}
    
    public boolean isClosed() {
        return handler.isClosed();
    }

    @Override
    public void close() throws IOException {
        if(currentThread != null) {
            currentThread.interrupt();
            currentThread = null;
        }
        handler.close();
    }

    @Override
    public final void run() {
        try(this) {
            ObjectOutputStream output = handler.getOutputStream();
            output.writeObject("register=" + id);
            output.flush();
            work();
        } catch(Exception e) {
            catchWorkerException(e);
        }
    }
 
}
