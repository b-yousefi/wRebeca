package rebeca.wrebeca.common;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class InvariantChecker extends RecursiveAction {
	public InvariantChecker(GlobalState myglState,StateSpaceBuilder builder, int source)
	{
		gl=myglState;
		stBuilder=builder;
		st_source=source;
	}
	private GlobalState gl;
	private StateSpaceBuilder stBuilder;
	private int st_source;
	@Override
	protected  void compute() {
		if (StateSpaceBuilder.getInvariants() != null) {
			for (Method m : StateSpaceBuilder.getInvariants()) {
				
				try {
					Object[] arguments = new Object[] { gl, null };
					Object var = m.invoke(stBuilder, arguments);
					if ((boolean) var) {
						//if(!stBuilder.isStop())
						{
							stBuilder.setStop(true);											
							StringBuilder str = new StringBuilder();
							createCounterExample createCounter=new createCounterExample(gl, st_source,stBuilder.hash, str,new HashSet<Integer>());
							createCounter.fork();
							createCounter.join();
						}
						break;
					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

 @SuppressWarnings("serial")
class createCounterExample extends RecursiveAction{

	public  createCounterExample(GlobalState myglState, int stNum, VisitedGlobalstates hash, StringBuilder str_,HashSet<Integer> visited_) {
		gl=myglState;
		visitedGlstates=hash;
		st_source=stNum;
		visited=visited_;
		str=str_;
	}
	private GlobalState gl;
	private VisitedGlobalstates visitedGlstates;
	private int st_source;
	HashSet<Integer> visited;
	StringBuilder str;
	@Override
	protected  void compute() {
		List<Integer> sources =Trans.getTransitions().keySet().stream()
				.filter(i -> Trans.getTransitions().get(i).stream().anyMatch(x -> x.stateNum == st_source))
				.collect(Collectors.toList());
		str.append(gl.toString());
		str.append("\n");

		for (int s : sources) {
			StringBuilder str2 = new StringBuilder();
			str2.append(str);
			str2.append(s + "-->" + st_source);
			str2.append("\n");
			if (s == 0) {
				try {
					FileWriter writerp = new FileWriter( Trans.getOutputPath() + "/Output/invariant" + (Trans.getNumCounterExamples()) + ".txt", true);
					Trans.incCounter();
					try {
						writerp.write(str2.toString());

					} finally {
						writerp.close();

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				if(!visited.contains(s))
				{
					HashSet<Integer> visited_temp=new HashSet<Integer>();
					visited_temp.addAll(visited);
					visited_temp.add(s);
					createCounterExample createCounter=new createCounterExample(visitedGlstates.getGlState(st_source), s, visitedGlstates, str2,visited_temp);
					createCounter.fork();
					createCounter.join();
				
				}
			}
		}
	}
}
