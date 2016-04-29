package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class qu implements Istorage {
	Queue<message> storage;

	public qu() {
		storage = new ArrayDeque<message>();
	}

	@Override
	public List<message> getNaxt() {
		List<message> result = new ArrayList<message>();
		result.add(storage.peek());
		return result;
	}

	@Override
	public void addMessage(message newMessage) {
		storage.add(newMessage);

	}

	@Override
	public Integer getSize() {

		return storage.size();
	}

	@Override
	public Istorage deepCopy() {
		qu copied = new qu();
		for (message m : storage) {
			message cpM = m.deepCopy();
			copied.addMessage(cpM);
		}
		return copied;
	}

	@Override
	public void remove(message removed) {
		storage.poll();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		for (message msg : storage) {
			result = prime * result + msg.hashCode();
		}
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
		qu other = (qu) obj;
		if (storage == null) {
			if (other.storage != null)
				return false;
		} else {
			if (storage.size() != other.storage.size())
				return false;
			message[] msgs = storage.toArray(new message[0]);
			message[] msgsOther = ((qu) other).storage.toArray(new message[0]);
			for (int i = 0; i < msgs.length; i++) {
				if (!msgs[i].equals(msgsOther[i]))
					return false;
			}

		}
		return true;
	}

	@Override
	public String toString() {
		String str = "qu [storage=";
		Object[] msgs = storage.toArray();
		for (int i = 0; i < msgs.length; i++) {
			str += msgs[i].toString() + "]";
		}

		return str;
	}

	@Override
	public int compareTo(Istorage other) {
		int res = 0;
		if (storage.size() != ((qu) other).storage.size())
			if (storage.size() < ((qu) other).storage.size())
				return -1;
			else
				return 1;
		message[] msgs = storage.toArray(new message[0]);
		message[] msgsOther = ((qu) other).storage.toArray(new message[0]);
		for (int i = 0; i < msgs.length; i++) {
			res = msgs[i].compareTo(msgsOther[i]);
			if (res != 0)
				return res;
		}
		return res;
	}

	public boolean hasInitMessage() {
		if (storage.size() == 0)
			return false;
		return storage.stream().anyMatch(x -> x.getMethodID().contains("initial"));
	}

	public message getInitialMessage() {
		if (this.hasInitMessage())
			return this.getNaxt().get(0);
		return null;
	}

}
