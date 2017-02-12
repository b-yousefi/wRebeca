/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rebeca.timed;

import rebeca.wrebeca.dynamicNetwork.glStateDynamic;

/**
 *
 * @author Yousefi
 */
public class glStateDynamicTime extends glStateDynamic{
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        if (getGlobal_state() == null) {
            result += 0;
        } else {
            for (Integer id = 0; id < getGlobal_state().size(); id++) {
                result = prime * result + getGlobal_state().get(id).hashCode();
            }

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
        glStateDynamicTime other = (glStateDynamicTime) obj;
        if (getGlobal_state() == null) {
            if (other.getGlobal_state() != null) {
                return false;
            }
        } else {
            int shift = -1;
            if(getGlobal_state().get(0) instanceof StateTime){
                StateTime st1, st2;
                for (Integer id : getGlobal_state().keySet()) {
                    if (!other.getGlobal_state().containsValue(getGlobal_state().get(id))) {
                        //check time shift
                        st1 = (StateTime) getGlobal_state().get(id);
                        st2 = (StateTime) other.getGlobal_state().get(id);
                        shift = st1.equalsWithShift(st2, shift);
                        if  (shift == -1)
                            return false;
                    }
                }
            } else { 
                StateClassicTime st1,st2;
                for (Integer id : getGlobal_state().keySet()) {
                    if (!other.getGlobal_state().containsValue(getGlobal_state().get(id))) {
                        //check time shift
                        st1 = (StateClassicTime) getGlobal_state().get(id);
                        st2 = (StateClassicTime) other.getGlobal_state().get(id);
                        shift = st1.equalsWithShift(st2, shift);
                        if  (shift == -1)
                            return false;
                    }
                }
            }
        }
        return true;
    }
}
