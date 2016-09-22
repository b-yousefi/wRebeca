package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VisitedGlobalstates {
  
    private static VisitedGlobalstates instance;
    private final Map<GlobalState, Integer> visited;
    private Integer state_number;

    public GlobalState getGlState(int stNum) {

        for (GlobalState item : visited.keySet()) {
            if (visited.get(item) == stNum) {
                return item;
            }
        }
        return null;
    }

    private VisitedGlobalstates() {
        visited = new ConcurrentHashMap<>();
        state_number = 0;
    }

    public static VisitedGlobalstates getInstance() {
        if (instance == null) {
            instance = new VisitedGlobalstates();
        }
        return instance;
    }

    public Integer size() {
        return visited.size();
    }

    public Integer get_stNumber(GlobalState gl) {
        Integer stNumber = -1;
        stNumber = visited.get(gl);
        if (stNumber == null) {
            stNumber = -1;
        }
        return stNumber;
    }

    public Integer insert(GlobalState gl) {
        Integer stNum = -1;
        synchronized(visited){
            stNum = get_stNumber(gl);
            if (stNum == -1) {
                visited.put(gl, state_number);
                Trans.getInstance().add_st(state_number);
                stNum = state_number;
                state_number++;
            }
        }
        return stNum;
    }

}
