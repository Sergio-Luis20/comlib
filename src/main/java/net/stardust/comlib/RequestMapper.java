package net.stardust.comlib;

@FunctionalInterface
public interface RequestMapper {
    
    <T, U> Response<T> handle(Request<U> request) throws Exception;

}
