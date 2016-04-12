package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
import java.util.List;

public interface Istorage  {
	public List<message> getNaxt();
	public void remove(message removed);
	public void addMessage(message newMessage);
	public Integer getSize();
	public int hashCode() ;
	public boolean equals(Object obj);
	public String toString();
	public int compareTo(Istorage other);
	public Istorage deepCopy();
	public boolean hasInitMessage();
	public message getInitialMessage();
}
