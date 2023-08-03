package br.sergio.comlib;

public interface MethodHandler {
    
    ResponseData get(Object content) throws MappingException;
    ResponseData post(Object content) throws MappingException;
    ResponseData put(Object content) throws MappingException;
    ResponseData delete(Object content) throws MappingException;
    ResponseData patch(Object content) throws MappingException;
    ResponseData head(Object content) throws MappingException;
    ResponseData options(Object content) throws MappingException;
    ResponseData connect(Object content) throws MappingException;
    ResponseData trace(Object content) throws MappingException;

}
