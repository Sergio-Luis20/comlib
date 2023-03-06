package net.stardust.comlib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConnectionInfo {
    
    protected String ip;
    protected int port, timeout;
    protected Map<String, String> info;

    public ConnectionInfo(String ip, int port) {
        setIP(ip);
        setPort(port);
    }

    public ConnectionInfo(Path file) throws IOException {
        List<String> list = Files.readAllLines(file);
        info = new HashMap<>();
        for(String str : list) {
            String[] pair = str.split("=");
            info.put(pair[0], pair[1]);
        }
        setIP(info.get("server-ip"));
        setPort(Integer.parseInt(info.get("server-port")));
        String timeout = info.get("timeout");
        setTimeout(timeout == null ? 0 : Integer.parseInt(timeout));
    }

    public ConnectionInfo(Map<String, String> info) {
        setIP(info.get("server-ip"));
        setPort(Integer.parseInt(info.get("server-port")));
        String timeout = info.get("timeout");
        setTimeout(timeout == null ? 0 : Integer.parseInt(timeout));
        this.info = info;
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        String regex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
              "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
              "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
              "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        if(!ip.equals("localhost") && !ip.matches(regex)) {
            throw new IllegalArgumentException("invalid ip: " + ip);
        }
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if(port < 1024 || port >= 65536) {
            throw new IllegalArgumentException("invalid port: " + port);
        }
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout < 0 ? 0 : timeout;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(info);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        if(o instanceof ConnectionInfo info) {
            return ip.equals(info.ip) && port == info.port && timeout == info.timeout;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, timeout);
    }

}
