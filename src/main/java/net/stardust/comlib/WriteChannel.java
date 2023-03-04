package net.stardust.comlib;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WriteChannel extends Channel {

    public WriteChannel(ConnectionHandler handler) {
        super(handler);
    }

    public void write(Serializable o) {
        synchronized(queue) {
            if(o != null) {
                queue.add(o);
            }
        }
    }

    @Override
    protected void work() throws Exception {
        try(ObjectOutputStream output = new ObjectOutputStream(handler.getOutputStream())) {
            while(!closed) {
                synchronized(queue) {
                    Serializable obj = queue.poll();
                    if(obj != null) {
                        output.writeObject(obj);
                        output.flush();
                    }
                }
            }
        }
    }

}
