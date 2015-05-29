package view;

import impl.calculerBleuDaoImpl;
import impl.saveSourceDaoImpl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.Bleumod;
import model.Focusmod;

import dao.calculerBleuDao;
import dao.saveSourceDao;

public class Blue_interface extends JFrame implements ActionListener, MouseListener{
	JPanel jp1, jp2, jp3, jp4, jp5, jp6, jp7, jp8;
	JLabel jl1, jl2, jl3, jl4, jl5, jl6, jl7, jl8;
	List<JLabel> list = new ArrayList<JLabel>();
	JButton jb1, jb2, jb3, jb5;
	JTextField jt1, jt2, jt3, jt5, jt6;
	JTextField jtf;
	JTable jtb;
	JScrollPane jsp;
	DefaultTableModel model = new DefaultTableModel();
	JCheckBox jcb;
	JCheckBox jcb1;
	JTextArea jaw;
	
	double[] correct;// sauvegarder des nombre des mots que Mt mérite référence
	double[] len; // nombre de mots dans chaque N-gram
	
	double d=0.2;
	int n=4;
	boolean flag = false;
	boolean flagFocus = false;
	
	// sauvegarder des paths de fichier Mt
	List<String> mtname = new ArrayList<String>();
	
	// sauvegarder des paths de fichier référence
	List<String> rfname = new ArrayList<String>();
	
	// sauvegarder des paths de fichier focusname
	List<String> focusname = new ArrayList<String>();
	

	
	ArrayList[] mtContext = null;// sauvegarder des contexte de chaque Mt
	ArrayList[] rfContext = null;// sauvegarder des contexte de chaque
	File[] focusFile = null;// créer des files selon de nombre de focus
	ArrayList[] focusContext = null;// sauvegarder des contexte de chaque
	Focusmod focus;
	
	public Blue_interface() {
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();
		jp4 = new JPanel();
		jp5 = new JPanel();
		jp6 = new JPanel();
		jp7 = new JPanel();
		jp8 = new JPanel();

		jl1 = new JLabel("Text Translation:");
		jl2 = new JLabel("Text Reference:");
		jl3 = new JLabel("");
		jl4 = new JLabel("N-gram : ");
		jl5 = new JLabel("");
		jl6 = new JLabel("");
		jl7 = new JLabel("Text Focus : ");
		jl8 = new JLabel("d : ");
		jt6 = new JTextField(10);
		jt6.setText("0.2");
		jtf = new JTextField(10);
		jtf.setText("4");

		
		jcb = new JCheckBox("IngnorCase");
		jcb1 = new JCheckBox("Use Focus");

		jb1 = new JButton("Upload");
		jb1.addActionListener(this);
		jb1.setActionCommand("choix-mt");

		jb2 = new JButton("Upload");
		jb2.addActionListener(this);
		jb2.setActionCommand("choix-rf");

		jb3 = new JButton("Calculation");
		jb3.addActionListener(this);
		jb3.setActionCommand("calculer");

		jb5 = new JButton("Upload");
		jb5.addActionListener(this);
		jb5.setActionCommand("focus");

		jt1 = new JTextField(20);
		jt2 = new JTextField(20);
		jt3 = new JTextField(10);
		jt5 = new JTextField(20);

		this.setLayout(new GridLayout(8, 1));
		jp1.add(jl1);
		jp1.add(jt1);
		jp1.add(jb1);
		jp1.setLayout(new FlowLayout(FlowLayout.LEFT));

		jp2.add(jl2);
		jp2.add(jb2);
		jp6.setLayout(new GridLayout(5, 0));
		jp2.add(jp6);
		jp2.setLayout(new FlowLayout(FlowLayout.LEFT));

		jp3.add(jl4);
		jp3.add(jtf);
		jp3.add(jcb);
		jp3.add(jb3);
		jp3.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp4.setLayout(new GridLayout(3, 1));
		jp4.add(jl6);
		jp4.add(jl3);
		jp4.add(jl5);

		jp7.add(jl7);
		jp7.add(jt5);
		jp7.add(jb5);
		jp7.setLayout(new FlowLayout(FlowLayout.LEFT));

		jp8.add(jl8);
		jp8.add(jt6);
		jp8.add(jcb1);
		jp8.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		jaw = new JTextArea();
		jaw.setForeground(Color.RED);
		jaw.setLineWrap(true);        
		jaw.setWrapStyleWord(true); 	
		JScrollPane js = new JScrollPane(jaw);

		this.add(jp1);
		this.add(jp7);
		this.add(jp8);
		this.add(jp2);
		this.add(jp3);
		this.add(jp5);
		this.add(jp4);
		this.add(js);

		this.setTitle("Blue");
		this.setSize(500, 720);
		this.setResizable(false);
		this.setLocation(450, 10);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	
	
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getActionCommand().equals("choix-mt")) {
				jt1.setText("");
				mtname.clear();
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Choose your MT....");
				jfc.showOpenDialog(null);
				jfc.setVisible(true);
				String filename1 = jfc.getSelectedFile().getAbsolutePath();
				mtname.add(filename1);
				String namelist = "";
				for (int i = 0; i < mtname.size(); i++) {
					namelist += mtname.get(i) + "  ";
				}
				jt1.setText(namelist);
			}

