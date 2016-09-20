package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import static rebeca.wrebeca.common.StateSpaceBuilder.getInvariants;

public class GlobalStateHandler implements Runnable {

    public GlobalStateHandler(GlobalState myglState, int source) {
        gl = myglState;
        st_source = source;
    }
    private final GlobalState gl;
    private final int st_source;

    @Override
    public void run() {
        int st_des;
        for (State current_st : gl.getStates()) {
            if (current_st.getStorage().getSize() != 0) {
                for (Message item : current_st.getStorage().getNaxt()) {
                    Map<State, List<Message>> new_msg_st;
                    State s = current_st.deepCopy();
                    s.getStorage().remove(item);
                    new_msg_st = StateSpaceBuilder.getInstance().message_handler(s, item, gl.getTop());
                    if (new_msg_st != null) {
                        for (State new_st : new_msg_st.keySet()) {
                            GlobalState temp = gl.deepCopy();
                            temp.remove_lState(current_st);
                            temp.add_lState(new_st);
                            if (!new_msg_st.get(new_st).isEmpty()) //there is a message that should be broadcast 
                            {
                                temp.send_messages(new_msg_st.get(new_st));
                            }
                            String label = StateSpaceBuilder.getInstance().labelBuilder(current_st, gl.getTop(), item);
                            st_des = VisitedGlobalstates.getInstance().get_stNumber(temp);

                            if ((st_des) == -1) {
                                st_des = VisitedGlobalstates.getInstance().insert(temp);
                                Trans.add_st(st_des);
                                Trans lb = new Trans(st_des, label);
                                lb.add_transition(st_source);
                                if (!StateSpaceBuilder.getInstance().gradually && check_invariants(temp)) {
                                    StateSpaceBuilder.getInstance().put_work(temp);
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

    public boolean check_invariants(GlobalState gl) {

        boolean holds = true;
        if (getInvariants() != null) {
            for (Method m : getInvariants()) {

                try {
                    Object var;
                    Object[] arguments = new Object[]{gl, null};
                    var = m.invoke(StateSpaceBuilder.getInstance(), arguments);
                    if ((boolean) var) {
                        holds = false;
                        StateSpaceBuilder.getInstance().setStop();
                        Trans.printCounterExample(gl, st_source);
                        StateSpaceBuilder.getInstance().complete_building();
                        break;

                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                }
            }
        }
        return holds;
    }
}
