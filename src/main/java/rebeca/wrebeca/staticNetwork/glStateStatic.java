package rebeca.wrebeca.staticNetwork;

/**
 * @author Behnaz Yousefi
 *
 */
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import rebeca.wrebeca.common.State;
import rebeca.wrebeca.common.GlobalState;
import rebeca.wrebeca.common.Message;

public class glStateStatic extends GlobalState {

    private TreeMap<State, TreeSet<Integer>> global_state;

    public TreeMap<State, TreeSet<Integer>> getGlobal_state() {
        return global_state;
    }

    @Override
    public int getTop() {
        return 0;
    }

    @Override
    public void setTop(int top_) {
        // do nothing
    }

    @Override
    public List<State> getStates() {
        List<State> states = new ArrayList<>();
        states.addAll(global_state.keySet());
        return states;
    }

    @Override
    public void clearGlobal_state() {
        this.global_state = new TreeMap<>();
    }

    public glStateStatic() {
        this.global_state = new TreeMap<>();
    }

    @Override
    public int get_number_of_states(Class<?> t) {
        return (int) getGlobal_state().keySet().stream().filter(x -> x.getClass().equals(t)).count();
    }

    @Override
    public void add_lState(State local_st) {
        if (!global_state.containsKey(local_st)) {
            global_state.put(local_st, new TreeSet<>());
            global_state.get(local_st).add(local_st.getId());
        } else {
            TreeSet<Integer> ids = global_state.get(local_st);
            global_state.remove(local_st);
            ids.add(local_st.getId());
            State copiedState = local_st.deepCopy();
            copiedState.setId(ids.first());

            global_state.put(copiedState, ids);
        }
    }

    @Override
    public void remove_lState(State local_st) {
        if (global_state.containsKey(local_st)) {
            TreeSet<Integer> ids = global_state.get(local_st);
            ids.remove((Object) local_st.getId());
            global_state.remove(local_st);
            if (!ids.isEmpty()) {
                State new_local_st = local_st.deepCopy();
                new_local_st.setId(ids.first());
                global_state.put(new_local_st, ids);
            }
        }

    }

    private void remove_lState(int item) {
        State local_st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(item)).findFirst()
                .get();
        TreeSet<Integer> ids = global_state.get(local_st);
        ids.remove((Object) item);
        global_state.remove(local_st);
        if (!ids.isEmpty()) {
            State new_local_st = local_st.deepCopy();
            new_local_st.setId(ids.first());
            global_state.put(new_local_st, ids);
        }
    }

    @Override
    public void clear() {
        getGlobal_state().clear();
    }

    @Override
    public void broadcast(Message msg) {
        List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
        for (int item : Reachable_nodes) {

            State st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(item)).findFirst().get();
            if (st != null) {
                Method[] mth_namse = st.getClass().getMethods();
                for (Method m : mth_namse) {
                    if (m.getName().equals(msg.getMethodID())) {
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

    @Override
    public void groupcast(Message msg) {
        List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
        List<Integer> recs = msg.getRecID().stream().filter(x -> Reachable_nodes.contains(x))
                .collect(Collectors.toList());
        for (int item : recs) {
            State st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(item)).findFirst().get();
            Method[] mth_namse = st.getClass().getMethods();
            for (Method m : mth_namse) {
                if (m.getName().equals(msg.getMethodID())) {
                    st.setId(item);
                    this.remove_lState(st);
                    st.getStorage().addMessage(msg);
                    this.add_lState(st);
                    break;
                }
            }
        }
    }

    @Override
    public boolean unicast(Message msg) {
        List<Integer> Reachable_nodes = get_reachable_nodes(msg.getSenderID());
        if (msg.getSenderID() == msg.getRecID().get(0)) {
            Reachable_nodes.add(msg.getRecID().get(0));
        }
        if (Reachable_nodes.contains(msg.getRecID().get(0))) {
            State st = global_state.keySet().stream().filter(x -> global_state.get(x).contains(msg.getRecID().get(0)))
                    .findFirst().get();
            Method[] mth_namse = st.getClass().getMethods();
            for (Method m : mth_namse) {
                if (m.getName().equals(msg.getMethodID())) {
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
        if (getGlobal_state() == null) {
            result += 0;
        } else {
            List<State> sorted_states = new ArrayList<>();
            sorted_states.addAll(global_state.keySet());
            Collections.sort(sorted_states);
            for (State st : sorted_states) {
                result = prime * result + st.hashCode();
                result = prime * result + getGlobal_state().get(st).size();
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
        glStateStatic other = (glStateStatic) obj;
        if (getGlobal_state() == null) {
            if (other.getGlobal_state() != null) {
                return false;
            }
        } else {
            for (State item : getGlobal_state().keySet()) {
                if (!other.getGlobal_state().containsKey(item)) {
                    return false;
                } else if (other.getGlobal_state().get(item).size() != this.getGlobal_state().get(item).size()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public GlobalState deepCopy() {
        glStateStatic newGl = new glStateStatic();
        newGl.clear();
        for (State item : this.getGlobal_state().keySet()) {
            State st = item.deepCopy();
            TreeSet<Integer> ids = new TreeSet<>();
            ids.addAll(global_state.get(item));
            newGl.global_state.put(st, ids);
        }
        return newGl;
    }

    @Override
    public String toString() {
        String str = "glState [";
        for (State item : global_state.keySet()) {
            str += item.toString() + "count:" + global_state.get(item).toString() + ", ";
        }
        str = str.substring(0, str.length() - 1);
        return str + "]";
    }

}
