package net.stardust.comlib;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WriteChannel extends Channel {

    public WriteChannel(ConnectionHandler handler, String id) {
        super(handler, id);
    }

    public void write(Serializable o) {
        queue.add(o);
    }

    @Override
    protected void work() throws Exception {
        ObjectOutputStream output = handler.getOutputStream();
        currentThread = Thread.currentThread();
        while(!isClosed()) {
            output.writeObject(queue.take());
            output.flush();
        }
    }

}
