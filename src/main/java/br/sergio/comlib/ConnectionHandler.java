package br.sergio.comlib;

import java.io.Closeable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ConnectionHandler extends Closeable {
    
    boolean isConnected();
    boolean isClosed();
    void connect(ConnectionInfo info) throws ConnectionException;
    ObjectInputStream getInputStream() throws ConnectionException;
    ObjectOutputStream getOutputStream() throws ConnectionException;

}
