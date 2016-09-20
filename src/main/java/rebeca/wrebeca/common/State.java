package rebeca.wrebeca.common;


import rebeca.wrebeca.common.Istorage;

/**
 * @author Behnaz Yousefi
 *
 */
public class State implements Comparable<State> {
	private int id;

	protected Istorage storage;

	public State(Istorage store) {

		storage = store;

	}

	public State(State parent) {
		this.id = parent.id;
		this.storage = parent.storage.deepCopy();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Istorage getStorage() {
		return storage;
	}

	public void setStorage(Istorage storage) {
		this.storage = storage;
	}

	@Override
	public int compareTo(State st) {
		if (st == null)
			return 1;

		int res = st.id > this.id ? -1 : 1;
		if (res != 0)
			return res;
		res = st.storage.compareTo(this.storage);
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((storage == null) ? 0 : storage.hashCode());
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
		State other = (State) obj;
		if (id != other.id)
			return false;
		if (storage == null) {
			if (other.storage != null)
				return false;
		} else if (!storage.equals(other.storage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", storage=" + storage.toString() + "]";
	}

	public State deepCopy() {
		State copied = new State(this.storage.deepCopy());
		copied.id = this.id;
		copied.storage = this.storage.deepCopy();

		return copied;
	}
}