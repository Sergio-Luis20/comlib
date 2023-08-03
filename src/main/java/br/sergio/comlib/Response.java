package br.sergio.comlib;

import java.io.Serializable;
import java.util.Optional;

public interface Response<T extends Serializable> extends DataHolder<T> {
    
    int getStatus();

    public static <U extends Serializable> Response<U> emptyResponse() {
        return emptyResponse(ResponseStatus.OK);
    }

    public static <U extends Serializable> Response<U> emptyResponse(int status) {
        return newResponse(null, null, status, null);
    }

    public static <U extends Serializable> Response<U> emptyResponse(String sender, int status) {
        return newResponse(sender, null, status, null);
    }

    public static <U extends Serializable> Response<U> emptyResponse(String sender, String receiver, int status) {
        return newResponse(sender, receiver, status, null);
    }

    public static <U extends Serializable> Response<U> newResponse(String sender, String receiver, int status, U content) {
        if(!ResponseStatus.containsStatus(status)) {
            throw new IllegalArgumentException("unknown status: " + status);
        }
        return new Response<>() {
            
            @Override
            public String getSender() {
                return sender;
            }

            @Override
            public String getReceiver() {
                return receiver;
            }

            @Override
            public int getStatus() {
                return status;
            }

            @Override
            public Optional<U> getContent() {
                return Optional.ofNullable(content);
            }

        };
    }
    
}
