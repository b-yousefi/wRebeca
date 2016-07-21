package rebeca.wrebeca.common.topologyConstraint;

import java.util.ArrayList;
/**
 * @author Behnaz Yousefi
 *
 */

import java.util.HashSet;
import java.util.List;


public class possible_topologies extends ChangeTopologies {
	static HashSet<Topology>  final_tops = new HashSet<Topology>();
	public static List<Integer> set_cons;
//	public static Map<Integer, List<BitSet>> node_top;	
    public static Topology consts;
    private static int rebecs_count;
	private boolean first_ = false;

	public possible_topologies()
	{
       
        
        
	}
	
	public static int getRebecs_count() {
		return rebecs_count;
	}


	public static void setRebecs_count(int rebecs_count) {
		possible_topologies.rebecs_count = rebecs_count;
		 consts = new Topology(rebecs_count);
		 set_cons = new ArrayList<Integer>();
	}
	
    public static void set_Constraint(int nod1, int nod2, boolean value)
    {
    	if(value)
    	{
    		consts.getConnections(nod1).set(nod2);
    		consts.getConnections(nod2).set(nod1);
    	}
    	else
    	{
    		consts.getConnections(nod1).clear(nod2);
    		consts.getConnections(nod2).clear(nod1);
    	}
        if (nod1 < nod2)
            set_cons.add(nod1 * rebecs_count + nod2);
        else
            set_cons.add(nod2 * rebecs_count + nod1);
    }
	@Override
	public HashSet<Topology> changeTopology(Topology current_top) {
		if (first_ == false) {
			first_ = true;
			compute_all_possible_tops();
		}
		HashSet<Topology> final_topls = new HashSet<Topology>();
		for (Topology item : final_tops) {
			if (!item.equals(current_top))
				final_topls.add(item);
		}
		return final_topls;

	}
//    public void possible_topology()
//    {
//    	List<BitSet> tops = new ArrayList<BitSet>();
//        //node_top = new HashMap<Integer, List<BitSet>>();
//        List<BitSet> all_permutation = new ArrayList<BitSet>();
//        BitSet bi=new BitSet(rebecs_count);
//        bi.clear();
//        all_permutation.add(bi);
//        for (int i = 0; i < rebecs_count; i++)
//        {
//            List<BitSet> temp = new ArrayList<BitSet>(all_permutation);
//            for(BitSet conn : temp)
//            {
//            	
//            	BitSet new_top = new BitSet();
//            	new_top=conn;
//                new_top.set(i);
//                all_permutation.add(new_top);
//            }
//        }
//        for (int i = 0; i < rebecs_count; i++)
//        {
//        	 List<BitSet> dd= new ArrayList<BitSet>();
//        	 dd.addAll(all_permutation);
//            node_top.put(i,dd);
//        }
//    }
	//@Override
    public static void compute_all_possible_tops()
    {
    	final_tops.add(consts);
        for (int i = 0; i < rebecs_count; i++)
        {
            for (int j = i + 1; j < rebecs_count; j++)
            {
                if (!possible_topologies.set_cons.contains(i * rebecs_count + j))
                {               	
                	HashSet<Topology> temp = new HashSet<Topology>();
                	
                    for (Topology top : final_tops)
                    {
                    	Topology tempTop=new Topology(top);
                    	tempTop.getConnections(i).set(j);
                    	tempTop.getConnections(j).set(i);
                        if (i < j)
                            set_cons.add(i * rebecs_count + j);
                        else
                            set_cons.add(j * rebecs_count + i);   
                        temp.add(tempTop);
                    }
                    final_tops.addAll(temp);
                }
            }
        }
    }
	@Override
	public HashSet<Topology> all_possibleTopologies() {
		if (final_tops.size() == 0)
			compute_all_possible_tops();
		return final_tops;
	}

//	public HashSet<Topology> possible_topology() {
//		// int counter = 0;
//		int all_bits = (int) Math.pow(2, rebec_count);
//		BitSet temp = new BitSet(all_bits);
//		temp.clear();
//		Topology top = new Topology(rebec_count);
//		double x = (((rebec_count * rebec_count) - rebec_count) / 2);
//		double top_count = Math.pow(2, x);
//		for (int i = 1; i <= Math.pow(2, all_bits); i++) {
//			int round = 0;
//			int k = 0;
//			while (round != rebec_count) {
//				top.setConnections(round, round, true);// = true;
//				for (int kk = round + 1; kk < rebec_count; kk++) {
//					if ((i % Math.pow(2, k)) == 0) {
//						if (top.getConnections(round).get(kk) == false) {
//							top.setConnections(round, kk, true);
//							top.setConnections(kk, round, true);
//						} else {
//							top.setConnections(round, kk, false);
//							top.setConnections(kk, round, false);
//						}
//					}
//					k++;
//				}
//				round++;
//				k = round * rebec_count;
//			}
//
//			if (!final_tops.contains(top) && satisfy_constraints(top)) {
//				final_tops.add(new Topology(top));
//			}
//			if (final_tops.size() == top_count)
//				break;
//		}
//		return final_tops;
//	}

	public boolean satisfy_constraints(Topology topl) {
		return false;
	}



}
