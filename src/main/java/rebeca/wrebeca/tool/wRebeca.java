package rebeca.wrebeca.tool;

/**
 * @author Behnaz Yousefi
 *
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
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
import java.nio.file.Paths;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class wRebeca {

    private JFrame frame;
    private JMenuItem mntmOpen;
    JMenuItem mntmCompile;
    JMenu mnVerification;
    JTree trSource;
    JMenuItem mntmCreateStateSpace;
    JMenu mnSource;
    JMenu mnFile;
    RTextScrollPane sp;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            // TODO Auto-generated catch block
        }

        EventQueue.invokeLater(() -> {
            try {
                
                wRebeca window = new wRebeca();
                window.frame.setVisible(true);
                
            } catch (Exception e) {
            }
        });
    }

    private boolean create_stateSpace() {
        FileFilter classfilter = (File pathname) -> pathname.toString().matches(".*.class");
        boolean succ = false;
        if (inputFile != null) {
            String pkgName = inputFile.getName().split("\\.")[0];
            String inputDirctory = inputFile.getParentFile().toString();

            FileOutputStream errorStream = null;
            FileOutputStream outputStream = null;
            try {
                errorStream = new FileOutputStream(inputDirctory + "//" + pkgName + "/Errors.txt");
                outputStream = new FileOutputStream(inputDirctory + "//" + pkgName + "/Output.txt");

            } catch (FileNotFoundException e1) {
            }
            ////
            File modelerFile = new File(inputDirctory + "//" + pkgName + "//modeler.java");
            String changedModeler = FileToString(modelerFile);

            String stateSpaceConfig = "super(" + compileInfo.getInstance().isClts() + ","
                    + compileInfo.getInstance().isMcrl() + "," + compileInfo.getInstance().isLts()
                    + "," + compileInfo.getInstance().getMax_thread_num() + ");";
            System.out.println(stateSpaceConfig);
            changedModeler = changedModeler.replace("super(false,false,true,4);", stateSpaceConfig);

                try (FileWriter writer = new FileWriter(modelerFile)) {
                    writer.write(changedModeler);
                } catch (IOException e) {
                }
            
            File[] classFiles = new File(inputDirctory + "//" + pkgName).listFiles((classfilter));
            for (int i = 0; i < classFiles.length; i++) {
                try {
                    Files.deleteIfExists(classFiles[i].toPath());
                } catch (IOException ex) {
                    Logger.getLogger(wRebeca.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(System.getenv("JAVA_HOME") == null){
                JOptionPane.showMessageDialog(frame, "You need to install the Java Development Kit (JDK) version 8 or later and set the JAVA_HOME variable properly.", "Error", 0);
                return false;
            }
            System.setProperty("java.home", System.getenv("JAVA_HOME"));
            if (outputStream != null && errorStream != null) {

                System.out.println("Compilation is successful");
                System.out.println("state space creation is started with the following configuration:");
                System.out.println("Storage type: " + (compileInfo.getInstance().isQueue() ? "Queue" : "Bag") + " "
                        + (compileInfo.getInstance().isReduction() ? " with applying reduction" : " without applying reduction"));
                try {
                    System.out.println("javac -cp \"" + inputDirctory + "\";" + System.getProperty("java.class.path")
                            +"\" "+ inputDirctory + "\\" + pkgName + "\\*.java");
                    succ = runProcess("javac -cp \"" + inputDirctory + "\";" + System.getProperty("java.class.path")
                            +" "+ inputDirctory + "\\" + pkgName + "\\*.java\"");
                    process.join();
                    if (Files.exists(Paths.get(inputDirctory + "\\" + pkgName + "\\modeler.java"))) {
                        System.out.println("Start creating the state space");
                        System.out.println(System.getProperty("java.class.path"));
                        System.out.println("java -cp \"" + inputDirctory + "\";" + System.getProperty("java.class.path")
                                + ((compileInfo.getInstance().getHeapSize()!=0)?(" -Xmx" + compileInfo.getInstance().getHeapSize()+"m "):" ")+ pkgName + ".modeler \"" + inputDirctory + "\\" + pkgName + "\"");
                        succ = runProcess("java -cp \"" + inputDirctory + "\";" + System.getProperty("java.class.path")
                                + ((compileInfo.getInstance().getHeapSize()!=0)?(" -Xmx" + compileInfo.getInstance().getHeapSize()+"m "):" ") + pkgName + ".modeler \"" + inputDirctory + "\\" + pkgName + "\"");
                    }

                } catch (Exception ex) {
                    // TODO Auto-generated catch block
                    System.out.println("Sorry!!! State space generation is not possible duo to some errors");
                }
            } else {

                System.out.println("Compilation Failed");
            }
        }
        return succ;
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

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        mnFile = new JMenu("File");
        menuBar.add(mnFile);

        mntmOpen = new JMenuItem("Open");
        mntmOpen.addActionListener((ActionEvent e) -> {
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
                System.out.println("CurrentDirectory: " + chooser.getCurrentDirectory());
                System.out.println("SelectedFile: " + chooser.getSelectedFile());
                filePath = chooser.getSelectedFile().getPath();
                inputFile = chooser.getSelectedFile();
                try {
                    FileReader inputReader = new FileReader(filePath);
                    textArea.read(inputReader, null);
                    List<String> codeLines = Files.readAllLines(inputFile.toPath());
                    if (inputFile.toString().matches(".*.rebeca")) {
                        compileInfo.getInstance().setClassic(true);
                    } else if (inputFile.toString().matches(".*.wrebeca")) {
                        compileInfo.getInstance().setDynamic(true);
                    } else {
                        compileInfo.getInstance().setDynamic(false);
                    }
                    DefaultMutableTreeNode r = createCodeTree(codeLines);
                    DefaultTreeModel treeModel1 = (DefaultTreeModel) trSource.getModel();
                    treeModel1.setRoot(r);
                    treeModel1.reload();
                    mnVerification.setEnabled(false);
                    mntmCompile.setEnabled(true);
                    mntmCreateStateSpace.setEnabled(false);
                }catch (FileNotFoundException e1) {
                }catch (IOException e1) {
                    // TODO Auto-generated catch block
                }
            }
        });
        mntmOpen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });

        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.addActionListener((ActionEvent e) -> {
            if (filePath != null) {
                // save
                saveFile();
                // new
                inputFile = null;
                filePath = null;
                try {
                    File file = new File("newFile.txt");
                    if (!Files.exists(file.toPath())) {
                        file.createNewFile();
                    }
                    textArea.read(new FileReader("newFile.txt"), null);
                    treeModel.setRoot(new DefaultMutableTreeNode("Nothing"));
                    treeModel.reload();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        mnFile.add(mntmNew);
        mnFile.add(mntmOpen);

        mntmSave = new JMenuItem("Save");
        mntmSave.addActionListener((ActionEvent e) -> {
            saveFile();
        });
        mnFile.add(mntmSave);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        mnFile.add(mntmExit);

        mnSource = new JMenu("Run");
        menuBar.add(mnSource);

        mntmCompile = new JMenuItem("Compile");
        mntmCompile.setEnabled(false);
        mntmCompile.addActionListener((ActionEvent e) -> {
            if (inputFile == null) {
                saveFile();
            }
            if (inputFile != null) {
                compile = new frmCompile(errorList, frame);
                System.out.println("configure compiling options");
                compile.showFrm(filePath);
            }
            // compile.showFrm();
        });
        mnSource.add(mntmCompile);

        mntmCreateStateSpace = new JMenuItem("Create State Space");
        mntmCreateStateSpace.setEnabled(false);
        mntmCreateStateSpace.addActionListener((ActionEvent e) -> {
            if (compileInfo.getInstance().isMcrl()) {
                if (!compileInfo.getInstance().isClts()) {
                    compileInfo.getInstance().setLts(true);
                }
                compileInfo.getInstance().setMcrl(false);
            }
            create_stateSpace();
        });
        mnSource.add(mntmCreateStateSpace);
        mnVerification = new JMenu("Verification");
        mnVerification.setEnabled(false);
        mnSource.add(mnVerification);

        JMenuItem mntmInvariant = new JMenuItem("Invariant");
        mntmInvariant.addActionListener((ActionEvent e) -> {
            load_invariant();
            compileInfo.getInstance().setMcrl(false);
            compileInfo.getInstance().setLts(true);
            //create_stateSpace();
        });
        mnVerification.add(mntmInvariant);

        JMenuItem mntmMcrl = new JMenuItem("mcrl");
        mntmMcrl.addActionListener((ActionEvent e) -> {
            compileInfo.getInstance().setMcrl(true);
            compileInfo.getInstance().setLts(false);
            String pkgName = inputFile.getName().toString().split("\\.")[0];
            String inputDirctory = inputFile.getParentFile().toString();
            
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select a File");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileNameExtensionFilter formulaFilter = new FileNameExtensionFilter("textual	µ-Calculus(.mcf)",
                    "mcf");
            chooser.setFileFilter(formulaFilter);
            if (chooser.showOpenDialog(mntmOpen) == JFileChooser.APPROVE_OPTION) {
                System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                try {
                    Files.deleteIfExists(Paths.get(inputDirctory + "/" + pkgName + "/Output/stateSpaceMcrl.aut"));
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                if (create_stateSpace()) {
                    new Thread(new Runnable() {
                        public void run() {
                            while (!Files.exists(
                                    Paths.get(inputDirctory + "/" + pkgName + "/Output/stateSpaceMcrl.aut"))) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                            try {
                                String command = "";
                                if (Files.exists(
                                        Paths.get(inputDirctory + "/" + pkgName + "/Output/stateSpaceMcrl.aut"))) {
                                    command = "lts2pbes \"" + inputDirctory + "/" + pkgName
                                            + "/Output/stateSpaceMcrl.aut" + "\" \"" + inputDirctory + "/" + pkgName
                                            + "/Output/stateSpaceMcrl.pbes" + "\"  --data=\"" + inputDirctory + "/"
                                            + pkgName + "/Output/state_space.mcrl2" + "\" --formula=\""
                                            + chooser.getSelectedFile().getPath() + "\"";
                                    System.out.println(command);
                                    runProcess(command);
                                    if (Files.exists(Paths
                                            .get(inputDirctory + "/" + pkgName + "/Output/stateSpaceMcrl.pbes"))) {
                                        command = "pbes2bool \"" + inputDirctory + "/" + pkgName
                                                + "/Output/stateSpaceMcrl.pbes"
                                                + "\" --erase=none --rewriter=jitty --search=breadth-first --strategy=0 --verbose";
                                        System.out.println(command);
                                        runProcess(command);
                                    }
                                }
                            } catch (Exception e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        mnVerification.add(mntmMcrl);

        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);

        JMenuItem mntmUserManual = new JMenuItem("User Manual");
        mnHelp.add(mntmUserManual);
        mntmUserManual.addActionListener((ActionEvent e) -> { 
            
            if (Desktop.isDesktopSupported()) {
            // File in user working directory, System.getProperty("user.dir");
            File file = new File("UserManual.pdf");
            if (!file.exists()) {
                // In JAR
                InputStream inputStream = ClassLoader.getSystemClassLoader()
                                    .getResourceAsStream("UserManual.pdf");
                // Copy file
                OutputStream outputStream;
                try {               
                outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(wRebeca.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(wRebeca.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                try {
                    java.awt.Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    Logger.getLogger(wRebeca.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
//        try { 
//            File file = new File(getClass().getClassLoader().getResource("UserManual.pdf").getFile());
//            java.awt.Desktop.getDesktop().open(file);
//        } catch (IOException ex) {
//            Logger.getLogger(wRebeca.class.getName()).log(Level.SEVERE, null, ex);
//        }
        });
        
        JMenuItem mntmAbout = new JMenuItem("About");
        mnHelp.add(mntmAbout);
        mntmAbout.addActionListener((ActionEvent e) -> {           
            JOptionPane.showMessageDialog(frame, "https://github.com/b-yousefi/wRebeca","About", 1);
        });
                
        JPanel cpCenter = new JPanel(new BorderLayout());
        frame.setContentPane(cpCenter);
        JPanel cp = new JPanel(new BorderLayout());
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        sp = new RTextScrollPane(textArea);
        // cp.add(sp);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        cp.add(splitPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(sp);

        JSplitPane splitPaneV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trSource, cp);

        cpCenter.add(splitPaneV);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(tabbedPane.getSize().width, (int) (frame.getHeight() * 0.3)));
        splitPane.setRightComponent(tabbedPane);

        JTextArea txtrOutput = new JTextArea();
        PrintStream printStream = new PrintStream(new CustomOutputStream(txtrOutput));
        System.setOut(printStream);
        System.setErr(printStream);
        JScrollPane scrollPaneOut = new JScrollPane();
        tabbedPane.addTab("Output", null, scrollPaneOut, null);
        scrollPaneOut.setViewportView(txtrOutput);

        JScrollPane scrollPane = new JScrollPane();

        tabbedPane.addTab("Errors", null, scrollPane, null);
        errorList = new JTable();
        scrollPane.setViewportView(errorList);
        errorList.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Line", "Description"}));

        errorList.getColumnModel().getColumn(1).setPreferredWidth(548);

        trSource.addTreeSelectionListener((TreeSelectionEvent e) -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) trSource.getLastSelectedPathComponent();
            
            /* if nothing is selected */
            if (node == null) {
                return;
            }
            
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
            }
            // getui
        });

    }

    static Thread process;

    private static boolean runProcess(String command) throws Exception {
        Process p = Runtime.getRuntime().exec(command);
        final InputStream stream = p.getInputStream();
        if (process == null || !process.isAlive()) {
            process = new Thread(new Runnable() {
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
            });
            process.start();
            return true;
        } else {
            System.out.println("Program is busy!!!!! Please wait");
            return false;
        }
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
                inputFile = chooser.getSelectedFile();
            }

        }
        if (filePath != null) {
            try {
                textArea.write(new FileWriter(filePath));
                textArea.read(new FileReader(filePath), null);
                mntmCompile.setEnabled(true);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
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
        }

        return str;
    }

    private void load_invariant() {
        String pkgName = inputFile.getName().split("\\.")[0];
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
                System.out.println("invariant file: " + chooser.getSelectedFile());
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
                            String invar = invFunctions[i]
                                    .substring(start + "public Boolean".length() + 1, invFunctions[i].indexOf("("))
                                    .trim();
                            invInsertion += "inv = this.getClass().getDeclaredMethod (\"" + invar
                                    + "\",new Class[]{GlobalState.class,Object[].class}); \n this.addInvariant(inv) ;";
                        }
                    }
                    invInsertion += "} catch (NoSuchMethodException e) {e.printStackTrace();} catch (SecurityException e) {e.printStackTrace();}";

                    modeler = modeler.replace("//Adding Invariants", invInsertion);
                    FileWriter writer = new FileWriter(modelerFile);
                    writer.write(modeler);
                    writer.close();
                    System.out.println("invariant file has been succefully loaded");
                    System.out.println("state space creation is started to check the given invariants");
                    create_stateSpace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    System.out.println("there has been a problem while loading the invariant file");

                    e1.printStackTrace();
                }
                // invFile.toString().split("//Invariant");
            } else {
                System.out.println("No Selection ");
            }

        }

    }

}
