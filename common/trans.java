package wRebeca.common;

/**
 * @author Behnaz Yousefi
 *
 */
import java.io.File;
import java.io.FileFilter;
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

public class trans {

	private static Map<Integer, HashSet<trans>> transitions;
	private static Object lock_tr = new Object();
	private static Object lock_trPut = new Object();
	// int stNum;
	private static String outputPath;

	public static void setOutputPath(String modelerPath) {
		outputPath = modelerPath;
	}

	static {
		transitions = new HashMap<Integer, HashSet<trans>>();

	}

	public static Map<Integer, HashSet<trans>> getTransitions() {
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

	public static void add_st(int source) {
		synchronized (lock_trPut) {
			if (!transitions.containsKey(source))
				transitions.put(source, new HashSet<trans>());
		}
	}

	public int stateNum;
	String label;

	public trans(int des_, String label_) {
		stateNum = des_;
		label = label_;
	}

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
		trans other = (trans) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (stateNum != other.stateNum)
			return false;
		return true;
	}

	public static void write_aut() throws IOException {
		FileWriter writer = new FileWriter(outputPath + "/Output/state_space.aut");
		try {
			String str = "des ( 0" + " ," + trans.getNumOfTransitions() + " ," + trans.getNumOfStates() + " )";
			writer.write(str);
			writer.write("\n");
			for (int source : trans.getTransitions().keySet()) {
				for (trans item : trans.getTransitions().get(source)) {
					str = "( " + source + " , \"" + item.getLabel() + "\" ," + item.getStateNum() + " )";
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

	public static void write_mcrl2(visitedGlstates hash, int allInitializedState) throws IOException {
		Files.delete(Paths.get(outputPath + "/Output/state_space.mcrl2"));
		String acts = write_action(hash, allInitializedState);
		write_file(acts, outputPath + "/Output/state_space.mcrl2");

	}

	private static void write_file(String str, String path) throws IOException {
		FileWriter writerp = new FileWriter(path, true);
		try {
			writerp.write(str);

		} finally {
			writerp.close();
		}
	}

	public static void printCounterExample(glState gl, int stNum, visitedGlstates hash) {
		StringBuilder str = new StringBuilder();
		createCounterExample(gl, stNum, hash, str);

	}

	static int numCounters = 0;

	private static void createCounterExample(glState gl, int stNum, visitedGlstates hash, StringBuilder str) {
		List<Integer> sources = transitions.keySet().stream()
				.filter(i -> transitions.get(i).stream().anyMatch(x -> x.stateNum == stNum))
				.collect(Collectors.toList());
		str.append(gl.toString());
		str.append("\n");

		for (int s : sources) {
			StringBuilder str2 = new StringBuilder();
			str2.append(str);
			str2.append(s + "-->" + stNum);
			str2.append("\n");
			if (s == 0) {
				try {
					write_file(str2.toString(), outputPath + "/Output/invariant" + (numCounters++) + ".txt");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				createCounterExample(hash.getGlState(stNum), s, hash, str2);
		}
	}

	private static String write_action(visitedGlstates hash, int allInitializedState) throws IOException {
		String actions = "changeTop;\n";

		Field[] prop_s;
		Class<?> rebec_type;
		java.util.List<Class<?>> rebec_types = new ArrayList<Class<?>>();
		glState gl = hash.getGlState(allInitializedState);
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
			if (translated != null && method_args.class.isAssignableFrom(translated)) {
				prop_s = translated.getFields();
				actions += translated.getSimpleName() + ":";
				for (Field type_arg : translated.getDeclaredFields()) {
					switch (type_arg.getType().getName()) {
					case "int":
						actions += "Nat#";
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
		for (wRebeca.common.State item : gl.getStates()) {
			rebec_type = item.getClass();
			prop_s = rebec_type.getDeclaredFields();
			if (!rebec_types.contains(rebec_type)) {
				rebec_types.add(rebec_type);
				rebecs_number = gl.get_number_of_states(rebec_type);
				;

				for (Field prp : prop_s) {
					prp.setAccessible(true);
					if (prp.getName() != "storage" && prp.getName() != "id") {
						if (!prp.getType().isArray()) {
							for (int i = 0; i < rebecs_number; i++) {
								actions += "inf" + i + prp.getName() + ",";
								acts += "inf" + i + prp.getName() + ",";
							}
							actions = actions.substring(0, actions.length() - 1) + ":";
							if (prp.getType().equals(Integer.TYPE) || prp.equals(Integer.class))
								actions = actions.substring(0, actions.length()) + "Nat;\n";
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
								actions += "inf" + i + prp.getName() + ",";

								acts += "inf" + i + prp.getName() + ",";

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
										actions += "Nat#";
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
									actions += "Nat#";
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
		write_file("proc", outputPath + "\\Output\\state_space.mcrl2");
		return acts;
	}
}
