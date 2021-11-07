package hello.advanced.trace;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TraceId {
    private String id;
    private int level;

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }
    //내부에서만 사용. 다음 Id 쉽게 만들기 위함
    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId(){
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    //다음 id 편하게 만드는 것.
    public TraceId createNextId(){
        return new TraceId(id, level+1);
    }
    //이전 id 만들기. 외부에서도 쓰게 할 것
    public TraceId createPreviousId(){
        return new TraceId(id, level-1);
    }
    //첫번째 level인지 파악
    public boolean isFirstLevel(){
        return level == 0;
    }
}
