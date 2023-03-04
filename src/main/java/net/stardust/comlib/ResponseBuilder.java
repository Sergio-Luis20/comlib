package net.stardust.comlib;

import java.io.Serializable;
import java.util.Optional;

public class ResponseBuilder<T extends Serializable> {
    
    protected String sender, receiver;
    protected int status;
    protected T content;

    public ResponseBuilder<T> sender(String sender) {
        this.sender = sender;
        return this;
    }

    public ResponseBuilder<T> receiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public ResponseBuilder<T> status(int status) {
        if(!ResponseStatus.containsStatus(status)) {
            throw new IllegalArgumentException("unknown status: " + status);
        }
        this.status = status;
        return this;
    }

    public ResponseBuilder<T> content(T content) {
        this.content = content;
        return this;
    }

    public Response<T> build() {
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
            public Optional<T> getContent() {
                return Optional.ofNullable(content);
            }

        };
    }

}
