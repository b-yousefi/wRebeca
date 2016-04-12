package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class message implements Comparable<message>, Serializable {
	private Integer senderID;

	private List<Integer> recID;

	private String methodID;

	private method_args msgArgs;

	public Integer getSenderID() {
		return senderID;
	}

	public void setSenderID(Integer senderID) {
		this.senderID = senderID;
	}

	public List<Integer> getRecID() {
		return recID;
	}

	public void setRecID(List<Integer> recID) {
		this.recID = recID;
	}

	public String getMethodID() {
		return methodID;
	}

	public void setMethodID(String methodID) {
		this.methodID = methodID;
	}

	public method_args getMsgArgs() {
		return msgArgs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodID == null) ? 0 : methodID.hashCode());
		result = prime * result + ((msgArgs == null) ? 0 : msgArgs.hashCode());
		// result = prime * result + ((recID == null) ? 0 : recID.hashCode());
		// result = prime * result + ((senderID == null) ? 0 :
		// senderID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		message other = (message) obj;
		if (methodID == null) {
			if (other.methodID != null)
				return false;
		} else if (!methodID.equals(other.methodID))
			return false;
		if (msgArgs == null) {
			if (other.msgArgs != null)
				return false;
		} else if (!msgArgs.equals(other.msgArgs))
			return false;
		// if (recID == null) {
		// if (other.recID != null)
		// return false;
		// } else if (!recID.equals(other.recID))
		// return false;
		// if (senderID == null) {
		// if (other.senderID != null)
		// return false;
		// } else if (!senderID.equals(other.senderID))
		// return false;
		return true;
	}

	public message deepCopy() {
		message copied = new message();
		copied.methodID = this.methodID;
		copied.msgArgs = this.msgArgs.deepCopy();
		copied.recID = new ArrayList<Integer>(this.recID);
		copied.senderID = this.senderID;
		return copied;
	}

	public void setMsgArgs(method_args msgArgs) {
		this.msgArgs = msgArgs;
	}

	public message() {
		recID = new ArrayList<Integer>();
	}

	public message(String method_id_, method_args m, Integer senderID_) {
		msgArgs = m;
		senderID = senderID_;
		methodID = method_id_;
		recID = new ArrayList<Integer>();
	}

	public message(String method_id_, method_args m, Integer recID_, Integer senderID_) {
		msgArgs = m;
		recID = new ArrayList<Integer>();
		recID.add(recID_);
		senderID = senderID_;
		methodID = method_id_;
	}

	public message(String method_id_, method_args m, List<Integer> recID_, Integer senderID_) {
		msgArgs = m;
		recID = recID_;
		senderID = senderID_;
		methodID = method_id_;
	}

	public message(String method_id_, method_args m, boolean[] recID_, Integer senderID_) {
		msgArgs = m;
		recID = new ArrayList<Integer>();
		for (Integer i = 0; i < recID_.length; i++) {
			if (recID_[i])
				recID.add(i);
		}

		senderID = senderID_;
		methodID = method_id_;
	}

	@Override
	public int compareTo(message other) {
		if (other == null)
			return 1;

		Integer res = 0;

		res = other.methodID.compareTo(this.methodID);
		if (res != 0)
			return res;

		// if (recID.size() == 1)
		// {
		// res = other.recID.containsAll(this.recID)?1:0;
		// if (res != 0) return res;
		// }

		// res = msg1.senderID.CompareTo(this.senderID);
		// if (res != 0) return res;

		res = other.methodID.compareTo(this.methodID);
		if (res != 0)
			return res;

		res = other.msgArgs.compareTo(this.msgArgs);
		if (res != 0)
			return res;

		return res;
	}

	@Override
	public String toString() {
		return "[methodID=" + methodID + ", msgArgs=" + msgArgs + "]";
	}
}
