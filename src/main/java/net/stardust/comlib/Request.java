package net.stardust.comlib;

import java.io.Serializable;
import java.util.Optional;

public interface Request<T extends Serializable> extends DataHolder<T> {
    
    RequestMethod getMethod();

    public static <U extends Serializable> Request<U> noMethodRequest(String receiver, U content) {
        return newRequest(null, receiver, null, content);
    }

    public static <U extends Serializable> Request<U> newRequest(String sender, String receiver, RequestMethod method, U content) {
        return new Request<>() {
            
            @Override
            public String getSender() {
                return sender;
            }

            @Override
            public String getReceiver() {
                return receiver;
            }

            @Override
            public RequestMethod getMethod() {
                return method;
            }

            @Override
            public Optional<U> getContent() {
                return Optional.ofNullable(content);
            }

        };
    }

}
