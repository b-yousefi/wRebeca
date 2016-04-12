package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
public class msg_state {
	private message msg;

	private State st;

	public msg_state(message mm, State s) {
		msg = mm;
		setSt(s);
	}

	public msg_state(State s) {
		msg = new message();
		setSt(s);
	}

	public message getMsg() {
		return msg;
	}

	public void setMsg(message msg) {
		this.msg = msg;
	}

	public State getSt() {
		return st;
	}

	public void setSt(State st) {
		this.st = st;
	}
}
