package rebeca.wrebeca.common.topologyConstraint;
//package wRebeca.common.topologyConstraint;
//import java.util.BitSet;
//import java.util.HashSet;
//
//    public class possible_topologies extends ChangeTopologies
//    {
//        HashSet<Topology> final_tops = new HashSet<Topology>();
//        private boolean first_ = false;
//        @Override
//        public  HashSet<Topology> changeTopology(Topology current_top)
//        {
//            if (first_ == false)
//            {
//                first_ = true;
//                this.possible_topology();
//            }
//            HashSet<Topology> final_topls = new HashSet<Topology>();
//            for (Topology  item : final_tops)
//            {
//                if (!item.equals(current_top))
//                    final_topls.add(item);
//            }
//            return final_topls;
//
//        }
//        @Override
//        public  HashSet<Topology> all_possibleTopologies()
//        {
//            if (final_tops.size() == 0)
//                possible_topology();
//            return final_tops;
//        }
//
//
//        public HashSet<Topology> possible_topology()
//        {
//            //int counter = 0;
//            int all_bits = (int)Math.pow(2, rebec_count);
//            BitSet temp = new BitSet(all_bits);
//            temp.clear();
//            Topology top = new Topology(rebec_count);
//            double x = (((rebec_count * rebec_count) - rebec_count) / 2);
//            double top_count = Math.pow(2, x);
//            for (int i = 1; i <= Math.pow(2, all_bits); i++)
//            {
//                int round = 0;
//                int k = 0;
//                while (round != rebec_count)
//                {
//                    top.setConnections(round,round,true);// = true;
//                    for (int kk = round + 1; kk < rebec_count; kk++)
//                    {
//                        if ((i % Math.pow(2, k)) == 0)
//                        {
//                            if (top.getConnections(round).get(kk) == false)
//                            {
//                            	top.setConnections(round,kk,true);
//                            	top.setConnections(kk,round,true);
//                            }
//                            else
//                            {
//                            	top.setConnections(round,kk,false);
//                            	top.setConnections(kk,round,false);
//                            	//top.getConnections(round).clear(kk);
//                            	//top.getConnections(kk).clear(round);
//                            }
//                        }
//                        k++;
//                    }
//                    round++;
//                    k = round * rebec_count;
//                }
//               // counter++;
//
//                if (!final_tops.contains(top) && satisfy_constraints(top))
//                {
//                    final_tops.add(new Topology(top));
//                }
//                if (final_tops.size() == top_count)
//                    break;
//            }
//            //////////////////for test//////////////////////
//            //final_tops.Clear();
//            //Topology top2 = new Topology(rebec_count);
//            //top2.Connections[0][0] = true;
//            //top2.Connections[1][1] = true;
//            //top2.Connections[2][2] = true;
//            //top2.Connections[3][3] = true;
//            //top2.Connections[0][1] = true;
//            //top2.Connections[1][0] = true;
//            ////top2.Connections[2][1] = true;
//            ////top2.Connections[1][2] = true;
//            ////top2.Connections[2][3] = true;
//            ////top2.Connections[3][2] = true;
//            //top2.Connections[0][3] = true;
//            //top2.Connections[3][0] = true;
//            //final_tops.Add(top2);
//            ////top2 = new Topology(rebec_count);
//            //////top2.Connections[0][0] = true;
//            //////top2.Connections[1][1] = true;
//            ////top2.Connections[2][2] = true;
//            ////top2.Connections[3][3] = true;
//            ////top2.Connections[0][1] = true;
//            ////top2.Connections[1][0] = true;
//            ////top2.Connections[2][1] = true;
//            ////top2.Connections[1][2] = true;
//            ////top2.Connections[2][3] = true;
//            ////top2.Connections[3][2] = true;
//            //////top2.Connections[0][3] = true;
//            //////top2.Connections[3][0] = true;
//            ////final_tops.Add(top2);
//
//
//            //List<string> strs = new List<string>();
//            //foreach (Topology item2 in final_tops)
//            //{
//            //    string str = "";
//            //    for (int i = 0; i < item2.Connections.Length; i++)
//            //    {
//            //        for (int j = 0; j < item2.Connections.Length; j++)
//            //            str += item2.Connections[i][j].ToString() + ",";
//            //    }
//            //    strs.Add(str);
//            //}
//            //final_tops.Remove(final_tops.First());
//            return final_tops;
//        }
//
//        public  boolean satisfy_constraints(Topology topl)
//        {
//            return false;
//        }
//    }
