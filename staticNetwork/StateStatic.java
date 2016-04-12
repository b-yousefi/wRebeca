package wRebeca.staticNetwork;
/**
 * @author Behnaz Yousefi
 *
 */

import java.util.List;

import wRebeca.common.Istorage;
import wRebeca.common.State;
import wRebeca.common.topologyConstraint.Topology;

public class StateStatic extends State {

	public StateStatic(Istorage store) {
		super(store);
	}

	public StateStatic(State parent) {
		super(parent);
	}

	@Override
	public int compareTo(State st) {
		if (st == null)
			return 1;
		if (getClass() != st.getClass())
			return -1;
		int res = st.getStorage().compareTo(this.getStorage());
		if (res != 0)
			return res;
		res = st.getId() > this.getId() ? -1 : 1;
		List<Integer> ne =Topology.topologies.get(0).getNeighbors(this.getId());
		List<Integer> ne_other = Topology.topologies.get(0).getNeighbors(st.getId());
		if (ne.size() != ne_other.size()) {
			if (ne.size() < ne_other.size())
				return -1;
			else
				return 1;
		} else {
			for (int i = 0; i < ne.size(); i++) {
				res = ne.get(i).compareTo(ne_other.get(i));
				if (res != 0)
					return res;
			}
		}
		return res;
	}

	@Override
	public State deepCopy() {
		StateStatic copied = new StateStatic(this.storage.deepCopy());
		copied.setId(this.getId());
		copied.storage = this.storage.deepCopy();

		return copied;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((storage == null) ? 0 : storage.hashCode());
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
		StateStatic other = (StateStatic) obj;
		for (int i = 0; i < Topology.getRebecs_count(); i++) {
			if (Topology.topologies.get(0).getConnections(this.getId()).get(i) != Topology.topologies.get(0)
					.getConnections(other.getId()).get(i)) {
				if (i == other.getId() || i == this.getId()) {
					if (i == other.getId() && Topology.topologies.get(0).getConnections(this.getId())
							.get(i) != Topology.topologies.get(0).getConnections(other.getId()).get(this.getId()))
						return false;
					if (i == this.getId() && Topology.topologies.get(0).getConnections(this.getId())
							.get(other.getId()) != Topology.topologies.get(0).getConnections(other.getId()).get(i))
						return false;
				} else
					return false;
			}
		}
		if (storage == null) {
			if (other.storage != null)
				return false;
		} else if (!storage.equals(other.storage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = "State [" + storage.toString();
		return str + "]";
	}
}