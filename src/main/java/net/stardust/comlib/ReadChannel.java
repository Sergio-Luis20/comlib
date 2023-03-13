package net.stardust.comlib;

import java.io.ObjectInputStream;
import java.io.Serializable;

public class ReadChannel extends Channel {

    public ReadChannel(ConnectionHandler handler, String id) {
        super(handler, id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T read() {
        currentThread = Thread.currentThread();
        try {
            return (T) queue.take();
        } catch(InterruptedException e) {
            // Connection closed
            return null;
        }
    }

    @Override
    protected void work() throws Exception {
        ObjectInputStream input = handler.getInputStream();
        while(!isClosed()) {
            queue.add((Serializable) input.readObject());
        }
    }

}
