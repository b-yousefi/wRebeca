package rebeca.wrebeca.common;

import java.io.BufferedReader;
/**
 * @author Behnaz Yousefi
 *
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Trans {

	private static Map<Integer, HashSet<Trans>> transitions;
	private static Object lock_tr = new Object();
	private static Object lock_trPut = new Object();
	private static List<String> selectedFields;
	static int numCounterExamples = 0;
	// int stNum;
	private static String outputPath;

	public static synchronized void incCounter()
	{
		numCounterExamples++;
	}
	
	public static void setOutputPath(String modelerPath) {
		outputPath = modelerPath;
	}

	static {
		transitions = new HashMap<Integer, HashSet<Trans>>();

	}

	public static Map<Integer, HashSet<Trans>> getTransitions() {
		return transitions;
	}

	public static int getNumOfStates() {
		return transitions.keySet().size();
	}

	public static int getNumOfTransitions() {
		int sum = 0;
		for (Integer source : transitions.keySet()) {
			sum += transitions.get((Object) source).size();
		}
		return sum;
	}

	public Boolean add_transition(int source) {

		if (!transitions.containsKey(source))
			add_st(source);
		synchronized (lock_tr) {
			if (transitions.get(source).contains(this)) {
				return true;
			} else {
				transitions.get(source).add(this);
				return false;
			}
		}
	}
	public  static  void printCounterExample(GlobalState gl, int stNum, VisitedGlobalstates hash) {
		StringBuffer str = new StringBuffer();
		createCounterExample(gl, stNum, hash, str,new HashSet<Integer>());
		
	}
	
	static int numCounters = 0;
	private static   void createCounterExample(GlobalState gl, int stNum, VisitedGlobalstates hash, StringBuffer str,HashSet<Integer> visited) {
		List<Integer> sources = transitions.keySet().stream()
				.filter(i -> transitions.get(i).stream().anyMatch(x -> x.stateNum == stNum))
				.collect(Collectors.toList());
		str.append(gl.toString());
		str.append("\n");

		for (int s : sources) {
			StringBuffer str2 = new StringBuffer();
			str2.append(str);
			str2.append(s + "-->" + stNum);
			str2.append("\n");
			if (s == 0) {
				try {
				//	synchronized (lock_inv) {
					Trans.incCounter();
				//	}
					FileWriter writerp = new FileWriter( outputPath + "/Output/invariant" + Trans.getNumCounterExamples() + ".txt", true);
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
					createCounterExample(hash.getGlState(stNum), s, hash, str2,visited_temp);
				}
			}
		}
	}
	
	
	public static void add_st(int source) {
		synchronized (lock_trPut) {
			if (!transitions.containsKey(source))
				transitions.put(source, new HashSet<Trans>());
		}
	}

	public int stateNum;
	String label;

	public Trans(int des_, String label_) {
		stateNum = des_;
		label = label_;
	}
	public Trans()
	{}
	public int getStateNum() {
		return stateNum;
	}

	public void setStateNum(int stateNum) {
		this.stateNum = stateNum;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + stateNum;
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
		Trans other = (Trans) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (stateNum != other.stateNum)
			return false;
		return true;
	}

	public static void write_aut(boolean forMcrl) throws IOException {
		FileWriter writer = new FileWriter(outputPath + "/Output/state_space.aut");
		try {
			String str = "des ( 0" + " ," + Trans.getNumOfTransitions() + " ," + Trans.getNumOfStates() + " )";
			writer.write(str);
			writer.write("\n");
			for (int source : Trans.getTransitions().keySet()) {
				for (Trans item : Trans.getTransitions().get(source)) {
					if(!forMcrl)
					{
						str = "( " + source + " , \"" + item.getLabel() + "\" ," + item.getStateNum() + " )";
					}else
					{
						str = "( " + source + " , \"" + item.getLabel().substring(item.getLabel().indexOf(":")+1, item.getLabel().length()) + "\" ," + item.getStateNum() + " )";
					}
					writer.write(str);
					writer.write("\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
private static int numOfInfo_transitions=0;
	@SuppressWarnings("resource")
	public static void write_mcrl2(VisitedGlobalstates hash, int allInitializedState) throws IOException {
		if(Files.exists(Paths.get(outputPath + "/Output/state_space.mcrl2")))
			Files.delete(Paths.get(outputPath + "/Output/state_space.mcrl2"));
	//	write_file(acts, outputPath + "/Output/state_space.mcrl2");
		write_aut(true);
		FileWriter writer = new FileWriter(outputPath + "/Output/state_space.aut",true);
		for (int source : Trans.getTransitions().keySet()) {
			writer.write(glStateInfoTostring(source,hash));		
		}
		writer.close();
		BufferedReader reader = new BufferedReader(new FileReader((outputPath + "/Output/state_space.aut")));
		String line=null;
		String str = "des ( 0" + " ," +(Trans.getNumOfTransitions()+ numOfInfo_transitions)+ " ," + Trans.getNumOfStates() + " )";
		writer = new FileWriter(outputPath + "/Output/stateSpaceMcrl.aut");
		writer.write(str+"\n");
		line=reader.readLine();
		while((line=reader.readLine())!=null)
		{
			writer.write(line+"\n");
		}
		writer.close();
	}
	
	private static String glStateInfoTostring(int stNum,VisitedGlobalstates hash)
	{
		System.out.println(Trans.getNumOfStates());
		StringBuilder str = new StringBuilder();
		Class<? extends State> rebec_type;
		Field[] prop_s;
		String selfTrans="";
		for (State item : hash.getGlState(stNum).getStates()) {
			
			rebec_type = item.getClass();
			prop_s = rebec_type.getDeclaredFields();
				for (Field prp : prop_s) { 
					selfTrans="("+stNum+",\"info"+item.getId();
					prp.setAccessible(true);
					if (prp.getName() != "storage" && prp.getName() != "id") {
						selfTrans+=prp.getName()+"(";
						if (!prp.getType().isArray()) {
							try {
								if(prp.get(item)!=null)
									str.append(selfTrans+prp.get(item).toString()+")\","+stNum+")\n");
								numOfInfo_transitions++;
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else
						{
							Object arr=null;
							int lenght_1=0;
							try {
								if(prp.get(item)!=null)
								{
									arr = Array.get(prp.get(item), 0);
									lenght_1=Array.getLength(prp.get(item));
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(arr != null ){
							if ( arr.getClass().isArray()) {
								String arrayStr="";
								for(int i=0;i<lenght_1;i++)
								{
									Object arr2=null;
									try {
										arr2 = Array.get(prp.get(item), i);
									} catch (ArrayIndexOutOfBoundsException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if(arr2!=null && arr2.getClass().isArray())
									{
										String temp=arrayObjectToString(arr2);
										 arrayStr+=""+temp.substring(0,temp.length()-1)+",";

									}
								}
								if(arrayStr!="")
								{
									arrayStr=selfTrans+arrayStr.substring(0,arrayStr.length()-1)+")\","+stNum+")\n";
									str.append(arrayStr);
									numOfInfo_transitions++;
								}
							}else
							{
								String arrayStr="";
								try {
									arrayStr = arrayObjectToString(prp.get(item));
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(arrayStr!="")
								{
								arrayStr=selfTrans+arrayStr.substring(0,arrayStr.length()-1)+")\","+stNum+")\n";
								str.append(arrayStr);
								numOfInfo_transitions++;
								}
							}
							}
						}
					}
				}
		}
		return str.toString();
	}
	
	private static String arrayObjectToString(Object obj)
	{
		String arrayStr="";
		try {
			
			for(int i=0;i<Array.getLength(obj);i++)
			{
				arrayStr+= Array.get(obj, i).toString()+",";
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayStr;
	}

	private static void write_file(String str, String path) throws IOException {
		FileWriter writerp = new FileWriter(path, true);
		try {
			writerp.write(str);

		} finally {
			writerp.close();
		}
	}



	@SuppressWarnings("unused")
	private static String write_action(VisitedGlobalstates hash, int allInitializedState) throws IOException {
		String actions = "act \n changeTop;\n";

		Field[] prop_s;
		Class<?> rebec_type;
		java.util.List<Class<?>> rebec_types = new ArrayList<Class<?>>();
		GlobalState gl = hash.getGlState(allInitializedState);
		int rebecs_number;
		String acts = "init \n allow({changeTop,";

		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.toString().matches(".*.java");
			}
		};
		File[] translatedFiles = new File(outputPath).listFiles((filter));
		for (int i = 0; i < translatedFiles.length; i++) {
			Class<?> translated = null;
			try {
				translated = ClassLoader.getSystemClassLoader().loadClass(Paths.get(outputPath).getFileName() + "."
						+ translatedFiles[i].getName().substring(0, translatedFiles[i].getName().indexOf(".")));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (translated != null && IMethodArgs.class.isAssignableFrom(translated)) {
				prop_s = translated.getFields();
				actions += translated.getSimpleName().substring(0,translated.getSimpleName().lastIndexOf("_")) + ":";
				for (Field type_arg : translated.getDeclaredFields()) {
					switch (type_arg.getType().getName()) {
					case "int":
						actions += "Int#";
						break;
					case "boolean":
						actions += "Bool#";
						break;
					default:
						break;
					}
				}
				actions = actions.substring(0, actions.length() - 1);
				actions += ";\n";
				acts += translated.getSimpleName() + ",";
			}
		}
		for (State item : gl.getStates()) {
			rebec_type = item.getClass();
			prop_s = rebec_type.getDeclaredFields();
			if (!rebec_types.contains(rebec_type)) {
				rebec_types.add(rebec_type);
				rebecs_number = gl.get_number_of_states(rebec_type);
			

				for (Field prp : prop_s) {
					prp.setAccessible(true);
					if (prp.getName() != "storage" && prp.getName() != "id") {
						if (!prp.getType().isArray()) {
							for (int i = 0; i < rebecs_number; i++) {
								actions += "info" + i + prp.getName() + ",";
								acts += "info" + i + prp.getName() + ",";
							}
							actions = actions.substring(0, actions.length() - 1) + ":";
							if (prp.getType().equals(Integer.TYPE) || prp.equals(Integer.class))
								actions = actions.substring(0, actions.length()) + "Int;\n";
							else if (prp.getType().equals(boolean.class) || prp.getClass().equals(Boolean.class))
								actions = actions.substring(0, actions.length()) + "Bool;\n";
						} else {
							// Object[] array = null;
							int lenght_1 = 0;
							try {
								lenght_1 = Array.getLength(prp.get(item));
							} catch (IllegalArgumentException | IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int i = 0; i < rebecs_number; i++) {
								actions += "info" + i + prp.getName() + ",";

								acts += "info" + i + prp.getName() + ",";

							}
							actions = actions.substring(0, actions.length() - 1) + ":";
							Object ar2 = null;
							try {
								ar2 = Array.get(prp.get(item), 0);
							} catch (ArrayIndexOutOfBoundsException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (ar2 != null && ar2.getClass().isArray()) {
								int lenght_2 = Array.getLength(ar2);

								int c = lenght_1 * lenght_2;
								if (ar2.getClass().getComponentType().equals(int.class)
										|| ar2.getClass().getComponentType().equals(Integer.class)) {
									for (int i = 0; i < c; i++) {
										actions += "Int#";
									}

									actions = actions.substring(0, actions.length() - 1);
									actions += ";\n";
								} else if (ar2.getClass().getComponentType().equals(boolean.class)
										|| ar2.getClass().getComponentType().equals(Boolean.class)) {
									for (int i = 0; i < c; i++) {
										actions += "Bool#";
									}
									actions = actions.substring(0, actions.length() - 1);
									actions += ";\n";
								}

							}
							// System.out.println(ar2.getClass());
							if (ar2.getClass().equals(int.class) || ar2.getClass().equals(Integer.class)) {
								for (int it = 0; it < lenght_1; it++)
									actions += "Int#";
								actions = actions.substring(0, actions.length() - 1);
								actions += ";\n";
							} else if (ar2.getClass().equals(boolean.class) || ar2.getClass().equals(Boolean.class)) {
								for (int it = 0; it < lenght_1; it++)
									actions += "Bool#";
								actions = actions.substring(0, actions.length() - 1);
								actions += ";\n";
							}
						}
					}
				}
			}
		}
		acts = acts.substring(0, acts.length() - 1);
		acts += "},S0);";
		write_file(actions, outputPath + "\\Output\\state_space.mcrl2");
		//write_file("proc", outputPath + "\\Output\\state_space.mcrl2");
		return acts;
	}

	public static List<String> getSelectedFields() {
		return selectedFields;
	}

	public static void setSelectedFields(List<String> selectedFields) {
		Trans.selectedFields = selectedFields;
	}

	public static String getOutputPath() {
		return outputPath;
	}

	public synchronized static int getNumCounterExamples() {
		return numCounterExamples;
	}
}
