package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.List;

public interface Istorage extends Comparable<Istorage>, Cloneable {

    public List<Message> getNaxt();

    public void remove(Message removed);

    public void addMessage(Message newMessage);

    public Integer getSize();

    public Istorage deepCopy();

    public boolean hasInitMessage();

    public Message getInitialMessage();
}
