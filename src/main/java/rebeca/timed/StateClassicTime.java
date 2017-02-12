/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rebeca.timed;

import java.util.List;
import rebeca.classic.StateClassic;
import rebeca.wrebeca.common.Istorage;
import rebeca.wrebeca.common.Message;
import rebeca.wrebeca.common.State;

/**
 *
 * @author Yousefi
 */
public class StateClassicTime extends StateClassic{
       private int localTime;

    public int getLocalTime() {
        return localTime;
    }

    public void setLocalTime(int localTime) {
        this.localTime = localTime;
    }
    
    public void setDelay(int time){
        this.localTime  = this.localTime + time;
    }
    
    public StateClassicTime(Istorage store) {
        super(store);
    }
    
    public StateClassicTime(State state) {
        super(state);
        this.localTime=((StateClassicTime)state).localTime;
    }
    
    @Override
    public List<Message> getEnabledMessages() {
        List<Message> msgs = ((BagTime)this.storage).getNext(this.localTime);
        this.setLocalTime(((MessageTime)msgs.get(0)).getArrival());
        return msgs;
    } 
    
    @Override
    public int compareTo(State st) {
        int res = super.compareTo(st);
        if(res!=0)
            return res;
        res =((StateClassicTime)st).localTime > this.localTime ? -1 : 1;
        return res;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();;
//        result = prime * result +  localTime;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj))
            return false;
        if (getClass() != obj.getClass()) {
            return false;
        }
        StateClassicTime other = (StateClassicTime) obj;
//        if(this.localTime!=other.localTime)
//            return false;
        return true;
    }
    
    public int equalsWithShift(State obj, int shift) {
        if(!super.equals(obj))
            return -1;
        if (getClass() != obj.getClass()) {
            return -1;
        }
        StateClassicTime other = (StateClassicTime) obj;
        int sh = other.localTime - this.localTime;
        if (shift != -1 && shift != sh)
            return -1;
        if(shift == -1)
            shift = sh;
        BagTime bagThis = ((BagTime)getStorage());
        BagTime bagOther = (BagTime)other.getStorage().deepCopy();
        for(Message mt: bagThis.getMessages()){
            for(int i=0; i<bagOther.getSize();i++){
                if(bagOther.getMessages().get(i).equals(mt)){
                    sh = ((MessageTime)bagOther.getMessages().get(i)).equalsWithShift(((MessageTime)mt), sh);
                    if (sh != -1 && shift == sh){
                        bagOther.getMessages().remove(i);
                        break;
                    } else {
                        return -1;
                    }
                }
            }     
        }
        return sh;
    }

    @Override
    public String toString() {
        return super.toString() + ", localTime=" + localTime;
    }

    public StateClassicTime deepCopy() {
        StateClassicTime copied = new StateClassicTime(this.storage.deepCopy());
        copied.knownRebecsDef = this.knownRebecsDef;
        copied.setId(this.getId());
        copied.localTime = this.localTime;
        copied.storage = this.storage.deepCopy();
        return copied;
    }
  
}
