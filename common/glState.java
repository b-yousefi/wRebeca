package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */

import java.util.ArrayList;
import java.util.List;

import wRebeca.common.topologyConstraint.Topology;

public abstract class glState {
    public abstract void clearGlobal_state();
    
    public abstract int getTop();
    
    public abstract void setTop(int top_);
    
    public abstract List<State> getStates();
	
    public abstract int get_number_of_states(Class<?> t);

    public abstract void add_lState(State local_st);
    public abstract void remove_lState(State local_st);

    public abstract void clear();

    public abstract void broadcast(message msg);
	
    public abstract void groupcast(message msg);


    public abstract boolean unicast(message msg);

	public abstract int hashCode();

	public abstract boolean equals(Object obj) ;

    public abstract glState deepCopy();
    
	public abstract String toString();
	
	public void send_messages(List<message> msgs)
	{
		for (message mm : msgs)
        {
			if(mm.getRecID().size()==0)
				this.broadcast(mm);
			else
			{
					if (mm.getRecID().size() > 1)
						this.groupcast(mm);
					else
						this.unicast(mm);
			}
		}
	}

	public boolean hasAnyInitMessage(){
		return this.getStates().stream().anyMatch(x->x.getStorage().hasInitMessage()==true);
	}


   public List<Integer> get_reachable_nodes(int self)
   {
       List<Integer> neighboures = new ArrayList<Integer>();
       for (int i = 0; i < Topology.topologies.get(this.getTop()).getConnections(self).size(); i++)
       {
           if (Topology.topologies.get(this.getTop()).getConnections(self).get(i) == true && i!=self)
               neighboures.add(i);
       }

       return neighboures;
   }
    
}