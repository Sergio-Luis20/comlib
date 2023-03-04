package net.stardust.comlib;

import java.io.Serializable;
import java.util.Objects;

public abstract class RequestMapper {

    protected final String id;

    public RequestMapper(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public abstract <T extends Serializable, U extends Serializable> Response<T> handle(Request<U> request) throws Exception;

    public final String getID() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        if(o instanceof RequestMapper mapper) {
            return id.equals(mapper.id);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

}
