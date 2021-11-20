package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldLogTrace implements LogTrace{

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    //traceId 동기화 => 동시성 이슈 생길 수 있는데 뒤에서 다룸
    private TraceId traceIdHolder;

    @Override
    public TraceStatus begin(String message) {
        //sync호출
        syncTraceId();
        //sync 후에는 traceIdHolder에 값이 들어가므로 이렇게
        TraceId traceId = traceIdHolder;
        Long startTimeMs = System.currentTimeMillis();
        //로그 출력
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
        return new TraceStatus(traceId, startTimeMs, message);    }

    private void syncTraceId(){
        if (traceIdHolder == null){
            traceIdHolder = new TraceId();
        }else{
            //null이 아니면 다음 level로
            traceIdHolder = traceIdHolder.createNextId();
        }
    }
    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e){
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if(e == null){
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        }else{
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, e.toString());
        }
        //traceId 없앰
        releaseTraceId();
    }

    private void releaseTraceId() {
        //첫번째 레벨이라는 것은 마지막 단계로 온 것
        if(traceIdHolder.isFirstLevel()){
            traceIdHolder = null;
        }else{
            //이전 level로 돌려주는 것.
            traceIdHolder = traceIdHolder.createPreviousId();
        }
    }

    private static String addSpace(String prefix, int level){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<level; i++){
            sb.append((i == level-1)?"|"+prefix:"|   ");
        }
        return sb.toString();
    }
}
