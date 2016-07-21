//Invariant
public Boolean loopFreedom(GlobalState gl, Object[] pars)
{
	boolean loop;

	int src =0;
	int[] seen = null;
		loop = false;
	for (int id = 0; id < gl.getStates().size(); id++)
	{
		for (int des = 0; des < gl.getStates().size(); des++)
		{


			seen = new int[gl.getStates().size()];
			for (int i = 0; i < gl.getStates().size(); i++)
			{
				seen[i] = -1;
			}
			loop = looop(gl, des, id, 0, seen);
			if (loop)
				break;
		}
		if (loop)
			break;
	}
	if (loop)
	{
		System.out.println("Loop is found!!!!!!!!!!!");
		return true;
	}
	else
		return false;

}
boolean looop(GlobalState gl, int des, int src, int indx, int[] seen)
{

	int nextHop = -1;
	boolean loop = false;

	Node curNode = (Node) gl.getStates().get(src) ;
	seen[indx] = curNode.getId();
	indx++;
	for (int k = 0; k < gl.getStates().size(); k++)
	{
		nextHop = curNode.getnhop()[des][k];
		if (nextHop == curNode.getId())
			break;

		for (int j = 0; j < indx; j++)
		{
			if (nextHop == des)
				break;
			if (seen[j] == nextHop)
			{
				loop = true;
				break;
			}
		}
		if (loop)
			return true;
		else
		{
			//src = nextHop;
			if (nextHop == -1 || nextHop == des)
			{// return false;
			}
			else
				if (looop(gl, des, nextHop, indx, seen))
				{
					loop = true;
					break;
				}
		}
	}
	if (loop)
		return true;
	else
		return false;
}