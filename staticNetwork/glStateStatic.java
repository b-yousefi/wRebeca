package wRebeca.staticNetwork;

/**
 * @author Behnaz Yousefi
 *
 */
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wRebeca.common.State;
import wRebeca.common.glState;
import wRebeca.common.message;

public class glStateStatic extends glState {
	private Map<State, List<Integer>> global_state;

	public Map<State, List<Integer>> getGlobal_state() {
		return global_state;
	}

	public int getTop() {
		return 0;
	}

	public void setTop(int top_) {
		// do nothing
	}

	public List<State> getStates() {
		List<State> states = new ArrayList<State>();
		states.addAll(global_state.keySet());
		return states;
	}

	public void clearGlobal_state() {
		this.global_state = new HashMap<State, List<Integer>>();
	}

	public glStateStatic() {
		this.global_state = new HashMap<State, List<Integer>>();
	}

	public int get_number_of_states(Class<?> t) {
		return (int) getGlobal_state().values().stream().filter(x -> x.getClass().equals(t)).count();
	}

	public void add_lState(State local_st) {
		if (!global_state.containsKey(local_st)) {
			global_state.put(local_st, new ArrayList<Integer>());
			global_state.get(local_st).add(local_st.getId());
		} else {
			List<Integer> ids = global_state.get(local_st);
			global_state.remove(local_st);
			ids.add(local_st.getId());
			Collections.sort(ids);
			local_st.setId(Collections.min(ids));

			global_state.put(local_st, ids);
		}
	}

	public void remove_lState(State local_st) {
		if (global_state.containsKey(local_st)) {
			List<Integer> ids = global_state.get(local_st);
			ids.remove((Object) local_st.getId());
			global_state.remove(local_st);
			if (ids.size() != 0) {
				State new_local_st = local_st.deepCopy();
				new_local_st.setId(Collections.min(ids));
				Collections.sort(ids);
				global_state.put(new_local_st, ids);
			}
		}

	}

	private void remove_lState(int item) {
		State local_st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(item)).findFirst()
				.get();
		List<Integer> ids = global_state.get(local_st);
		ids.remove((Object) item);
		global_state.remove(local_st);
		if (ids.size() != 0) {
			State new_local_st = local_st.deepCopy();
			new_local_st.setId(Collections.min(ids));
			Collections.sort(ids);
			global_state.put(new_local_st, ids);
		}
	}

	public void clear() {
		getGlobal_state().clear();
	}

	public void broadcast(message msg) {
		List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
		for (int item : Reachable_nodes) {

			State st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(item)).findFirst().get();
			if (st != null) {
				Method[] mth_namse = st.getClass().getMethods();
				for (Method m : mth_namse) {
					if (m.getName() == msg.getMethodID()) {
						st.setId(item);
						this.remove_lState(item);
						st.getStorage().addMessage(msg);
						this.add_lState(st);
						break;
					}
				}
			}
		}
	}

	public void groupcast(message msg) {
		List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
		List<Integer> recs = msg.getRecID().stream().filter(x -> Reachable_nodes.contains(x))
				.collect(Collectors.toList());
		for (int item : recs) {
			State st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(item)).findFirst().get();
			Method[] mth_namse = st.getClass().getMethods();
			for (Method m : mth_namse) {
				if (m.getName() == msg.getMethodID()) {
					st.setId(item);
					this.remove_lState(st);
					st.getStorage().addMessage(msg);
					this.add_lState(st);
					break;
				}
			}
		}
	}

	public boolean unicast(message msg) {
		List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
		if (msg.getSenderID() == msg.getRecID().get(0))
			Reachable_nodes.add(msg.getRecID().get(0));
		if (Reachable_nodes.contains(msg.getRecID().get(0))) {
			State st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(msg.getRecID().get(0)))
					.findFirst().get();
			Method[] mth_namse = st.getClass().getMethods();
			for (Method m : mth_namse) {
				if (m.getName() == msg.getMethodID()) {
					st.setId(msg.getRecID().get(0));
					this.remove_lState(st);
					st.getStorage().addMessage(msg);
					this.add_lState(st);
					break;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (getGlobal_state() == null)
			result += 0;
		else {
			List<State> sorted_states = new ArrayList<State>();
			sorted_states.addAll(global_state.keySet());
			Collections.sort(sorted_states);
			for (State st : sorted_states) {
				result = prime * result + st.hashCode();
				Collections.sort(getGlobal_state().get(st));
				result = prime * result + getGlobal_state().get(st).size();
			}

		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		glStateStatic other = (glStateStatic) obj;
		if (getGlobal_state() == null) {
			if (other.getGlobal_state() != null)
				return false;
		} else {
			for (State item : getGlobal_state().keySet()) {
				if (!other.getGlobal_state().containsKey(item))
					return false;
				else {
					Collections.sort(getGlobal_state().get(item));
					if (other.getGlobal_state().get(item).size() != this.getGlobal_state().get(item).size())
						return false;
				}
			}
		}
		return true;
	}

	public glState deepCopy() {
		glStateStatic newGl = new glStateStatic();
		newGl.clear();
		for (State item : this.getGlobal_state().keySet()) {
			State st = item.deepCopy();
			List<Integer> ids = new ArrayList<Integer>();
			ids.addAll(global_state.get(item));
			newGl.global_state.put(st, ids);
		}
		return newGl;
	}

	@Override
	public String toString() {
		String str = "glState [";
		for (State item : global_state.keySet()) {
			Collections.sort(getGlobal_state().get(item));
			str += item.toString() + "count:" + global_state.get(item).toString() + ", ";
		}
		str = str.substring(0, str.length() - 1);
		return str + "]";
	}

}
