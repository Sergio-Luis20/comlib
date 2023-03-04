package net.stardust.comlib;

import java.io.Serializable;

@FunctionalInterface
public interface RequestMapper {

    public abstract <T extends Serializable, U extends Serializable> Response<T> handle(Request<U> request) throws MappingException;

}
