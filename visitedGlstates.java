package wRebeca.common;


/**
 * @author Behnaz Yousefi
 *
 */
import java.util.HashMap;
import java.util.Map;

public class visitedGlstates {
	Map<glState, Integer> tablee;
	public Object lock_put = new Object();

	public glState getGlState(int stNum) {
		
		for (glState item : tablee.keySet()) {
			if (tablee.get(item) == stNum)
				return item;
		}
		return null;
	}

	Integer state_number;

	public Integer table_size() {
		return tablee.size();
	}

	public Integer get_stNumber(glState gl) {
		Integer stNumber = -1;
		synchronized (lock_put) {
			stNumber = tablee.get(gl);
		}
		if (stNumber == null)
			stNumber = -1;
		return stNumber;
	}

	public Integer insert(glState gl) {
		Integer stNum = -1;
		
		
			synchronized (lock_put) {
				stNum = get_stNumber(gl);
				if (stNum == -1) {
				glState gll = gl.deepCopy();
				if (tablee.put(gll, state_number) == null) {
					stNum = state_number;
					state_number++;
				} else {
					stNum = state_number;
				}
			}
		}
		return stNum;
	}

	public visitedGlstates(Integer start) {
		state_number = start;
		tablee = new HashMap<glState, Integer>();
	}

}
