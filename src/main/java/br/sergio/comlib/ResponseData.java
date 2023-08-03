package br.sergio.comlib;

import java.io.Serializable;

public final class ResponseData implements Serializable {
    
    private int status;
    private Serializable content;

    public ResponseData(int status) {
        this(status, null);
    }

    public ResponseData(int status, Serializable content) {
        if(!ResponseStatus.containsStatus(status)) {
            throw new IllegalArgumentException("invalid response status: " + status);
        }
        this.status = status;
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public Serializable getContent() {
        return content;
    }

}
