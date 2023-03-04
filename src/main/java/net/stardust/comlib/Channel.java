package net.stardust.comlib;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public abstract class Channel implements Runnable, Closeable {
    
    protected final ConnectionHandler handler;
    protected Queue<Serializable> queue;
    protected boolean closed;

    public Channel(ConnectionHandler handler) {
        this.handler = Objects.requireNonNull(handler);
        queue = new LinkedList<>();
        Thread reader = new Thread(this);
        reader.setDaemon(true);
        reader.start();
    }

    protected abstract void work() throws Exception;

    protected boolean printStackTrace() {
        return false;
    }
    
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
            if(printStackTrace()) {
                e.printStackTrace();
            }
        }
    }
 
}
