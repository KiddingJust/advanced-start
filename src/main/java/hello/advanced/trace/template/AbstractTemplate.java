package hello.advanced.trace.template;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;

public abstract class AbstractTemplate<T> {

    private final LogTrace trace;

    protected AbstractTemplate(LogTrace trace) {
        this.trace = trace;
    }
    //반환 타입이 각각 다르므로 제네릭으로.
    //타입 설정을 객체를 생성하는 시점으로 미루는 것
    public T execute(String message){
        TraceStatus status = null;
        try{
            status = trace.begin("OrderController.request()");
            //비즈니스 로직 호출
            T result = call();
            trace.end(status);
            return result;
        }catch (Exception e){
            trace.exception(status, e);
            throw e;
        }
    }

    protected abstract T call();
}
