package net.stardust.comlib;

import java.io.Closeable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ConnectionHandler extends Closeable {
    
    boolean isClosed();
    void connect(ConnectionInfo info) throws Exception;
    ObjectInputStream getInputStream() throws Exception;
    ObjectOutputStream getOutputStream() throws Exception;

}
