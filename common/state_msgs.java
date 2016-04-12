package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
import java.util.ArrayList;
import java.util.List;

class state_msgs {
	private List<message> msgs;

	private State st;

	public void add_message(message mm) {
		msgs.add(mm);
	}

	public state_msgs(List<message> mm, State s) {
		msgs = new ArrayList<message>(mm);
		st = s;
	}

	public state_msgs(State s, List<message> mm) {
		msgs = new ArrayList<message>(mm);
		st = s;
	}

	public state_msgs(State s) {
		msgs = new ArrayList<message>();
		st = s;
	}

	public List<message> getMsgs() {
		return msgs;
	}

	public State getSt() {
		return st;
	}

}
