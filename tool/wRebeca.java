package wRebeca.tool;

/**
 * @author Behnaz Yousefi
 *
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.fife.ui.rtextarea.*;


import org.fife.ui.rsyntaxtextarea.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class wRebeca {

	private JFrame frame;
	private JMenuItem mntmOpen;
	JTree trSource;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					wRebeca window = new wRebeca();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public wRebeca() {
		initialize();

	}

	RSyntaxTextArea textArea;
	JFileChooser chooser;
	JMenuItem mntmSave;
	String filePath = null;
	File inputFile;
	frmCompile compile;
	private JTable errorList;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		trSource = new JTree();
		DefaultTreeModel treeModel = (DefaultTreeModel) trSource.getModel();
		treeModel.setRoot(new DefaultMutableTreeNode("Nothing"));
		treeModel.reload();

		// ((DefaultTreeModel)trSource.getModel().getRoot())
		frame.setBounds(100, 100, 700, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.toString().matches(".*.java");
			}
		};
		mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select a File");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter wRebecaFilter = new FileNameExtensionFilter(
						"wireless Rebeca for dynamic topology(.wRebeca)", "wRebeca");
				FileNameExtensionFilter bRebecaFilter = new FileNameExtensionFilter(
						"broadcasting Rebeca for static topology(.bRebeca)", "bRebeca");
				chooser.addChoosableFileFilter(bRebecaFilter);
				chooser.addChoosableFileFilter(wRebecaFilter);
				if (chooser.showOpenDialog(mntmOpen) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
					System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
					filePath = chooser.getSelectedFile().getPath();
					inputFile = chooser.getSelectedFile();
					try {
						FileReader inputReader = new FileReader(filePath);
						textArea.read(inputReader, null);
						List<String> codeLines = Files.readAllLines(inputFile.toPath());
						if (inputFile.toString().matches(".*.wrebeca"))
							compileInfo.dynamic = true;
						else
							compileInfo.dynamic = false;
						DefaultMutableTreeNode r = createCodeTree(codeLines);
						DefaultTreeModel treeModel = (DefaultTreeModel) trSource.getModel();
						treeModel.setRoot(r);
						treeModel.reload();

					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					System.out.println("No Selection ");
				}
			}

		});
		mntmOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (filePath != null) {
					// save
					saveFile();
					// new
					textArea = new RSyntaxTextArea(20, 60);
				}
			}
		});
		mnFile.add(mntmNew);
		mnFile.add(mntmOpen);

		mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();

			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		JMenu mnSource = new JMenu("Run");
		menuBar.add(mnSource);

		JMenuItem mntmCompile = new JMenuItem("Compile");
		mntmCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compile = new frmCompile(errorList);
				System.out.println("configure compiling options");
				compile.showFrm(filePath);
				// compile.showFrm();

			}
		});
		mnSource.add(mntmCompile);

		JMenuItem mntmCreateStateSpace = new JMenuItem("Create State Space");
		mntmCreateStateSpace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (inputFile != null) {
					String pkgName = inputFile.getName().toString().split("\\.")[0];
					String inputDirctory = inputFile.getParentFile().toString();

					JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
					FileOutputStream errorStream = null;
					FileOutputStream outputStream = null;
					try {
						errorStream = new FileOutputStream(inputDirctory + "//" + pkgName + "/Errors.txt");
						outputStream = new FileOutputStream(inputDirctory + "//" + pkgName + "/Output.txt");

					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					File[] translatedFiles = new File(inputDirctory + "//" + pkgName).listFiles((filter));
					String[] javaFiles = new String[translatedFiles.length];
					// javaFiles[0]="-Xlint:unchecked ";
					for (int i = 0; i < translatedFiles.length; i++) {
						javaFiles[i] = translatedFiles[i].getPath();
					}
					if (outputStream != null && errorStream != null) {
						System.out.println(
								"Start compiling translated files of model " + pkgName + " in " + inputFile.toString());
						int compilationResult = compiler.run(null, outputStream, errorStream, javaFiles);
						System.out.println(compilationResult);

						if (compilationResult == 0) {
							System.out.println("Compilation is successful");
							try {
								System.out.println("Start creating the state space");
								// System.out.println(System.getProperty("java.class.path"));
								runProcess("java -cp \"" + inputDirctory + "\";" + System.getProperty("java.class.path")
										+ " " + pkgName + ".modeler \"" + inputDirctory + "\\" + pkgName + "\"");
							} catch (Exception ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
							}
						} else {

							System.out.println("Compilation Failed");
						}
					}
				}

			}
		});
		mnSource.add(mntmCreateStateSpace);

		JMenuItem mntmVerify = new JMenuItem("Verify");
		mntmVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pkgName = inputFile.getName().toString().split("\\.")[0];
				String inputDirctory = inputFile.getParentFile().toString();
				File modelerFile = new File(inputDirctory + "/" + pkgName + "/modeler.java");
				File invFile;
				if (modelerFile.exists()) {
					chooser = new JFileChooser();
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setDialogTitle("Select a File");
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("invariant file", "java");
					chooser.setFileFilter(filter);
					if (chooser.showOpenDialog(mntmOpen) == JFileChooser.APPROVE_OPTION) {
						System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
						System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
						invFile = chooser.getSelectedFile();
						String invariant;
						String modeler;
						try {
							invariant = FileToString(invFile);
							modeler = FileToString(modelerFile);
							modeler = modeler.replace("//Invariant Part", invariant);
							String[] invFunctions;

							invFunctions = invariant.split("//Invariant");
							String invInsertion = "Method inv; \n try { ";

							for (int i = 0; i < invFunctions.length; i++) {
								int start = invFunctions[i].indexOf("public");
								if (start != -1) {
									String invar = invFunctions[i].substring(start + "public Boolean".length() + 1,
											invFunctions[i].indexOf("(")).trim();
									invInsertion += "inv = this.getClass().getDeclaredMethod (\"" + invar
											+ "\",new Class[]{glState.class,Object[].class}); \n this.addInvariant(inv) ;";
								}
							}
							invInsertion += "} catch (NoSuchMethodException e) {e.printStackTrace();} catch (SecurityException e) {e.printStackTrace();}";

							modeler = modeler.toString().replace("//Adding Invariants", invInsertion);
							FileWriter writer = new FileWriter(modelerFile);
							writer.write(modeler);
							writer.close();

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// invFile.toString().split("//Invariant");
					} else {
						System.out.println("No Selection ");
					}

				}

			}

		});
		mnSource.add(mntmVerify);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmUserManual = new JMenuItem("User Manual");
		mnHelp.add(mntmUserManual);

		JPanel cpCenter = new JPanel(new BorderLayout());
		frame.setContentPane(cpCenter);
		JPanel cp = new JPanel(new BorderLayout());
		textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		// cp.add(sp);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		cp.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(sp);

		JSplitPane splitPaneV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trSource, cp);

		cpCenter.add(splitPaneV);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(tabbedPane.getSize().width, (int) (frame.getHeight() * 0.3)));
		// cp.add(tabbedPane, BorderLayout.SOUTH);
		splitPane.setRightComponent(tabbedPane);

		// JSplitPane splitPaneV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,);

		// s cp.add(splitPane, BorderLayout.CENTER);

		// PrintStream standardOut = System.out;
		// PrintStream standardErr = System.err;

		JTextArea txtrOutput = new JTextArea();
		PrintStream printStream = new PrintStream(new CustomOutputStream(txtrOutput));
		System.setOut(printStream);
		System.setErr(printStream);
		// standardOut.println("hey to consol");
		// txtrOutput.setTabSize(100);
		JScrollPane scrollPaneOut = new JScrollPane();
		tabbedPane.addTab("Output", null, scrollPaneOut, null);
		scrollPaneOut.setViewportView(txtrOutput);

		JScrollPane scrollPane = new JScrollPane();

		tabbedPane.addTab("Errors", null, scrollPane, null);
		errorList = new JTable();
		scrollPane.setViewportView(errorList);
		errorList.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Line", "Description" }));

		errorList.getColumnModel().getColumn(1).setPreferredWidth(548);

		trSource.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) trSource.getLastSelectedPathComponent();

				/* if nothing is selected */
				if (node == null)
					return;

				/* retrieve the node that was selected */
				// Object nodeInfo = node.getUserObject();

				// textArea.setCaret(new Caret());
				System.out.println(textArea.getCaretLineNumber());

				// .setActiveLineRange(20, 25);
				// .setSelectedOccurrenceText("Node");
				// .setSelectionStart(25);
				try {
					textArea.addLineHighlight(25, Color.CYAN);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// getui

			}
		});

	}

	private static void runProcess(String command) throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		final InputStream stream = p.getInputStream();
		new Thread(new Runnable() {
			public void run() {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(stream));
					String line = null;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
				} catch (Exception e) {
					// TODO
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
		}).start();
		// ProcessBuilder pb = new ProcessBuilder(command);
		// pb.redirectOutput(Redirect.INHERIT);
		// pb.redirectError(Redirect.INHERIT);
		// Process p = pb.start();
	}

	private void saveFile() {
		if (filePath == null) {
			chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Select a File");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(true);
			FileNameExtensionFilter wRebecaFilter = new FileNameExtensionFilter(
					"wireless Rebeca for dynamic topology(.wRebeca)", "wRebeca");
			FileNameExtensionFilter bRebecaFilter = new FileNameExtensionFilter(
					"broadcasting Rebeca for static topology(.bRebeca)", "bRebeca");
			chooser.addChoosableFileFilter(bRebecaFilter);
			chooser.addChoosableFileFilter(wRebecaFilter);
			if (chooser.showSaveDialog(mntmSave) == JFileChooser.APPROVE_OPTION) {
				filePath = chooser.getSelectedFile().getPath();
			}

		}
		if (filePath != null) {
			try {
				textArea.write(new FileWriter(filePath));
				textArea.read(new FileReader(filePath), null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	// Hash<String,Integer>
	private DefaultMutableTreeNode createCodeTree(List<String> codeLines) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(inputFile.getName().toString().split("\\.")[0]);
		DefaultMutableTreeNode rC = null;
		DefaultMutableTreeNode child = null;
		int count = 0, ch = 0;
		for (int i = 0; i < codeLines.size(); i++) {
			if (codeLines.get(i).startsWith("reactiveclass")) {
				rC = new DefaultMutableTreeNode(i);
				rC.setUserObject(codeLines.get(i)
						.substring(codeLines.get(i).indexOf("reactiveclass") + "reactiveclass".length() + 1,
								codeLines.get(i).indexOf("("))
						.trim());
				ch = 0;
				root.insert(rC, count++);
			} else if (codeLines.get(i).trim().startsWith("statevars")) {
				child = new DefaultMutableTreeNode(i);
				child.setUserObject("state variables");
				rC.insert(child, ch++);
			} else if (codeLines.get(i).trim().startsWith("msgsrv")) {
				child = new DefaultMutableTreeNode(i);
				child.setUserObject(
						codeLines.get(i).substring(codeLines.get(i).indexOf("msgsrv") + "msgsrv".length() + 1,
								codeLines.get(i).indexOf("(")).trim());
				rC.insert(child, ch++);
			} else if (codeLines.get(i).trim().startsWith("main")) {
				child = new DefaultMutableTreeNode(i);
				child.setUserObject("main");
				rC.insert(child, ch++);
			}

		}
		return root;
	}

	private String FileToString(File f) {
		String str = "";
		try {
			List<String> lines = Files.readAllLines(f.toPath());
			for (String line : lines) {
				str += "\n" + line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str;
	}
}
