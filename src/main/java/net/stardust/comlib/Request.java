package net.stardust.comlib;

import java.io.Serializable;
import java.util.Optional;

public interface Request<T> extends DataContainer<T>, Serializable {
    
    String getFrom();
    String getTo();
    RequestMethod getMethod();
    <U> Optional<U> invert(U content);

    default Optional<T> invert() {
        return Optional.ofNullable(getContent().orElseGet(() -> null));
    }

}
