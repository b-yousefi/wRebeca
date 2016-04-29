package wRebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ForkJoinPool;

import wRebeca.common.topologyConstraint.Topology;
import wRebeca.dynamicNetwork.glStateDynamicWithTau;

public class stateSpaceBuilder {
	public visitedGlstates hash;
	private ForkJoinPool pool;
	private boolean stop = false;
	private int allInitializedState = -1;
	boolean actl;
	boolean mcrl;
	boolean lts;
	boolean gradually = false;
	protected Timer timer;
    //final int max_thread=64;
	public stateSpaceBuilder(boolean actl_, boolean mcrl_, boolean lts_,int max_thread) {
		actl = actl_;
		mcrl = mcrl_;
		lts = lts_;
		
		setPool(new ForkJoinPool(max_thread));
		timer = new Timer();
		TimerTask printNumStates = new TimerTask() {
			@Override
			public void run() {
				if (hash.tablee != null)
					System.out.println("number of reached states: " + hash.table_size().toString());
			}
		};
		timer.schedule(printNumStates, 2000, 4 * 1000);
	}

	public void Initialize(glState gl, wRebeca.common.topologyConstraint.Topology topls, boolean defaultTop)
			throws IOException {
		if (gl.getTop() != -1) {
			hash = new visitedGlstates(0);
			hash.insert(gl);
			List<Integer> tops = new ArrayList<Integer>();
			tops.add(gl.getTop());
			init(gl, tops);

		} else {
			hash = new visitedGlstates(0);
			// trans.add_st(0);
			hash.insert(gl);
			List<Integer> tops = new ArrayList<Integer>();
			tops.addAll(Topology.topologies.keySet());
			init(gl, tops);
		}

	}

	public void init(glState gl, List<Integer> initTopls) throws IOException {
		int st_des, st_source;
		st_source = hash.get_stNumber(gl);

		for (int top : initTopls) {
			// gl.setTop(top);
			for (State current_st : gl.getStates()) {
				if (stop)
					break;
				if (!current_st.getStorage().hasInitMessage())
					continue;
				message initMsg = current_st.getStorage().getInitialMessage();
				Map<State, List<message>> new_msg_st = new HashMap<State, List<message>>();
				State s = current_st.deepCopy();
				s.getStorage().remove(initMsg);
				new_msg_st = message_handler(s, initMsg, top);
				if (new_msg_st == null)
					continue;
				for (State new_st : new_msg_st.keySet()) {
					glState temp = gl.deepCopy();
					temp.remove_lState(current_st);
					temp.add_lState(new_st);
					if (new_msg_st.get(new_st).size() != 0) // there is a
															// message that
															// should be
															// broadcast
					{
						temp.send_messages(new_msg_st.get(new_st));
					}
					String label = labelBuilder(current_st, top, initMsg);
					st_des = hash.get_stNumber(temp);

					if ((st_des) == -1) {
						st_des = hash.insert(temp);
						trans.add_st(st_des);
						trans lb = new trans(st_des, label);
						lb.add_transition(st_source);
						if (temp.hasAnyInitMessage()) {
							init(temp, initTopls);
						} else {
							if (!gradually) {
								allInitializedState = st_des;
								put_work(temp);
							}
						}
					}

				}
			}
		}

	}

	public Integer complete_building() {
		// Output output_builder = new Output();
		if (mcrl)
			try {
				trans.write_mcrl2(this.hash, allInitializedState);
			} catch (IOException e1) {
				// TODO Au to-generated catch block
				e1.printStackTrace();
			}
		if (lts)
			try {
				trans.write_aut(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return hash.table_size();
	}

	public Map<State, List<message>> message_handler(State st, message m, int top) {
		// do sth based on m

		Map<State, List<message>> result = null;
		return result;
		// update m to the value that you want to broadcast otherwise set m to
		// null
	}

	public String labelBuilder(State current_st, int top, message msg) {
		String label;
		if (Topology.topologies.size() != 1)
			label = current_st.getId() + ":" + msg.getMethodID() + "(";
		else
			label = msg.getMethodID() + "(";
		label += msg.getMsgArgs().toString() + " )";
		if (actl)
			label = Topology.topologies.get(top).connectionInfo(current_st.getId(),msg.getRecID()) + ", " + msg.getMethodID();
		return label;
	}

	private static List<Method> invariants;
	static {
		invariants = new ArrayList<Method>();
	}

	public static List<Method> getInvariants() {
		return invariants;
	}

	public static void addInvariant(Method invariant) {
		invariant.setAccessible(true);
		invariants.add(invariant);

	}

	public  synchronized  boolean check_invariants(glState gl, int source_) throws IOException {
		
		boolean holds = true;
		if (getInvariants() != null) {
			for (Method m : getInvariants()) {
				
				try {
					Object var;
					synchronized(m)
					{
						Object[] arguments = new Object[] { gl, null };
					
						 var = m.invoke(this, arguments);
					}
					if ((boolean) var) {
						holds = false;
						if(stop==false)
						{
							stop = true;
						
						trans.printCounterExample(gl, source_, hash);				
						}
						break;

					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		return holds;
	}

	public void put_work(glState states) throws IOException {
		int st_source = hash.get_stNumber(states);
//		if (stop == false) {
//			invariantChecker th = new invariantChecker(states, this, st_source);
//			Object result=th.fork();
//			th.join();
////System.out.println(result.toString());
//		}
		check_invariants(states, st_source);
		if (stop == false) {
			// if (states.Global_state.Sum(x => x.Value.Qu.Count) != 0)
			// {
			for (int item : Topology.topologies.keySet()) {
				if(!stop)
				{
				glState temp = states.deepCopy();
				temp.setTop(item);
				if (states.getClass() == glStateDynamicWithTau.class) {
					int st_des = hash.get_stNumber(temp);
					if ((st_des) == -1) {
						st_des = hash.insert(temp);
						trans.add_st(st_des);
						trans lb = new trans(st_des, "tau");
						lb.add_transition(st_source);
						put_work(temp);

					} else if (temp.getTop() == states.getTop()) {
						createNewFork(temp, st_source);
					} else {
						trans lb = new trans(st_des, "tau");
						lb.add_transition(st_source);
					}

				} else {
					createNewFork(temp, st_source);
				}
				}
			}
		}
	}

	public void createNewFork(glState gl, int source) {
		if (stop == false) {
			glStateHandler th = new glStateHandler(gl, this, source);
			getPool().execute(th);
		}

	}

	public ForkJoinPool getPool() {
		return pool;
	}

	public void setPool(ForkJoinPool pool) {
		this.pool = pool;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
