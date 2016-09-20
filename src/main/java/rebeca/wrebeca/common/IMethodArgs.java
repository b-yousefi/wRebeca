package rebeca.wrebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
public interface IMethodArgs extends Comparable<IMethodArgs>, Cloneable {
	public IMethodArgs deepCopy();
}
