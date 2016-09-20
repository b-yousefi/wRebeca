package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalStateHandler implements Runnable {

    public GlobalStateHandler(GlobalState myglState, StateSpaceBuilder builder, int source) {
        gl = myglState;
        stBuilder = builder;
        st_source = source;
    }
    private GlobalState gl;
    private StateSpaceBuilder stBuilder;
    private int st_source;

    @Override
    public void run() {
        int st_des;
        for (State current_st : gl.getStates()) {
            if (current_st.getStorage().getSize() != 0) {
                List<Message> items = current_st.getStorage().getNaxt();
                for (Message item : items) {
                    Map<State, List<Message>> new_msg_st = new HashMap<State, List<Message>>();
                    State s = current_st.deepCopy();
                    s.getStorage().remove(item);
                    new_msg_st = stBuilder.message_handler(s, item, gl.getTop());
                    if (new_msg_st != null) {
                        for (State new_st : new_msg_st.keySet()) {
                            GlobalState temp = gl.deepCopy();
                            temp.remove_lState(current_st);
                            temp.add_lState(new_st);
                            if (new_msg_st.get(new_st).size() != 0) //there is a message that should be broadcast 
                            {
                                temp.send_messages(new_msg_st.get(new_st));
                            }
                            String label = stBuilder.labelBuilder(current_st, gl.getTop(), item);
                            st_des = stBuilder.hash.get_stNumber(temp);

                            if ((st_des) == -1) {
                                st_des = stBuilder.hash.insert(temp);
                                Trans.add_st(st_des);
                                Trans lb = new Trans(st_des, label);
                                lb.add_transition(st_source);
                                if (!stBuilder.gradually) {
                                    try {
                                        stBuilder.put_work(temp);
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Trans lb = new Trans(st_des, label);
                                lb.add_transition(st_source);
                            }
                        }
                    }
                }
            }
        }
    }

}
