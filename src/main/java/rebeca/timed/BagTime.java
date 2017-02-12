/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rebeca.timed;

import java.util.ArrayList;
import java.util.List;
import rebeca.wrebeca.common.Bag;
import rebeca.wrebeca.common.Istorage;
import rebeca.wrebeca.common.Message;

/**
 *
 * @author Yousefi
 */
public class BagTime extends Bag{

   @Override
    public List<Message> getNext() {
        throw new UnsupportedOperationException("You should use the getNext with \"localTime\" parameter"); 
    }
    
//    @Override
//    public Istorage deepCopy() {
//        BagTime copied = new BagTime();
//        for(int i=0;i<super.getMessages().size();i++) {
//            Message cpM = super.getMessages().get(i).deepCopy();
//            copied.addMessage(i, cpM);
//        }
//        return copied;
//    }

    List<Message> getNext(int localTime) {
        int minTime = Integer.MAX_VALUE, temp;
        List<Message> enabledMessages = new ArrayList<>();
        MessageTime mt;
        for(Message m: getMessages()){
            mt =((MessageTime)m);
            if((temp = Math.max(mt.getArrival(), localTime)) < minTime){
                minTime = temp;
                enabledMessages.clear();
            } 
            if (temp == minTime){
                enabledMessages.add(mt);
            }
        }
        return enabledMessages;
    }
}
