package io.penguin.penguincore.config;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import reactor.core.publisher.Mono;

public class ObservableReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {
    @Override
    public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
        return returnValue != null && supportsReturnType(returnType);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Mono.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        final Mono<?> observable = Mono.class.cast(returnValue);
        WebAsyncUtils.getAsyncManager(webRequest)
                .startDeferredResultProcessing(new ObservableAdapter<>(observable), mavContainer);
    }

    public class ObservableAdapter<T> extends DeferredResult<T> {
        public ObservableAdapter(Mono<T> observable) {
            observable.subscribe(this::setResult, this::setErrorResult);
        }
    }
}