package wRebeca.common.topologyConstraint;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.BitSet;
import java.util.HashSet;

public class possible_topologies extends ChangeTopologies {
	HashSet<Topology> final_tops = new HashSet<Topology>();
	private boolean first_ = false;

	@Override
	public HashSet<Topology> changeTopology(Topology current_top) {
		if (first_ == false) {
			first_ = true;
			this.possible_topology();
		}
		HashSet<Topology> final_topls = new HashSet<Topology>();
		for (Topology item : final_tops) {
			if (!item.equals(current_top))
				final_topls.add(item);
		}
		return final_topls;

	}

	@Override
	public HashSet<Topology> all_possibleTopologies() {
		if (final_tops.size() == 0)
			possible_topology();
		return final_tops;
	}

	public HashSet<Topology> possible_topology() {
		// int counter = 0;
		int all_bits = (int) Math.pow(2, rebec_count);
		BitSet temp = new BitSet(all_bits);
		temp.clear();
		Topology top = new Topology(rebec_count);
		double x = (((rebec_count * rebec_count) - rebec_count) / 2);
		double top_count = Math.pow(2, x);
		for (int i = 1; i <= Math.pow(2, all_bits); i++) {
			int round = 0;
			int k = 0;
			while (round != rebec_count) {
				top.setConnections(round, round, true);// = true;
				for (int kk = round + 1; kk < rebec_count; kk++) {
					if ((i % Math.pow(2, k)) == 0) {
						if (top.getConnections(round).get(kk) == false) {
							top.setConnections(round, kk, true);
							top.setConnections(kk, round, true);
						} else {
							top.setConnections(round, kk, false);
							top.setConnections(kk, round, false);
						}
					}
					k++;
				}
				round++;
				k = round * rebec_count;
			}
			// counter++;

			if (!final_tops.contains(top) && satisfy_constraints(top)) {
				final_tops.add(new Topology(top));
			}
			if (final_tops.size() == top_count)
				break;
		}
		return final_tops;
	}

	public boolean satisfy_constraints(Topology topl) {
		return false;
	}
}
