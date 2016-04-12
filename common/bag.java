package wRebeca.common;
/**
 * @author Behnaz Yousefi
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class bag implements Istorage {

	List<message> storage;
	
	public bag()
	{
		storage=new ArrayList<message>();
	}
	@Override
	public List<message> getNaxt() {	
		List<message> results=new ArrayList<message>();
		results.addAll(storage);
		Collections.sort(results);
		return results;
	}

	@Override
	public void addMessage(message newMessage) {
		message newMsg=newMessage.deepCopy();		
		storage.add(newMsg);
		Collections.sort(storage);
	}

	@Override
	public Integer getSize() {
		return storage.size();
	}

	@Override
	public Istorage deepCopy() {
		bag copied=new bag();
	    for(message m:storage){
	    	message cpM=m.deepCopy();
	    	copied.addMessage(cpM);
	    }	    
		return  copied;
	}
	@Override
	public void remove(message removed) {
		storage.remove(removed)	;
		Collections.sort(storage);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		Collections.sort(storage);
		result = prime * result+storage.hashCode() ;
		return result;
	}
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		bag other = (bag) obj;
		Collections.sort(storage);
		Collections.sort(other.storage);
		if (storage == null) {
			if (other.storage != null)
				return false;
		} else if (!this.storage.equals(other.storage))
			return false;
		return true;
	}
	@Override
	public String toString() {
		String str="bag [storage=";
		//Collections.sort(storage);
		Object[] msgs= storage.toArray();
		for(int i=0;i<msgs.length;i++)
		{
			str+=msgs[i].toString() + "]";
		}
		
		return str;
	}
	@Override
	public int compareTo(Istorage other) {
		int res=0;
		if(storage.size()!=((bag)other).storage.size())
			if(storage.size()<((bag)other).storage.size())
				return -1;
			else
				return 1;
		message[] msgs= storage.toArray(new message[0]);
		message[] msgsOther=((bag)other).storage.toArray(new message[0]) ;
		for(int i=0;i<msgs.length;i++)
		{
			res=msgs[i].compareTo(msgsOther[i]);
			if(res!=0)
				return res;
		}
		return res;		
	}
	public boolean hasInitMessage()
	{
		if(storage.size()==0)
			return false;
		return storage.stream().anyMatch(x->x.getMethodID().equals("initial"));
	}
	
	public message getInitialMessage(){
		if(this.hasInitMessage())
			return storage.stream().filter(x->x.getMethodID().equals("initial")).findFirst().get();
		return null;
	}
}
