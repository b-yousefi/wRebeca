package rebeca.wrebeca.dynamicNetwork;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rebeca.wrebeca.common.State;
import rebeca.wrebeca.common.GlobalState;
import rebeca.wrebeca.common.Message;

public class glStateDynamic extends GlobalState {

    private Map<Integer, State> global_state;
    private int top;

    public Map<Integer, State> getGlobal_state() {
        return global_state;
    } 

    @Override
    public int getTop() {
        return top;
    }

    @Override
    public void setTop(int top_) {
        top = top_;
    }

    @Override
    public List<State> getStates() {
        List<State> states = new ArrayList<>();
        states.addAll(global_state.values());
        return states;
    }

    public void setGlobal_state(Map<Integer, State> global_state) {
        this.global_state = global_state;
    }

    @Override
    public void clearGlobal_state() {
        this.global_state = new HashMap<>();
    }

    public glStateDynamic() {
        setGlobal_state(new HashMap<>());
    }

    @Override
    public int get_number_of_states(Class<?> t) {
        return (int) getGlobal_state().values().stream().filter(x -> x.getClass().equals(t)).count();
    }

    @Override
    public void add_lState(State local_st) {
        State new_st = local_st.deepCopy();
        getGlobal_state().put(new_st.getId(), new_st);
    }

    @Override
    public void remove_lState(State local_st) {
        getGlobal_state().remove(local_st.getId());
    }

    @Override
    public void clear() {
        getGlobal_state().clear();
    }

    @Override
    public void broadcast(Message msg) {
        List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
        for (int item : Reachable_nodes) {
            Method[] mth_namse = getGlobal_state().get(item).getClass().getMethods();
            for (Method m : mth_namse) {
                if (m.getName().equals(msg.getMethodID())) {
                    this.getGlobal_state().get(item).getStorage().addMessage(msg);
                    break;
                }
            }
        }
    }

    @Override
    public void groupcast(Message msg) {
        List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
        List<Integer> recs = msg.getRecID().stream().filter(x -> Reachable_nodes.contains(x)).collect(Collectors.toList());
        for (int item : recs) {
            Method[] mth_namse = getGlobal_state().get(item).getClass().getMethods();
            for (Method m : mth_namse) {
                if (m.getName().equals(msg.getMethodID())) {
                    this.getGlobal_state().get(item).getStorage().addMessage(msg);
                    break;
                }
            }
        }
    }

    @Override
    public boolean unicast(Message msg) {
        List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
        if (msg.getSenderID().equals(msg.getRecID().get(0))) {
            Reachable_nodes.add(msg.getRecID().get(0));
        }
        if (Reachable_nodes.contains(msg.getRecID().get(0))) {
            Method[] mth_namse = getGlobal_state().get(msg.getRecID().get(0)).getClass().getMethods();
            for (Method m : mth_namse) {
                if (m.getName().equals(msg.getMethodID())) {
                    this.getGlobal_state().get(msg.getRecID().get(0)).getStorage().addMessage(msg);
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
        if (getGlobal_state() == null) {
            result += 0;
        } else {
            for (Integer id = 0; id < getGlobal_state().size(); id++) {
                result = prime * result + getGlobal_state().get(id).hashCode();
            }

        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        glStateDynamic other = (glStateDynamic) obj;
        if (getGlobal_state() == null) {
            if (other.getGlobal_state() != null) {
                return false;
            }
        } else {
            for (Integer id : getGlobal_state().keySet()) {
                if (!other.getGlobal_state().containsValue(getGlobal_state().get(id))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public glStateDynamic deepCopy() {
        glStateDynamic newGl = new glStateDynamic();
        newGl.top = this.top;
        newGl.setGlobal_state(new HashMap<>());
        for (Integer i : this.getGlobal_state().keySet()) {
            State st = this.getGlobal_state().get(i).deepCopy();
            newGl.add_lState(st);
        }
        return newGl;
    }

    @Override
    public String toString() {
        return "[" + getGlobal_state() + "]";
    }
}
