package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Qu implements Istorage {

    Queue<Message> storage;

    public Qu() {
        storage = new ArrayDeque<>();
    }

    @Override
    public List<Message> getNaxt() {
        List<Message> result = new ArrayList<>();
        result.add(storage.peek());
        return result;
    }

    @Override
    public void addMessage(Message newMessage) {
        storage.add(newMessage);
    }

    @Override
    public Integer getSize() {
        return storage.size();
    }

    @Override
    public Istorage deepCopy() {
        Qu copied = new Qu();
        for (Message m : storage) {
            Message cpM = m.deepCopy();
            copied.addMessage(cpM);
        }
        return copied;
    }

    @Override
    public void remove(Message removed) {
        storage.poll();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (Message msg : storage) {
            result = prime * result + msg.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Qu other = (Qu) obj;
        if (storage == null) {
            if (other.storage != null) {
                return false;
            }
        } else {
            if (storage.size() != other.storage.size()) {
                return false;
            }
            Message[] msgs = storage.toArray(new Message[0]);
            Message[] msgsOther = ((Qu) other).storage.toArray(new Message[0]);
            for (int i = 0; i < msgs.length; i++) {
                if (!msgs[i].equals(msgsOther[i])) {
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public String toString() {
        String str = "storage [qu =";
        Object[] msgs = storage.toArray();
        for (int i = 0; i < msgs.length; i++) {
            str += msgs[i].toString() + "]";
        }

        return str;
    }

    @Override
    public int compareTo(Istorage other) {
        int res = 0;
        if (storage.size() != ((Qu) other).storage.size()) {
            if (storage.size() < ((Qu) other).storage.size()) {
                return -1;
            } else {
                return 1;
            }
        }
        Message[] msgs = storage.toArray(new Message[0]);
        Message[] msgsOther = ((Qu) other).storage.toArray(new Message[0]);
        for (int i = 0; i < msgs.length; i++) {
            res = msgs[i].compareTo(msgsOther[i]);
            if (res != 0) {
                return res;
            }
        }
        return res;
    }

    @Override
    public boolean hasInitMessage() {
        if (storage.isEmpty()) {
            return false;
        }
        return storage.stream().anyMatch(x -> x.getMethodID().contains("initial"));
    }

    @Override
    public Message getInitialMessage() {
        if (this.hasInitMessage()) {
            return this.getNaxt().get(0);
        }
        return null;
    }
}
