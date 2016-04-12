package wRebeca.tool;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import translator.Translate;
import translator.errorInfo;

public class frmCompile {

	private JDialog frame;
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
	JRadioButton rdbtnQueue ;
	JRadioButton rdbtnBag;
	JCheckBox rdbtnApplyReduction ;
	JCheckBox chckbxClts;
	JCheckBox chckbxLts;
	JButton btnCancel;
	JButton btnOk;
	/**
	 * Create the application.
	 */
	public frmCompile(JTable errorlist) {
		initialize(errorlist);
	}
	
	static String  filePath="";

	public boolean showFrm(String filePath_)
	{	
		filePath=filePath_;
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

		return compileInfo.compile;
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(JTable errorlist) {
		frame = new JDialog();
		frame.setBounds(100, 100, 296, 161);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle("Select Compile Status");
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
				compileInfo.compile=false;
				frame.dispose();
				frame.setVisible(false);
			}
		});
		
		 chckbxClts = new JCheckBox("clts");
	
		 chckbxLts = new JCheckBox("lts");
		 btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			   compileInfo.queue=rdbtnQueue.isSelected();
			   compileInfo.bag=rdbtnBag.isSelected();
			   compileInfo.reduction=rdbtnApplyReduction.isSelected();
			   compileInfo.lts=chckbxLts.isSelected();
			   compileInfo.clts=chckbxClts.isSelected();
			   compileInfo.compile=true;	
				System.out.println("Compiling the file:"+filePath+"Storage type:"+(compileInfo.queue?"Queue":"Bag")
						+(compileInfo.reduction?"with applying reduction":"without applying reduction"));
				
				if(compileInfo.compile)
				{
					Translate trans=new Translate(filePath);
					List<errorInfo> translationErrors=trans.doTranslatation();
					DefaultTableModel listModel = (DefaultTableModel) errorlist.getModel();				
					for(errorInfo error:translationErrors)
					{
						listModel.addRow(new Object[]{error.getLine(),error.getDescrition()});
					}
					

				}
				frame.dispose();
				frame.setVisible(false);
				
			}
		});
		ButtonGroup group = new ButtonGroup();
	    group.add(rdbtnQueue);
	    group.add(rdbtnBag);

	    

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(16)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(rdbtnBag)
							.addContainerGap(221, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(rdbtnQueue)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(21)
									.addComponent(btnOk)))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
									.addComponent(btnCancel)
									.addGap(30))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(47)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(chckbxClts)
										.addComponent(rdbtnApplyReduction)
										.addComponent(chckbxLts))
									.addContainerGap())))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnApplyReduction)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnQueue)
						.addComponent(chckbxLts))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnBag)
						.addComponent(chckbxClts))
					.addPreferredGap(ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}




}
