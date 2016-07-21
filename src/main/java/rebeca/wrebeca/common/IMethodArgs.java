package rebeca.wrebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
public interface IMethodArgs extends Comparable<IMethodArgs> {
	public int compareTo(IMethodArgs msg1);

	public int hashCode();

	public boolean equals(Object obj);

	public IMethodArgs deepCopy();
}
