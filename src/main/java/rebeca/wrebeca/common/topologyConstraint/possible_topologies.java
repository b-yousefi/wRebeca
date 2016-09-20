package rebeca.wrebeca.common.topologyConstraint;

import java.util.ArrayList;
/**
 * @author Behnaz Yousefi
 *
 */

import java.util.HashSet;
import java.util.List;

public class possible_topologies extends ChangeTopologies {

    private static HashSet<Topology> final_tops = new HashSet<>();
    private static List<Integer> set_cons;
    private static Topology consts;
    private static int rebecs_count;
    private boolean first_ = false;

    public possible_topologies() {
    }

    public static int getRebecs_count() {
        return rebecs_count;
    }

    public static void setRebecs_count(int rebecs_count) {
        possible_topologies.rebecs_count = rebecs_count;
        consts = new Topology(rebecs_count);
        set_cons = new ArrayList<>();
    }

    public static void set_Constraint(int nod1, int nod2, boolean value) {
        if (value) {
            consts.getConnections(nod1).set(nod2);
            consts.getConnections(nod2).set(nod1);
        } else {
            consts.getConnections(nod1).clear(nod2);
            consts.getConnections(nod2).clear(nod1);
        }
        if (nod1 < nod2) {
            set_cons.add(nod1 * rebecs_count + nod2);
        } else {
            set_cons.add(nod2 * rebecs_count + nod1);
        }
    }

    @Override
    public HashSet<Topology> changeTopology(Topology current_top) {
        if (first_ == false) {
            first_ = true;
            compute_all_possible_tops();
        }
        HashSet<Topology> final_topls = new HashSet<>();
        for (Topology item : final_tops) {
            if (!item.equals(current_top)) {
                final_topls.add(item);
            }
        }
        return final_topls;

    }
    
    public static void compute_all_possible_tops() {
        final_tops.add(consts);
        for (int i = 0; i < rebecs_count; i++) {
            for (int j = i + 1; j < rebecs_count; j++) {
                if (!possible_topologies.set_cons.contains(i * rebecs_count + j)) {
                    HashSet<Topology> temp = new HashSet<Topology>();

                    for (Topology top : final_tops) {
                        Topology tempTop = new Topology(top);
                        tempTop.getConnections(i).set(j);
                        tempTop.getConnections(j).set(i);
                        if (i < j) {
                            set_cons.add(i * rebecs_count + j);
                        } else {
                            set_cons.add(j * rebecs_count + i);
                        }
                        temp.add(tempTop);
                    }
                    final_tops.addAll(temp);
                }
            }
        }
    }

    @Override
    public HashSet<Topology> all_possibleTopologies() {
        if (final_tops.isEmpty()) {
            compute_all_possible_tops();
        }
        return final_tops;
    }
    public boolean satisfy_constraints(Topology topl) {
        return false;
    }

}
