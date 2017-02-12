package rebeca.wrebeca.tool;

/**
 * @author Behnaz Yousefi
 *
 */

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import rebeca.translator.Translate;
import rebeca.translator.errorInfo;
import javax.swing.JFormattedTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;

public class frmCompile {

    private JFrame frame;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
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

    }
    JRadioButton rdbtnQueue;
    JRadioButton rdbtnBag;
    JCheckBox rdbtnApplyReduction;
    JCheckBox chckbxClts;
    JCheckBox chckbxLts;
    JCheckBox chckbxTimed;
    JButton btnCancel;
    JButton btnOk;
    JFrame mainWindow;
    JFormattedTextField txtHeapSize;

    /**
     * Create the application.
     *
     * @param parent
     * @wbp.parser.entryPoint
     */
    public frmCompile(JTable errorlist, JFrame parent) {
        mainWindow = parent;
        initialize(errorlist);

    }

    static String filePath = "";
    private JFormattedTextField txtMaxThread;

    public boolean showFrm(String filePath_) {
        filePath = filePath_;
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //frmCompile window = new frmCompile(errorlist);
//					frame.setModalityType(ModalityType.APPLICATION_MODAL);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return compileInfo.getInstance().isCompile();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(JTable errorlist) {
        frame = new JFrame();
        frame.setBounds(100, 100, 296, 161);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("Compile Configuration");
        //setResizable(false);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        frame.getContentPane().add(contentPane);

        rdbtnQueue = new JRadioButton("Queue");
        rdbtnQueue.setSelected(true);

        rdbtnBag = new JRadioButton("Bag");

        rdbtnApplyReduction = new JCheckBox("Apply Reduction");
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compileInfo.getInstance().setCompile(false);
                frame.dispose();
                frame.setVisible(false);
            }
        });

        chckbxClts = new JCheckBox("clts");

        chckbxLts = new JCheckBox("lts");
        chckbxTimed = new JCheckBox("timed");
        btnOk = new JButton("Ok");
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compileInfo.getInstance().setQueue (rdbtnQueue.isSelected());
                compileInfo.getInstance().setBag(rdbtnBag.isSelected());
                compileInfo.getInstance().setReduction(rdbtnApplyReduction.isSelected());
                compileInfo.getInstance().setLts(chckbxLts.isSelected());
                compileInfo.getInstance().setClts(chckbxClts.isSelected());
                compileInfo.getInstance().setTimed(chckbxTimed.isSelected());
                compileInfo.getInstance().setCompile(true);
                compileInfo.getInstance().setMax_thread_num(!txtMaxThread.getText().isEmpty()?Integer.parseInt(txtMaxThread.getText()):0);
                compileInfo.getInstance().setHeapSize(!txtHeapSize.getText().isEmpty()?Integer.parseInt(txtHeapSize.getText()):0);
                System.out.println("Compiling the file: " + filePath + " Storage type: " + (compileInfo.getInstance().isQueue() ? "Queue" : "Bag")
                        + (compileInfo.getInstance().reduction ? " with applying reduction" : " without applying reduction"));

                if (compileInfo.getInstance().isCompile()) {
                    Translate trans = new Translate(filePath);
                    List<errorInfo> translationErrors = trans.doTranslatation(compileInfo.getInstance());

                    DefaultTableModel listModel = (DefaultTableModel) errorlist.getModel();
                    for (int i = 0; i < listModel.getRowCount(); i++) {
                        listModel.removeRow(i);
                    }
                    for (errorInfo error : translationErrors) {
                        listModel.addRow(new Object[]{error.getLine(), error.getDescrition()});
                        System.out.println("Translation was not successfull. To see more information please go to the output tab");
                    }
                    if (translationErrors.size() == 0) {
                        mainWindow.getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
                        mainWindow.getJMenuBar().getMenu(1).getMenuComponent(2).setEnabled(true);
                        System.out.println("Translation is successfully done");
                    }

                }
                frame.dispose();
                frame.setVisible(false);

            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(rdbtnQueue);
        group.add(rdbtnBag);

        txtMaxThread = new JFormattedTextField();

        JLabel lblNewLabel = new JLabel("Number of threads");

        JLabel lblHeapSize = new JLabel("heap size(mb)");
        
        txtHeapSize = new JFormattedTextField();
        
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(31)
                                                .addComponent(btnOk)
                                                .addGap(118)
                                                .addComponent(btnCancel))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(10)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(lblNewLabel)
                                                        .addComponent(rdbtnQueue)
                                                        .addComponent(rdbtnBag))
                                                .addGap(4)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                                .addComponent(txtMaxThread, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(22)
                                                                .addComponent(lblHeapSize, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(txtHeapSize, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                                .addComponent(chckbxClts)
                                                                .addGap(10)
                                                                .addComponent(chckbxLts)
                                                        .addGap(10)
                                                        .addComponent(chckbxTimed))
                                                        .addComponent(rdbtnApplyReduction))))
                                .addGap(11))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(4)
                                                .addComponent(lblNewLabel))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(1)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(txtMaxThread, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblHeapSize)
                                                        .addComponent(txtHeapSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                                .addGap(7)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addComponent(rdbtnQueue)
                                        .addComponent(rdbtnApplyReduction))
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(3)
                                                .addComponent(rdbtnBag)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(btnOk)
                                                        .addComponent(btnCancel)))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(chckbxClts)
                                                        .addComponent(chckbxLts)
                                                        .addComponent(chckbxTimed)))))
        );
        contentPane.setLayout(gl_contentPane);
    }
}
