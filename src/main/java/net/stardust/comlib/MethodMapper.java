package net.stardust.comlib;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MethodMapper implements RequestMapper {

    private MethodHandler handler;
    private AtomicBoolean permitsNullContent;

    public MethodMapper(MethodHandler handler) {
        this(handler, false);
    }

    public MethodMapper(MethodHandler handler, boolean permitsNullContent) {
        this.handler = Objects.requireNonNull(handler, "handler = null");
        this.permitsNullContent = new AtomicBoolean(permitsNullContent);
    }
    
    @Override
    public Response<?> handle(Request<?> request) throws MappingException {
        Object content = request.getContent().orElseGet(() -> null);
        if(content == null && !permitsNullContent()) {
            return Response.emptyResponse(request.getReceiver(), request.getSender(), ResponseStatus.PRECONDITION_REQUIRED);
        }
        RequestMethod method = request.getMethod();
        if(method == null) {
            return Response.emptyResponse();
        }
        ResponseData data = switch(method) {
            case GET -> handler.get(content);
            case POST -> handler.post(content);
            case PUT -> handler.put(content);
            case DELETE -> handler.delete(content);
            case PATCH -> handler.patch(content);
            case HEAD -> handler.head(content);
            case OPTIONS -> handler.options(content);
            case CONNECT -> handler.connect(content);
            case TRACE -> handler.trace(content);
            default -> null;
        };
        if(data == null) {
            throw new CommunicationException("null response data pair");
        }
        return Response.newResponse(request.getReceiver(), request.getSender(), data.status, data.content);
    }

    public MethodHandler getHandler() {
        return handler;
    }

    public boolean permitsNullContent() {
        return permitsNullContent.get();
    }

    public void setPermitsNullContent(boolean permitsNullContent) {
        this.permitsNullContent.set(permitsNullContent);
    }

}
