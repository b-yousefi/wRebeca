/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rebeca.timed;

import java.util.ArrayList;
import java.util.List;
import rebeca.wrebeca.common.IMethodArgs;
import rebeca.wrebeca.common.Message;
import rebeca.wrebeca.common.State;

/**
 *
 * @author Yousefi
 */
public class MessageTime extends Message{
    private int arrival;
    private int deadline;

    public int getArrival() {
        return arrival;
    }

    public void setArrival(int arrival) {
        this.arrival = arrival;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }
    

    
    public MessageTime() {
        super();
        deadline = Integer.MAX_VALUE;
    }

    public MessageTime(String method_id_, IMethodArgs m, Integer senderID_, Integer arrival, Integer deadline) {
        super(method_id_,  m,  senderID_);
        this.arrival = arrival;
        this.deadline = deadline;
    }

    public MessageTime(String method_id_, IMethodArgs m, Integer recID_, Integer senderID_, Integer arrival, Integer deadline) {
        super(method_id_,  m, recID_,  senderID_);
        this.arrival = arrival;
        this.deadline = deadline;
    }

    public MessageTime(String method_id_, IMethodArgs m, List<Integer> recID_, Integer senderID_, Integer arrival, Integer deadline) {
        super(method_id_,  m, recID_,  senderID_);
        this.arrival = arrival;
        this.deadline = deadline;
    }

    public MessageTime(String method_id_, IMethodArgs m, boolean[] recID_, Integer senderID_, Integer arrival, Integer deadline) {
        super(method_id_,  m, recID_,  senderID_);
        this.arrival = arrival;
        this.deadline = deadline;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
//        result = prime * result + arrival;
//        result = prime * result + deadline;

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj))
            return false;
        if (getClass() != obj.getClass()) {
            return false;
        }
//        MessageTime other = (MessageTime) obj;
//        if (this.arrival != other.arrival)
//            return false;
//        if (this.deadline != other.deadline)
//            return false;
        return true;
    }
    
    public int equalsWithShift(Object obj, int shift) {
        if(!super.equals(obj))
            return -1;
        if (getClass() != obj.getClass()) {
            return -1;
        }
        MessageTime other = (MessageTime) obj;
        int sh = other.arrival - this.arrival;
        if (shift != -1 && shift != sh)
            return -1;
        sh = other.deadline - this.deadline;
        if (shift != -1 && shift != sh)
            return -1;
        return sh;
    }

    @Override
    public MessageTime deepCopy() {
        MessageTime copied;
        copied =(MessageTime) super.deepCopy();
        copied.arrival = this.arrival;
        copied.deadline = this.deadline;
        return copied;
    }

    @Override
    public int compareTo(Message other) {
        int res = super.compareTo(other);
        if(res!=0)
            return res;
        MessageTime mt = (MessageTime) other;
        res = mt.arrival > this.arrival ? -1 : 1;
        if(res!=0)
            return res;
        res = mt.deadline > this.deadline ? -1 : 1;
        if(res!=0)
            return res;
        return res;
    }

    @Override
    public String toString() {
        return super.toString() + ", arrival time=" + arrival + ", deadline time=" + deadline+ "#";
    }
    
}
