package rebeca.wrebeca.common.topologyConstraint;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.HashSet;

abstract class ChangeTopologies {
	public int rebec_count;

	public abstract HashSet<Topology> changeTopology(Topology top);

	public abstract HashSet<Topology> all_possibleTopologies();
}
