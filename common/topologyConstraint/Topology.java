package wRebeca.common.topologyConstraint;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Topology {
	private BitSet[] connections;
	private static int rebecs_count;
	private ChangeTopologies changeTop;
	public static Map<Integer, Topology> topologies;

	// Constructor
	public Topology(int rebec_count_, ChangeTopologies strategy_) {

		rebecs_count = rebec_count_;
		this.changeTop = strategy_;
		this.changeTop.rebec_count = rebec_count_;
		connections = new BitSet[rebecs_count];
		// HashSet<int> dont_cares;
		for (int i = 0; i < rebecs_count; i++) {
			connections[i] = new BitSet(rebecs_count);
			connections[i].clear();
			connections[i].set(i);
		}
		int c = 0;
		HashSet<Topology> tt = changeTop.all_possibleTopologies();
		topologies = new HashMap<Integer, Topology>();
		for (Topology top : tt) {
			topologies.put(c++, top);
		}
	}

	public String connectionInfo(Integer stID) {
		String str = "{";
		for (Integer i = 0; i < rebecs_count; i++) {
			if (connections[stID].get(i)) {
				str += stID.toString() + " ->" + i.toString() + " ,";
			} else {
				str += stID.toString() + " -\\>" + i.toString() + " ,";
			}

		}
		str = str.substring(0, str.length() - 1) + " }";
		return str;
	}

	public HashSet<Topology> get_all_tops(Topology top_) {
		return changeTop.changeTopology(top_);
	}

	public HashSet<Topology> get_Topologies() {
		return changeTop.all_possibleTopologies();
	}

	public static int getRebecs_count() {
		return rebecs_count;
	}

	public void setRebecs_count(int count) {
		rebecs_count = count;
	}

	public BitSet[] getConnections() {
		return connections;
	}

	public BitSet getConnections(int index) {
		return connections[index];
	}

	public List<Integer> getNeighbors(int index) {
		List<Integer> neighbors = new ArrayList<Integer>();
		for (int i = 0; i < Topology.getRebecs_count(); i++) {
			if (this.getConnections(index).get(i))
				neighbors.add(i);
		}
		return neighbors;
	}

	public void setConnections(int node1, int node2, boolean value) {
		if (value) {
			connections[node1].set(node2);
			// connections[node2].set(node1);
		} else {
			connections[node1].clear(node2);
			// connections[node2].clear(node1);
		}

	}

	public Topology(int count) {
		rebecs_count = count;
		connections = new BitSet[rebecs_count];
		// HashSet<int> dont_cares;
		for (int i = 0; i < rebecs_count; i++) {
			connections[i] = new BitSet(rebecs_count);
			connections[i].clear();
			connections[i].set(i);
		}

	}

	public Topology(Topology top) {
		this.connections = new BitSet[Topology.rebecs_count];
		Topology.rebecs_count = top.connections.length;
		for (int i = 0; i < Topology.rebecs_count; i++) {
			this.connections[i] = new BitSet(Topology.rebecs_count);
			this.connections[i].clear();
			this.setConnections(i, i, true);
			for (int j = 0; j < Topology.rebecs_count; j++) {
				if (top.connections[i].get(j))
					this.setConnections(i, j, true);
				else
					this.setConnections(i, j, false);
			}
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(connections);
		result = prime * result + rebecs_count;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// NOT For Static!!!!!!!!!!!!!!!!!!!!!!!!
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Topology other = (Topology) obj;
		if (!Arrays.equals(connections, other.connections))
			return false;
		// if (rebecs_count != Topology.rebecs_count)
		// return false;
		return true;
	}

	public boolean connected(int rebec1, int rebec2) {
		if (this.connections[rebec1].get(rebec2) == true && this.connections[rebec2].get(rebec1) == true) // direct
																											// connection
		{
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String str = "Topology [connections=";
		for (BitSet b : connections) {
			str += ";" + b.toString();
		}
		str += ", rebecs_count=" + rebecs_count + "]";
		return str;
	}

}
