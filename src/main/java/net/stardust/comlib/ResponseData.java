package net.stardust.comlib;

import java.io.Serializable;

public final class ResponseData implements Serializable {
    
    public final int status;
    public final Serializable content;

    public ResponseData(int status, Serializable content) {
        if(!ResponseStatus.containsStatus(status)) {
            throw new IllegalArgumentException("invalid response status: " + status);
        }
        this.status = status;
        this.content = content;
    }

}
