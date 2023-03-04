package net.stardust.comlib;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public interface ConnectionHandler extends Closeable {
    
    boolean isClosed();
    void connect(ConnectionInfo info) throws Exception;
    InputStream getInputStream() throws Exception;
    OutputStream getOutputStream() throws Exception;

}
