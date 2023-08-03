package br.sergio.comlib;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class WriteChannel extends Channel {

    private String readerId;

    public WriteChannel(ConnectionHandler handler, String id, String readerId) {
        super(handler, "write=" + id);
        this.readerId = Objects.requireNonNull(readerId);
    }

    public void write(Serializable o) {
        queue.add(o);
    }

    public void writeAll(Collection<? extends Serializable> collection) {
        if(collection == null) {
            return;
        }
        queue.addAll(collection);
    }

    public void writeAll(Serializable... array) {
        if(array == null) {
            return;
        }
        writeAll(Arrays.asList(array));
    }

    @Override
    protected void work() throws Exception {
        currentThread = Thread.currentThread();
        ObjectOutputStream output = handler.getOutputStream();
        output.writeObject(readerId);
        output.flush();
        while(!isClosed()) {
            output.writeObject(queue.take());
            output.flush();
        }
    }

}
