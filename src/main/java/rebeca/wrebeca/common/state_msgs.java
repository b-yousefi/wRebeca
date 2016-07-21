//package rebeca.wrebeca.common;
///**
// * @author Behnaz Yousefi
// *
// */
//import rebeca.wrebeca.common.State;
//import java.util.ArrayList;
//import java.util.List;
//
//class state_msgs {
//	private List<Message> msgs;
//
//	private State st;
//
//	public void add_message(Message mm) {
//		msgs.add(mm);
//	}
//
//	public state_msgs(List<Message> mm, State s) {
//		msgs = new ArrayList<Message>(mm);
//		st = s;
//	}
//
//	public state_msgs(State s, List<Message> mm) {
//		msgs = new ArrayList<Message>(mm);
//		st = s;
//	}
//
//	public state_msgs(State s) {
//		msgs = new ArrayList<Message>();
//		st = s;
//	}
//
//	public List<Message> getMsgs() {
//		return msgs;
//	}
//
//	public State getSt() {
//		return st;
//	}
//
//}
