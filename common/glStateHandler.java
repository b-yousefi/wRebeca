package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

@SuppressWarnings("serial")
public class glStateHandler extends RecursiveAction {
	public glStateHandler(glState myglState,stateSpaceBuilder builder, int source)
	{
		gl=myglState;
		stBuilder=builder;
		st_source=source;
	}
	private glState gl;
	private stateSpaceBuilder stBuilder;
	private int st_source;
	@Override
	protected void compute() {
        int st_des;
        for (State current_st : gl.getStates())
        {
            if(current_st.getStorage().getSize()!=0)
            {                   
                List<message> items = current_st.getStorage().getNaxt();
                for(message item :items)
                {
                    Map<State, List<message>> new_msg_st = new HashMap<State, List<message>>();
                    State s = current_st.deepCopy(); 
                    s.getStorage().remove(item);
                    new_msg_st = stBuilder.message_handler(s, item,gl.getTop());
                    if (new_msg_st == null)
                        continue;
                	for (State new_st : new_msg_st.keySet())
                    {
                        glState temp =gl.deepCopy();
                        temp.remove_lState(current_st);
                        temp.add_lState(new_st);
                        if (new_msg_st.get(new_st).size() != 0)  //there is a message that should be broadcast 
                        {
                           temp.send_messages(new_msg_st.get(new_st));
                        }   
                        String label=stBuilder.labelBuilder(current_st,gl.getTop(), item);
	                    st_des = stBuilder.hash.get_stNumber(temp);
							
	                        if ((st_des ) == -1)
	                        {
                                st_des = stBuilder.hash.insert(temp);
                                trans.add_st(st_des);
		                        trans lb = new trans(st_des, label);
		                        lb.add_transition(st_source);
                                if (!stBuilder.gradually)
									try {
										stBuilder.put_work(temp);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}		
	                        }
	                        else
	                        {
		                        trans lb = new trans(st_des, label);
		                        lb.add_transition(st_source);
	                        }
	                    }	                    
                }
            }
         }
	}

}
