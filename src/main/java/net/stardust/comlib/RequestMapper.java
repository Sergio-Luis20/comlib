package net.stardust.comlib;

import java.io.Serializable;

public interface RequestMapper {

    String getID();
    <T extends Serializable, U extends Serializable> Response<T> handle(Request<U> request) throws Exception;

}
