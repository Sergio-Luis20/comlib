package net.stardust.comlib;

import java.io.Serializable;
import java.util.Optional;

public class RequestBuilder<T extends Serializable> {
    
    protected String sender, receiver;
    protected RequestMethod method;
    protected T content;

    public RequestBuilder<T> sender(String sender) {
        this.sender = sender;
        return this;
    }

    public RequestBuilder<T> receiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public RequestBuilder<T> method(RequestMethod method) {
        this.method = method;
        return this;
    }

    public RequestBuilder<T> content(T content) {
        this.content = content;
        return this;
    }

    public Request<T> build() {
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
            public Optional<T> getContent() {
                return Optional.ofNullable(content);
            }

        };
    }

}
