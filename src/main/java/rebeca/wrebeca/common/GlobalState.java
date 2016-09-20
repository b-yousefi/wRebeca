package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */

import java.util.ArrayList;
import java.util.List;

import rebeca.wrebeca.common.topologyConstraint.Topology;

public abstract class GlobalState implements Cloneable{

    public abstract void clearGlobal_state();

    public abstract int getTop();

    public abstract void setTop(int top_);

    public abstract List<State> getStates();

    public abstract int get_number_of_states(Class<?> t);

    public abstract void add_lState(State local_st);

    public abstract void remove_lState(State local_st);

    public abstract void clear();

    public abstract void broadcast(Message msg);

    public abstract void groupcast(Message msg);

    public abstract boolean unicast(Message msg);

    public abstract GlobalState deepCopy();

    public void send_messages(List<Message> msgs) {
        for (Message mm : msgs) {
            if (mm.getRecID().isEmpty()) {
                this.broadcast(mm);
            } else if (mm.getRecID().size() > 1) {
                this.groupcast(mm);
            } else {
                this.unicast(mm);
            }
        }
    }

    public boolean hasAnyInitMessage() {
        return this.getStates().stream().anyMatch(x -> x.getStorage().hasInitMessage() == true);
    }

    public List<Integer> get_reachable_nodes(int self) {
        List<Integer> neighboures = new ArrayList<>();
        for (int i = 0; i < Topology.topologies.get(this.getTop()).getConnections(self).size(); i++) {
            if (Topology.topologies.get(this.getTop()).getConnections(self).get(i) == true && i != self) {
                neighboures.add(i);
            }
        }
        return neighboures;
    }

}