			if (e.getActionCommand().equals("focus")) {
				jt5.setText("");
				focusname.clear();
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Choose your Focus....");
				jfc.showOpenDialog(null);
				jfc.setVisible(true);
				String filename1 = jfc.getSelectedFile().getAbsolutePath();
				focusname.add(filename1);
				jt5.setText(filename1);
			}
		if (e.getActionCommand().equals("choix-rf")) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Choose your Reference....");
			jfc.showOpenDialog(null);
			jfc.setVisible(true);
			String filename1 = jfc.getSelectedFile().getAbsolutePath();
			rfname.add(filename1);

			JLabel jname = new JLabel();
			jp6.add(jname);
			jname.setText(filename1);
			jname.addMouseListener(this);
		}
		if (e.getActionCommand().equals("calculer")) {			
			calculerBleuDaoImpl calcule = new calculerBleuDaoImpl();
			double m = Double.parseDouble(jtf.getText());
			if (m<1) {
				javax.swing.JOptionPane.showMessageDialog(null,
						"sorry, n is integer, n >= 1, defaut n=4 !");
				jtf.setText("4");
				n=4;
			}
			n = Integer.parseInt(jtf.getText());
			flag = jcb.isSelected();
			flagFocus = jcb1.isSelected();
			saveSourceDao saveSource = new saveSourceDaoImpl();
			if (flagFocus) {
				focus = new Focusmod();
				focus.setFlagFocus(flagFocus);
				focus.setFocusname(focusname);
				focusContext = saveSource.saveSource(focusname);
				focus.setFocusContext(focusContext);
				d = Double.parseDouble(jt6.getText());
				if (d<0||d>1) {
					javax.swing.JOptionPane.showMessageDialog(null,
							"sorry, n is real, 0<=d<=1, defaut d=0.2 !");
					jt6.setText("0.2");
					d=0.2;		
				}
				focus.setD(d);		
			}else{
				focus = new Focusmod();
			}
			mtContext = saveSource.saveSource(mtname);
			rfContext = saveSource.saveSource(rfname);			
			jaw.setText("");
			
			Bleumod bleu = new Bleumod();
			bleu = calcule.calculeBleu(n, mtContext, rfContext,focus,flag);
			correct = bleu.getCorrect();
			len = bleu.getLen();
			
			Object[][] tableData = new Object[n][3];
			for (int i = 0; i < n; i++) {
				tableData[i][0] = i + 1 + "gram";
				tableData[i][1] = correct[i];
				tableData[i][2] = len[i];
			}
			Object[] columnTitle = { "N-gram", "Count_Cor", "Count_words" };
			model.setDataVector(tableData, columnTitle);
			jtb = new JTable(model);
			Dimension a = new Dimension();
			a.height = 60;
			a.width = 450;
			jtb.setPreferredScrollableViewportSize(a);
			jsp = new JScrollPane(jtb);
			jp5.add(jsp);

			jl6.setText("   MT_length : " + bleu.getLens() + "    " + " RF_length : "
					+ bleu.getLenr());
			jl3.setText("   bp : " + bleu.getBp());
			jl5.setText("   Bleu : " + bleu.getBlue());
			if(calcule.getWarninglist().size()>0){
				for(int i=0;i<calcule.getWarninglist().size();i++){
					String war = calcule.getWarninglist().get(i).toString()+"\r\n";
					jaw.append(war);
				}
			}
			calcule.setWarninglist(null);
		}
		} catch (Exception ex) {
		}
	}

	public void mouseClicked(MouseEvent e) {

		System.out.println(e.getSource());
		JLabel j = (JLabel) e.getSource();
		System.out.println(j.getText());
		for (int i = 0; i < rfname.size(); i++) {
			if (j.getText().equals(rfname.get(i))) {
				rfname.remove(i);
			}
		}
		jp6.remove(j);
		this.validate();
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
