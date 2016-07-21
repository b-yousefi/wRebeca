package rebeca.wrebeca.dynamicNetwork;


import rebeca.wrebeca.common.State;

 public class glStateDynamicWithTau extends glStateDynamic {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + this.getTop();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj))
			return false;
		glStateDynamic other = (glStateDynamic) obj;
		if (super.getTop() != other.getTop())
			return false;
		return true;
	}
	@Override
    public glStateDynamicWithTau deepCopy()
    {
		glStateDynamicWithTau newGl=new glStateDynamicWithTau();
    	newGl.setTop(this.getTop());   	
    	newGl.clearGlobal_state();
    	for(Integer i: this.getGlobal_state().keySet())
    	{
    		State st=this.getGlobal_state().get(i).deepCopy();
    		newGl.add_lState(st);
    	}
    	return newGl;
    }   
    
	@Override
	public String toString() {
		return "glState [" + super.getGlobal_state() + ", top=" + super.getTop() + "]";
	}
}
