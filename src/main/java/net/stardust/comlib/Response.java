package net.stardust.comlib;

import java.io.Serializable;

public interface Response<T extends Serializable> extends DataHolder<T> {
    
    int getStatus();

    public static <U extends Serializable> Response<U> emptyResponse() {
        return emptyResponse(ResponseStatus.OK);
    }

    public static <U extends Serializable> Response<U> emptyResponse(int status) {
        return new ResponseBuilder<U>().status(status).build();
        
    }
    
}
