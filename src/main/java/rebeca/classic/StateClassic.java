/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rebeca.classic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import rebeca.wrebeca.common.Istorage;
import rebeca.wrebeca.common.State;

/**
 *
 * @author Yousefi
 */
public class StateClassic extends State {
    
    public  List<KnownRebec> knownRebecsDef;
    private Integer indx=0;
    
    public StateClassic(Istorage store) {
        super(store);
        knownRebecsDef = new ArrayList();
    }
    
    public StateClassic(State parent) {
        super(parent);
        knownRebecsDef = ((StateClassic)parent).knownRebecsDef;
    }
    
    public void addKnown(Integer rebec){        
        knownRebecsDef.get(indx).setRebecID(rebec);
        indx++;
    }
    
    public Integer getKnownIndx(String key){
        Optional<KnownRebec> kn =knownRebecsDef.stream().filter(x->x.getName().equals(key)).findFirst();
        if(kn.isPresent())
            return kn.get().getRebecID();
        else 
            return -1;
    }
    
    public List<Integer> getNeighbours(){
        return knownRebecsDef.stream().map(x->x.getRebecID()).collect(toList());
    }
    
    @Override
    public int compareTo(State st) {
        if (st == null)
                return 1;
        if (getClass() != st.getClass())
                return -1;
        int res = st.getStorage().compareTo(this.getStorage());
        if (res != 0)
                return res;
        res = st.getId() > this.getId() ? -1 : 1;
        return res;
    }

    @Override
    public State deepCopy() {
            StateClassic copied = new StateClassic(this.storage.deepCopy());
            copied.knownRebecsDef = this.knownRebecsDef;
            copied.setId(this.getId());
            copied.storage = this.storage.deepCopy();

            return copied;
    }

    @Override
    public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((storage == null) ? 0 : storage.hashCode());
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
            StateClassic other = (StateClassic) obj;
            if(this.getId()!=other.getId())
                return false;
            if (storage == null) {
                    if (other.storage != null)
                            return false;
            } else if (!storage.equals(other.storage))
                    return false;
            return true;
    }

    @Override
    public String toString() {
            String str = "State "+this.getId()+" [" + storage.toString();
            return str + "]";
    }
}




