package net.stardust.comlib;

import java.io.ObjectInputStream;
import java.io.Serializable;

public class ReadChannel extends Channel {

    public ReadChannel(ConnectionHandler handler) {
        super(handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T read() {
        Serializable data;
        synchronized(queue) {
            if(queue.isEmpty()) {
                queue.notifyAll();
            }
            data = queue.poll();
        }
        return (T) data;
    }

    @Override
    protected void work() throws Exception {
        try(ObjectInputStream input = new ObjectInputStream(handler.getInputStream())) {
            while(!closed) {
                synchronized(queue) {
                    queue.add((Serializable) input.readObject());
                }
            }
        }
    }

}
