package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import rebeca.timed.MessageTime;
import rebeca.timed.StateClassicTime;
import rebeca.timed.StateTime;

import rebeca.wrebeca.common.topologyConstraint.Topology;
import rebeca.wrebeca.dynamicNetwork.glStateDynamicWithTau;
import rebeca.wrebeca.staticNetwork.StateStatic;

public class StateSpaceBuilder {

    private static StateSpaceBuilder instance;
    private final ThreadPoolExecutor pool;
    private int allInitializedState = -1;
    private boolean stopped = false;
    final boolean actl;
    final boolean mcrl;
    final boolean lts;
    final boolean time = true;
    final boolean gradually = false;
    private final Timer timer;
    private static final List<Method> INVARIANTS;
    

    public static StateSpaceBuilder getInstance() {
        return instance;
    }

    protected StateSpaceBuilder(boolean actl_, boolean mcrl_, boolean lts_, int max_thread) {
        actl = actl_;
        mcrl = mcrl_;
        lts = lts_;
        pool = new ThreadPoolExecutor(max_thread/2, max_thread!=0?max_thread:200, 2L, TimeUnit.DAYS, new ArrayBlockingQueue<>(1000000));
        timer = new Timer();

        
        instance=this;
    }

    static {
        INVARIANTS = new ArrayList<>();
    }

    public void Initialize(GlobalState gl, boolean defaultTop)
            throws IOException {
        if (defaultTop) {
            VisitedGlobalstates.getInstance().insert(gl);
            List<Integer> tops = new ArrayList<>();
            tops.add(gl.getTop());
            init(gl, tops);

        } else {
            VisitedGlobalstates.getInstance().insert(gl);
            List<Integer> tops = new ArrayList<>();
            tops.addAll(Topology.topologies.keySet());
            init(gl, tops);
        }
        TimerTask printNumStates = new TimerTask() {
            @Override
            public void run() {
                System.out.println("the number of states: " + Trans.getInstance().getNumOfStates());
                System.out.println("the number of transitions:" + Trans.getInstance().getNumOfTransitions());
                if(isTerminated()){
                    System.out.println("******************State space is created******************");
                    System.out.println("the final number of states: " + Trans.getInstance().getNumOfStates());
                    System.out.println("the final number of transitions:" + Trans.getInstance().getNumOfTransitions());                   
                    System.out.println("The output files are stored in the following path: " + Trans.getInstance().getOutputPath() + "\\Output");
                    complete_building();
                    timer.cancel();
                }
            }
        };
        timer.schedule(printNumStates, 4000, 4 * 1000);
    }

    public void init(GlobalState gl, List<Integer> initTopls) throws IOException {
        int st_des, st_source;
        st_source = VisitedGlobalstates.getInstance().get_stNumber(gl);

        for (int top : initTopls) {
            for (State current_st : gl.getStates()) {
                if (!current_st.getStorage().hasInitMessage()) {
                    continue;
                }
                Message initMsg = current_st.getStorage().getInitialMessage();
                Map<State, List<Message>> new_msg_st;
                State s = current_st.deepCopy();
                s.getStorage().remove(initMsg);
                new_msg_st = message_handler(s, initMsg, top);
                if (new_msg_st == null) {
                    continue;
                }
                for (State new_st : new_msg_st.keySet()) {
                    GlobalState temp = gl.deepCopy();
                    temp.remove_lState(current_st);
                    temp.add_lState(new_st);
                    // there is a message that should be broadcast
                    if (!new_msg_st.get(new_st).isEmpty()) {
                        temp.send_messages(new_msg_st.get(new_st));
                    }
                    String label = labelBuilder(current_st, top, initMsg);
                    st_des = VisitedGlobalstates.getInstance().get_stNumber(temp);

                    if ((st_des) == -1) {
                        st_des = VisitedGlobalstates.getInstance().insert(temp);
                        Trans.getInstance().add_transition(st_source,st_des,label);
                        if (temp.hasAnyInitMessage()) {
                            init(temp, initTopls);
                        } else if (!gradually) {
                            allInitializedState = st_des;
                            put_work(temp);
                        }
                    } else {
                        Trans.getInstance().add_transition(st_source,st_des,label);
                    }
                }
            }
        }

    }

    public void complete_building() {
        // Output output_builder = new Output();
        if (mcrl) {
            try {
                Trans.getInstance().write_mcrl2(VisitedGlobalstates.getInstance(), allInitializedState);
            } catch (IOException e1) {
            }
        }
        if (lts) {
            try {
                Trans.getInstance().write_aut(false);
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }
        timer.cancel();
        System.exit(0);
    }

    public Map<State, List<Message>> message_handler(State st, Message m, int top) {
        // do sth based on m
        Map<State, List<Message>> result = null;
        return result;
        // update m to the value that you want to broadcast otherwise set m to
        // null
    }

    public String labelBuilder(State current_st, int top, Message msg) {
        String label;
        if (current_st instanceof StateStatic) {
            label = msg.getMethodID() + "(";
        } else {
            label = current_st.getId() + ":" + msg.getMethodID() + "(";
        }
        label += msg.getMsgArgs().toString() + " )";
        if (actl) {
            label = Topology.topologies.get(top).connectionInfo(current_st.getId(), msg.getRecID()) + ", " + msg.getMethodID();
        } 
        if(current_st instanceof StateClassicTime && msg instanceof MessageTime) {
            label = "[("+"rebec"+current_st.getId()+"."+ label + "," + ((MessageTime)msg).getArrival() + "," + 
                    ((MessageTime)msg).getDeadline() + "),"+ ((StateClassicTime)current_st).getLocalTime() +"]";
        }
        if(current_st instanceof StateTime && msg instanceof MessageTime) {
            label = "[("+"rebec"+current_st.getId()+"."+ label + "," + ((MessageTime)msg).getArrival() + "," + 
                    ((MessageTime)msg).getDeadline() + "),"+ ((StateTime)current_st).getLocalTime() +"]";
        }
        return label;
    }

    public static List<Method> getInvariants() {
        return INVARIANTS;
    }

    public static void addInvariant(Method invariant) {
        invariant.setAccessible(true);
        INVARIANTS.add(invariant);

    }

    public void put_work(GlobalState states) {
        if(stopped)
            return ;
        int st_source = VisitedGlobalstates.getInstance().get_stNumber(states);
        for (int item : Topology.topologies.keySet()) {
            if(stopped)
                return ;
            GlobalState temp = states.deepCopy();
            temp.setTop(item);
            if (states.getClass() == glStateDynamicWithTau.class) {
                int st_des = VisitedGlobalstates.getInstance().get_stNumber(temp);
                if ((st_des) == -1) {
                    st_des = VisitedGlobalstates.getInstance().insert(temp);                    
                    Trans.getInstance().add_transition(st_source,st_des,"tau");
                    put_work(temp);

                } else if (temp.getTop() == states.getTop()) {
                    createNewFork(temp, st_source);
                } else {
                    Trans.getInstance().add_transition(st_source,st_des,"tau");
                }

            } else {
                createNewFork(temp, st_source);
            }
        }
    }

    public void createNewFork(GlobalState gl, int source) {
        if(!stopped){
            GlobalStateHandler th = new GlobalStateHandler(gl, source);
            pool.execute(th);
            //th.run();
        }

    }

    public boolean isTerminated() {
        return pool.getActiveCount() == 0 && pool.getQueue().isEmpty();
    }
    
    public void setStop(){
        stopped= true;
    } 
}
