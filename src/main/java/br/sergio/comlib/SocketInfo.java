package br.sergio.comlib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Map;

public class SocketInfo extends ConnectionInfo {

    protected int soTimeout;
    
    public SocketInfo(String ip, int port) {
        super(ip, port);
    }

    public SocketInfo(Path file) throws IOException {
        super(file);
        subConstructor();
    }

    public SocketInfo(Map<String, String> info) {
        super(info);
        subConstructor();
    }

    private void subConstructor() {
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
        info.put("so-timeout", String.valueOf(this.soTimeout));
    }

}
