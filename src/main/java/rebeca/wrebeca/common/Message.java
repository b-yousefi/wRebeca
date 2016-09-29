package rebeca.wrebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.util.ArrayList;
import java.util.List;

public class Message implements Comparable<Message>, Cloneable {

    private Integer senderID;

    private List<Integer> recID;

    private String methodID;

    private IMethodArgs msgArgs;

    public Integer getSenderID() {
        return senderID;
    }

    public void setSenderID(Integer senderID) {
        this.senderID = senderID;
    }

    public List<Integer> getRecID() {
        return recID;
    }

    public void setRecID(List<Integer> recID) {
        this.recID = recID;
    }

    public String getMethodID() {
        return methodID;
    }

    public void setMethodID(String methodID) {
        this.methodID = methodID;
    }

    public IMethodArgs getMsgArgs() {
        return msgArgs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((methodID == null) ? 0 : methodID.hashCode());
        result = prime * result + ((msgArgs == null) ? 0 : msgArgs.hashCode());
//        result = prime * result + ((recID == null) ? 0 : recID.hashCode());
      //  result = prime * result + ((senderID == null) ? 0 : senderID.hashCode());
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
        Message other = (Message) obj;
        if (methodID == null) {
            if (other.methodID != null) {
                return false;
            }
        } else if (!methodID.equals(other.methodID)) {
            return false;
        }
        if (msgArgs == null) {
            if (other.msgArgs != null) {
                return false;
            }
        } else if (!msgArgs.equals(other.msgArgs)) {
            return false;
        }
        // if (recID == null) {
        // if (other.recID != null)
        // return false;
        // } else if (!recID.equals(other.recID))
        // return false;
//         if (senderID == null) {
//         if (other.senderID != null)
//         return false;
//         } else if (!senderID.equals(other.senderID))
//         return false;
        return true;
    }

    public Message deepCopy() {
        Message copied = new Message();
        copied.methodID = this.methodID;
        copied.msgArgs = this.msgArgs.deepCopy();
        copied.recID = new ArrayList<>(this.recID);
        copied.senderID = this.senderID;
        return copied;
    }

    public void setMsgArgs(IMethodArgs msgArgs) {
        this.msgArgs = msgArgs;
    }

    public Message() {
        recID = new ArrayList<>();
    }

    public Message(String method_id_, IMethodArgs m, Integer senderID_) {
        msgArgs = m;
        senderID = senderID_;
        methodID = method_id_;
        recID = new ArrayList<>();
    }

    public Message(String method_id_, IMethodArgs m, Integer recID_, Integer senderID_) {
        msgArgs = m;
        recID = new ArrayList<>();
        recID.add(recID_);
        senderID = senderID_;
        methodID = method_id_;
    }

    public Message(String method_id_, IMethodArgs m, List<Integer> recID_, Integer senderID_) {
        msgArgs = m;
        recID = recID_;
        senderID = senderID_;
        methodID = method_id_;
    }

    public Message(String method_id_, IMethodArgs m, boolean[] recID_, Integer senderID_) {
        msgArgs = m;
        recID = new ArrayList<>();
        for (Integer i = 0; i < recID_.length; i++) {
            if (recID_[i]) {
                recID.add(i);
            }
        }
        senderID = senderID_;
        methodID = method_id_;
    }

    @Override
    public int compareTo(Message other) {
        if (other == null) {
            return 1;
        }
        Integer res;
        res = other.methodID.compareTo(this.methodID);
        if (res != 0) {
            return res;
        }
        // if (recID.size() == 1)
        // {
        // res = other.recID.containsAll(this.recID)?1:0;
        // if (res != 0) return res;
        // }
//        res = other.senderID.compareTo(this.senderID);
//        if (res != 0) return res;

        res = other.msgArgs.compareTo(this.msgArgs);
        if (res != 0) {
            return res;
        }

        return res;
    }

    @Override
    public String toString() {
        return "[methodID=" + methodID + ", msgArgs=" + msgArgs + "]";
    }
}
