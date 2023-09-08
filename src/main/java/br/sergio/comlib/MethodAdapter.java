package br.sergio.comlib;

public class MethodAdapter implements MethodHandler {

    @Override
    public ResponseData get(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData post(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData put(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData delete(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData patch(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData head(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData options(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData connect(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseData trace(Object content) throws MappingException {
        return new ResponseData(ResponseStatus.METHOD_NOT_ALLOWED);
    }
    
}
