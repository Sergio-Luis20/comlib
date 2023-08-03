package br.sergio.comlib;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadChannel extends Channel {

    public ReadChannel(ConnectionHandler handler, String id) {
        super(handler, "read=" + id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T take() {
        currentThread = Thread.currentThread();
        try {
            return (T) queue.take();
        } catch(InterruptedException e) {
            // Connection closed
            return null;
        } finally {
            currentThread = null;
        }
    }

    public List<Serializable> drainAll() { 
        List<Serializable> list = new ArrayList<>();
        queue.drainTo(list);
        return list;
    }

    public int available() {
        return queue.size();
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T poll() {
        return (T) queue.poll();
    }

    @Override
    protected void work() throws Exception {
        ObjectInputStream input = handler.getInputStream();
        while(!isClosed()) {
            queue.add((Serializable) input.readObject());
        }
    }

}
