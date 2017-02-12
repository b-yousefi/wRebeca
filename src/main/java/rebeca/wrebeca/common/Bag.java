package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bag implements Istorage {

    private List<Message> storage;

    public List<Message> getMessages() {
        return storage;
    }

    public Bag() {
        storage = new ArrayList<>();
    }

    @Override
    public List<Message> getNext() {
        return storage;
    }

    @Override
    public void addMessage(Message newMessage) {
        Message newMsg = newMessage.deepCopy();
        storage.add(findPlace(newMessage),newMsg);
    }

    public void addMessage(int indx, Message newMessage) {
        Message newMsg = newMessage.deepCopy();
        storage.add(indx,newMsg);
    }
    
    private int findPlace(Message message){
        int indx = 0;
        for(int i=0;i<storage.size();i++){
            if(message.compareTo(storage.get(i))>1){
                return indx;
            }
        }
        return storage.size();
    }

    @Override
    public Integer getSize() {
        return storage.size();
    }

    @Override
    public Istorage deepCopy() {
        try {
            Bag copied = this.getClass().newInstance();
            for(int i=0;i<storage.size();i++) {
                Message cpM = storage.get(i).deepCopy();
                copied.addMessage(i, cpM);
            }
            return copied;
        } catch (InstantiationException ex) {
            Logger.getLogger(Bag.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Bag.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void remove(Message removed) {
        storage.remove(removed);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + storage.hashCode();
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
        Bag other = (Bag) obj;
        if (storage == null) {
            if (other.storage != null) {
                return false;
            }
        } else if (!this.storage.equals(other.storage)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String str = "storage [bag=";
        Object[] msgs = storage.toArray();
        for (int i = 0; i < msgs.length; i++) {
            str += msgs[i].toString() + "]";
        }

        return str;
    }

    @Override
    public int compareTo(Istorage other) {
        int res = 0;
        if (storage.size() != ((Bag) other).storage.size()) {
            if (storage.size() < ((Bag) other).storage.size()) {
                return -1;
            } else {
                return 1;
            }
        }
        for (int i = 0; i < this.storage.size(); i++) {
            res = this.storage.get(i).compareTo(((Bag) other).storage.get(i));
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
            return storage.stream().filter(x -> x.getMethodID().contains("initial")).findFirst().get();
        }
        return null;
    }
}
