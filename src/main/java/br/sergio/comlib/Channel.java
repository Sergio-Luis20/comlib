package br.sergio.comlib;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Channel implements Runnable, Closeable {
    
    protected final ConnectionHandler handler;
    protected final Thread worker;
    protected LinkedBlockingQueue<Serializable> queue;
    protected Thread currentThread;
    private final String command;

    public Channel(ConnectionHandler handler, String command) {
        this.handler = Objects.requireNonNull(handler);
        this.command = Objects.requireNonNull(command);
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
            output.writeObject(command);
            output.flush();
            work();
        } catch(Exception e) {
            catchWorkerException(e);
        }
    }
 
}
