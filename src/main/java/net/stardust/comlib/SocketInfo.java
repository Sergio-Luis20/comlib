package net.stardust.comlib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class SocketInfo extends ConnectionInfo {

    protected int soTimeout;
    
    public SocketInfo(String ip, int port) {
        super(ip, port);
    }

    public SocketInfo(Path file) throws IOException {
        super(file);
        String soTimeout = info.get("so-timeout");
        setSoTimeout(soTimeout == null ? 0 : Integer.parseInt(soTimeout));
    }

    public InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(ip, port);
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout < 0 ? 0 : soTimeout;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && soTimeout == ((SocketInfo) o).soTimeout;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + soTimeout;
    }

}
