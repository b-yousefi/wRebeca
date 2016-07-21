package rebeca.wrebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */
import java.util.List;

public interface Istorage  {
	public List<Message> getNaxt();
	public void remove(Message removed);
	public void addMessage(Message newMessage);
	public Integer getSize();
	public int hashCode() ;
	public boolean equals(Object obj);
	public String toString();
	public int compareTo(Istorage other);
	public Istorage deepCopy();
	public boolean hasInitMessage();
	public Message getInitialMessage();
}
