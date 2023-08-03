package br.sergio.comlib;

import java.io.Serializable;

@FunctionalInterface
public interface RequestMapper {

    public abstract Response<? extends Serializable> handle(Request<? extends Serializable> request) throws MappingException;

}
