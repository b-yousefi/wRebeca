package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
public interface method_args extends Comparable<method_args> {
	public int compareTo(method_args msg1);

	public int hashCode();

	public boolean equals(Object obj);

	public method_args deepCopy();
}
