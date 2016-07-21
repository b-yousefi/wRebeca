package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VisitedGlobalstates {

    private static VisitedGlobalstates instance;
    private Map<GlobalState, Integer> visited;
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
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
        visited = new HashMap<GlobalState, Integer>();
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
        rwl.readLock().lock();
        try {
            stNumber = visited.get(gl);
        } finally {
            rwl.readLock().unlock();
        }
        if (stNumber == null) {
            stNumber = -1;
        }
        return stNumber;
    }

    public Integer insert(GlobalState gl) {
        Integer stNum = -1;

        rwl.writeLock().lock();
        try {
            stNum = get_stNumber(gl);
            if (stNum == -1) {
                GlobalState gll = gl.deepCopy();
                if (visited.put(gll, state_number) == null) {
                    stNum = state_number;
                    state_number++;
                } else {
                    stNum = state_number;
                }
            }
        } finally {
            rwl.writeLock().unlock();
        }
        return stNum;
    }

}
