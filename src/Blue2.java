import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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

public class Blue2 extends JFrame implements ActionListener, MouseListener

{
	JPanel jp1, jp2, jp3, jp4, jp5, jp6, jp7, jp8;
	JLabel jl1, jl2, jl3, jl4, jl5, jl6, jl7, jl8;
	List<JLabel> list = new ArrayList<JLabel>();
	JButton jb1, jb2, jb3, jb4, jb5;
	JTextField jt1, jt2, jt3, jt5, jt6;
	JTextField jtf;
	JTable jtb;
	JScrollPane jsp;
	DefaultTableModel model = new DefaultTableModel();
	JCheckBox jcb;
	JCheckBox jcb1;

	static List<String> mtname = new ArrayList<String>();// sauvegarder des
	// paths de fichier
	// Mt
	static List<String> rfname = new ArrayList<String>();// sauvegarder des
	// paths de fichier
	// référence

	static List<String> focusname = new ArrayList<String>();
	static int n = 4;// initialisation N-gram
	static double d = 0.2;// initialisation N-gram
	static File[] mtFile = null; // créer des files selon de nombre de Mt
	static File[] rfFile = null;// créer des files selon de nombre de référence
	static ArrayList[] mtContext = null;// sauvegarder des contexte de chaque Mt
	static ArrayList[] rfContext = null;// sauvegarder des contexte de chaque
	static File[] focusFile = null;// créer des files selon de nombre de focus
	static ArrayList[] focusContext = null;// sauvegarder des contexte de chaque
	// focus
	// référence
	static double[] correct;// sauvegarder des nombre des mots que Mt mérite
	// référence
	static double[] len; // nombre de mots dans chaque N-gram
	static int lens, lenr;// nombre de mots Mt et référence
	static double bp;
	static double blue;
	static boolean flag = false;
	static boolean flagFocus = false;
	static int line = 0;

	public static void main(String[] args) {

		try {
			getoption g = new getoption(args, "lhn:f:d:");
			int c;
			int l = 0;
			boolean help = false;
			while ((c = g.getNextOption()) != -1) {
				switch (c) {
				case 'l':
					flag = true;
					break;
				case 'n':
					n = Integer.parseInt(g.getOptionArg());
					break;
				case 'f':
					flagFocus = true;
					break;
				case 'd':
					d = Double.parseDouble(g.getOptionArg());
					break;
				case 'h':
					help = true;
					break;
				case '?':
					break;
				default:
					System.out.println("c'est fault");
					break;
				}
			}
			if (help) {
				System.out
						.println("Executive order: Option (optional), TraductionMachine.txt reference.txt");
				System.out
						.println("Choose option:-h help, -l lowercase, -f add focus, -d add weight, -n choose ngram.");
				System.out.println("-h help you to use the Blue");
				System.out.println("-f fichier of focus must exist");
				System.out.println("-d 0-1 Real, defaut 0.2");
				System.out.println("-n >=1 and Integer, defaut 4");
				System.exit(0);
			}
			Pattern pattern = Pattern.compile("[0-9]");
			if (args.length == 0) {
				Blue2 de = new Blue2();
			} else {
				for (int i = 0; i < args.length; i++) {

					if (args[i].startsWith("-") || args[i].startsWith("0")
							|| pattern.matcher(args[i]).matches()) {
						l++;
						continue;
					}
					if (flagFocus) {
						if (i == l) {
							focusname.add(args[i]);
						}
						if (i == l + 1) {
							mtname.add(args[i]);
						}
						if (i > l + 1) {
							rfname.add(args[i]);
						}
					} else {
						if (i == l) {
							mtname.add(args[i]);
						}
						if (i > l) {
							rfname.add(args[i]);
						}
					}
				}
				saveMT(mtname);
				saveRF(rfname);
				if (flagFocus) {
					saveFocus(focusname);
					calculeBlue(n, mtContext, rfContext);
				} else {
					calculeBlue(n, mtContext, rfContext);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Choose option: -l lowercase, -f add focus, -d add weight -n ngram -h help.");
			System.out
					.println("Executive order: option (optional), TraductionMachine.txt reference.txt");
			System.exit(0);
		}
	}

	/*
	 * lire le ficher de mt selon la path de fichier
	 */
	public static void saveMT(List<String> mtName) throws IOException {
		if (!mtName.isEmpty()) {
			int jb = 0;
			mtFile = new File[mtName.size()];
			mtContext = new ArrayList[mtName.size()];
			for (int i = 0; i < mtName.size(); i++) {
				mtContext[i] = new ArrayList<String>();
				mtFile[i] = new File(mtName.get(i));
				if (!mtFile[i].exists() || !mtFile[i].isFile()) {
					javax.swing.JOptionPane.showMessageDialog(null,
							"sorry,The path is faulT !");
				}
				String temp = null;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(mtFile[i])));
				while ((temp = br.readLine()) != null) {
					jb++;
					mtContext[i].add(temp);
				}
				br.close();
			}
		}
	}

	/*
	 * lire le ficher de référence selon la path de fichier
	 */
	public static void saveRF(List<String> rfName) throws IOException {
		if (!rfName.isEmpty()) {
			rfFile = new File[rfName.size()];
			rfContext = new ArrayList[rfName.size()];
			for (int i = 0; i < rfName.size(); i++) {
				rfContext[i] = new ArrayList<String>();
				rfFile[i] = new File(rfName.get(i));
				if (!rfFile[i].exists() || !rfFile[i].isFile()) {
					javax.swing.JOptionPane.showMessageDialog(null,
							"sorry,The path is fault !");
				}
				String temp = null;
				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						new FileInputStream(rfFile[i])));
				while ((temp = br1.readLine()) != null) {
					rfContext[i].add(temp);
				}
				br1.close();
			}
		}
	}

	/*
	 * lire le ficher de mt selon la path de fichier
	 */
	public static void saveFocus(List<String> focusName) throws IOException {
		if (!focusName.isEmpty()) {
			int jb = 0;
			focusFile = new File[focusName.size()];
			focusContext = new ArrayList[focusName.size()];
			for (int i = 0; i < focusName.size(); i++) {
				focusContext[i] = new ArrayList<String>();
				focusFile[i] = new File(focusName.get(i));
				if (!focusFile[i].exists() || !focusFile[i].isFile()) {
					javax.swing.JOptionPane.showMessageDialog(null,
							"sorry,The path is faulT !");
				}
				String temp = null;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(focusFile[i])));
				while ((temp = br.readLine()) != null) {
					jb++;
					focusContext[i].add(temp);
				}
				br.close();
			}
		}
	}

	/*
	 * calculer le Blue
	 */
	public static void calculeBlue(int n, ArrayList[] mtCon, ArrayList[] rfCon) {
		len = new double[n];
		correct = new double[n];
		String[] rfline = new String[rfCon.length];
		for (int i = 0; i < mtCon.length; i++) {
			for (int j = 0; j < mtCon[i].size(); j++) {
				for (int m = 0; m < rfCon.length; m++) {
					if (flag) {
						rfline[m] = rfCon[m].get(j).toString().toLowerCase();
					} else {
						rfline[m] = rfCon[m].get(j).toString();
					}
				}
				if (flagFocus) {
					calculeFocusUnePhase(n, mtCon[i].get(j).toString(),
							focusContext[i].get(j).toString(), rfline);
				} else {
					calculeUnePhase(n, mtCon[i].get(j).toString(), rfline);
				}
			}
		}
		System.out.println("Total_length_MT :" + lens + ", Total_length_RF "
				+ rfFile.length + " :  " + lenr);
		for (int i = 0; i < n; i++) {
			System.out.println(i + 1 + "-gram : " + correct[i] + " :" + len[i]);
		}
		if (lens > lenr) {
			bp = 1;
		} else {
			bp = Math.exp(1 - ((double) lenr / (lens)));
		}
		double total = 0;
		for (int i = 0; i < len.length; i++) {
			total += Math.log((double) correct[i] / len[i]);
		}

		blue = bp * Math.exp((double) total / n);
		if (blue <= 0.0) {
			System.out
					.println("WARNING : BLUE is zero, because no correct X-gram was found. Try -n with a lower valuer.");
		}
		System.out.println("BP :" + bp + ", Ratio :" + (double) (lens) / lenr
				+ ", Blue :" + blue);

	}

	/*
	 * calculer chaque phase de Mt pour mériter un liste de phases de références
	 */
	public static void calculeUnePhase(int n, String mtline, String[] rfline) {
		String lcs = "";
		String MtNg = "";
		String rf = "";
		Map[] Tngram = new HashMap[n];// sauvegarder chaque mots comme keys avec
		// le nombre ce mots répétition dans la
		// phase
		ArrayList[] mtNgram = new ArrayList[n];
		Map[] Rngram = new HashMap[n];
		Map RTotalNgram = new HashMap<String, Integer>();// sauvegarder tout les
		// mots déférente
		// avec values selon
		// la liste de
		// références
		ArrayList[] rfNgram = new ArrayList[n];

		if (flag) {
			lcs = mtline.toLowerCase();
		} else {
			lcs = mtline;
		}
		String[] mtli = lcs.split(" ");
		lens += mtli.length;
		for (int i = 0; i < n; i++) {
			len[i] += mtli.length - i;
		}
		if (rfline.length == 1) {
			String[] s = rfline[0].split(" ");
			lenr += s.length;
		}
		if (rfline.length > 1) {
			int tem = 0;
			for (int i = 0; i < rfline.length - 1; i++) {
				String[] s = rfline[i].split(" ");
				String[] s1 = rfline[i + 1].split(" ");
				if (s.length > s1.length) {
					tem = s.length;
				} else {
					tem = s1.length;
				}
			}
			lenr += tem;
		}
		// traiter le phase de Mt
		for (int i = 0; i < n; i++) {
			Tngram[i] = new HashMap<String, Integer>();
			mtNgram[i] = new ArrayList<String>();
			for (int j = 0; j < mtli.length - i; j++) {
				for (int m = 0; m <= i; m++) {
					MtNg += mtli[j + m];
				}
				mtNgram[i].add(MtNg);
				MtNg = "";
			}
			for (Object str : mtNgram[i]) {
				String st = str.toString();
				Object a = Tngram[i].get(st);
				if (a == null) {
					Tngram[i].put(st, 1);
				} else {
					Tngram[i].put(st, Integer.parseInt(String.valueOf(Tngram[i]
							.get(st))) + 1);
				}
			}
		}
		// traiter la liste de références
		for (int c = 0; c < rfline.length; c++) {
			String[] rfli = rfline[c].split(" ");
			for (int i = 0; i < n; i++) {
				Rngram[i] = new HashMap<String, Integer>();
				rfNgram[i] = new ArrayList<String>();
				for (int d = 0; d < rfli.length - i; d++) {
					for (int e = 0; e <= i; e++) {
						rf += rfli[d + e];
					}
					rfNgram[i].add(rf);
					rf = "";
				}
				for (Object str : rfNgram[i]) {
					String st = str.toString();
					Object a = Rngram[i].get(st);
					if (a == null) {
						Rngram[i].put(st, 1);
					} else {
						Rngram[i].put(st, Integer.parseInt(String
								.valueOf(Rngram[i].get(st))) + 1);
					}
				}
				if (c == 0) {
					Set Tkeys1 = Rngram[i].keySet();
					if (Tkeys1 != null) {
						Iterator Riterator = Tkeys1.iterator();
						while (Riterator.hasNext()) {
							String key = Riterator.next().toString();
							RTotalNgram.put(key, Rngram[i].get(key));
						}
					}
				}
			}
			if (c > 0) {
				for (int i = 0; i < n; i++) {
					Set Tkeys = Rngram[i].keySet();
					if (Tkeys != null) {
						Iterator Riterator = Tkeys.iterator();
						while (Riterator.hasNext()) {
							String key = Riterator.next().toString();
							if (RTotalNgram.containsKey(key)) {
								int RTvalue = Integer.parseInt(RTotalNgram.get(
										key).toString());
								int Rvalue = Integer.parseInt(Rngram[i]
										.get(key).toString());
								if (Rvalue > RTvalue) {
									RTotalNgram.put(key, Rvalue);
								}
							} else {
								RTotalNgram.put(key, Rngram[i].get(key));
							}

						}
					}
				}
			}
		}
		// calculer le correct[]
		for (int i = 0; i < n; i++) {
			Set Tkeys = Tngram[i].keySet();
			if (Tkeys != null) {
				Iterator Titerator = Tkeys.iterator();
				while (Titerator.hasNext()) {
					String key = Titerator.next().toString();
					if (RTotalNgram.containsKey(key)) {
						int RTvalue = Integer.parseInt(RTotalNgram.get(key)
								.toString());
						int Tvalue = Integer.parseInt(Tngram[i].get(key)
								.toString());
						if (Tvalue < RTvalue) {
							correct[i] += Tvalue;
						} else {
							correct[i] += RTvalue;
						}
					}

				}
			}
		}
	}

	/*
	 * calculer chaque phase de Mt pour mériter un liste de phases de références
	 */
	public static void calculeFocusUnePhase(int n, String mtline,
			String focusline, String[] rfline) {
		line++;
		String lcs = "";
		String MtNg = "";
		String rf = "";
		Map[] Tngram = new HashMap[n];// sauvegarder chaque mots comme keys avec
		// le nombre ce mots répétition dans la
		// phase
		ArrayList[] mtNgram = new ArrayList[n];
		Map[] Rngram = new HashMap[n];
		Map RTotalNgram = new HashMap<String, Integer>();// sauvegarder tout les
		// mots déférente
		// avec values selon
		// la liste de
		// références
		List[] rfNgram = new ArrayList[n];

		if (flag) {
			lcs = mtline.toLowerCase();
		} else {
			lcs = mtline;
		}
		String[] mtli = lcs.split(" ");
		lens += mtli.length;
		if (rfline.length == 1) {
			String[] s = rfline[0].split(" ");
			lenr += s.length;
		}
		if (rfline.length > 1) {
			int tem = 0;
			for (int i = 0; i < rfline.length - 1; i++) {
				String[] s = rfline[i].split(" ");
				String[] s1 = rfline[i + 1].split(" ");
				if (s.length > s1.length) {
					tem = s.length;
				} else {
					tem = s1.length;
				}
			}
			lenr += tem;
		}

		// ajouter focus
		String[] posFocus = focusline.trim().split(" ");
		double[] focus = new double[mtli.length];
		for (int i = 0; i < posFocus.length; i++) {
			int f = Integer.parseInt(posFocus[i].toString()) - 1;
			if (f > mtli.length) {
				System.out.println("WARNING : Sentence " + line
						+ "'s focus, position " + (i + 1)
						+ "is out of sentence border. Ignoring it");
				continue;
			}
			for (int j = 0; j < focus.length; j++) {
				if (i == 0) {
					double m = 1 - d * Math.abs(f - j);
					if (m < 0) {
						focus[j] = 0.0;
					} else {
						BigDecimal bg = new BigDecimal(m);
						focus[j] = bg.setScale(2, BigDecimal.ROUND_HALF_UP)
								.doubleValue();
					}
				} else {
					double m = 1 - d * Math.abs(f - j);
					if (focus[j] < m) {
						BigDecimal bg = new BigDecimal(m);
						focus[j] = bg.setScale(2, BigDecimal.ROUND_HALF_UP)
								.doubleValue();
					}
				}
			}
		}
		// difinie une ngramfocus[][] et ngramMt[][]
		double[][] ngramFocus = new double[n][];
		String[][] ngramMt = new String[n][];
		String ngramstr = "";
		for (int i = 0; i < n; i++) {
			ngramFocus[i] = new double[mtli.length - i];
			ngramMt[i] = new String[mtli.length - i];
			for (int j = 0; j < mtli.length - i; j++) {
				for (int m = 0; m <= i; m++) {
					double temp = focus[m + j];
					ngramstr += mtli[m + j];
					if (temp > ngramFocus[i][j]) {
						ngramFocus[i][j] = temp;
					}
				}
				len[i] += ngramFocus[i][j];
				ngramMt[i][j] = ngramstr;
				ngramstr = "";
			}
		}

		// traiter le phase de Mt
		for (int i = 0; i < n; i++) {
			Tngram[i] = new HashMap<String, Integer>();
			mtNgram[i] = new ArrayList<String>();
			for (int j = 0; j < mtli.length - i; j++) {
				for (int m = 0; m <= i; m++) {
					MtNg += mtli[j + m];
				}
				mtNgram[i].add(MtNg);
				MtNg = "";
			}
			for (Object str : mtNgram[i]) {
				String st = str.toString();
				Object a = Tngram[i].get(st);
				if (a == null) {
					Tngram[i].put(st, 1);
				} else {
					Tngram[i].put(st, Integer.parseInt(String.valueOf(Tngram[i]
							.get(st))) + 1);
				}
			}
		}

		// traiter la liste de références
		for (int c = 0; c < rfline.length; c++) {
			String[] rfli = rfline[c].split(" ");
			for (int i = 0; i < n; i++) {
				Rngram[i] = new HashMap<String, Integer>();
				rfNgram[i] = new ArrayList<String>();
				for (int d = 0; d < rfli.length - i; d++) {
					for (int e = 0; e <= i; e++) {
						rf += rfli[d + e];
					}
					rfNgram[i].add(rf);
					rf = "";
				}
				for (Object str : rfNgram[i]) {
					String st = str.toString();
					Object a = Rngram[i].get(st);
					if (a == null) {
						Rngram[i].put(st, 1);
					} else {
						Rngram[i].put(st, Integer.parseInt(String
								.valueOf(Rngram[i].get(st))) + 1);
					}
				}
				if (c == 0) {
					Set Tkeys1 = Rngram[i].keySet();
					if (Tkeys1 != null) {
						Iterator Riterator = Tkeys1.iterator();
						while (Riterator.hasNext()) {
							String key = Riterator.next().toString();
							RTotalNgram.put(key, Rngram[i].get(key));
						}
					}
				}
			}
			if (c > 0) {
				for (int i = 0; i < n; i++) {
					Set Tkeys = Rngram[i].keySet();
					if (Tkeys != null) {
						Iterator Riterator = Tkeys.iterator();
						while (Riterator.hasNext()) {
							String key = Riterator.next().toString();
							if (RTotalNgram.containsKey(key)) {
								int RTvalue = Integer.parseInt(RTotalNgram.get(
										key).toString());
								int Rvalue = Integer.parseInt(Rngram[i]
										.get(key).toString());
								if (Rvalue > RTvalue) {
									RTotalNgram.put(key, Rvalue);
								}
							} else {
								RTotalNgram.put(key, Rngram[i].get(key));
							}

						}
					}
				}
			}
		}
		// calculer le correct[]
		int jb = 0;
		for (int i = 0; i < n; i++) {
			Set Tkeys = Tngram[i].keySet();
			if (Tkeys != null) {
				Iterator Titerator = Tkeys.iterator();
				while (Titerator.hasNext()) {
					String key = Titerator.next().toString();
					if (RTotalNgram.containsKey(key)) {
						int RTvalue = Integer.parseInt(RTotalNgram.get(key)
								.toString());
						int Tvalue = Integer.parseInt(Tngram[i].get(key)
								.toString());
						if (Tvalue <= RTvalue) {
							for (int j = 0; j < ngramFocus[i].length; j++) {
								if (key.equals(ngramMt[i][j])) {
									correct[i] += ngramFocus[i][j];
								}
							}
						} else {
							int temc = 0;
							double[] tempcorrect = new double[Tvalue];
							for (int j = 0; j < ngramFocus[i].length; j++) {
								if (key.equals(ngramMt[i][j])) {
									if (temc == 0) {
										tempcorrect[temc] = ngramFocus[i][j];
									} else {
										for (int q = 0; q < temc; q++) {
											double m = ngramFocus[i][j];
											if (tempcorrect[q] < m) {
												double temp = tempcorrect[q];
												tempcorrect[q] = m;
												tempcorrect[q + 1] = temp;
											} else {
												tempcorrect[q + 1] = m;
											}
										}
									}
									temc++;
								}
							}
							for (int j = 0; j < RTvalue; j++) {
								correct[i] += tempcorrect[j];
							}
						}
					}

				}
			}
		}
	}

	public Blue2() {
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
		jb4 = new JButton("Clear");
		jb4.addActionListener(this);
		jb4.setActionCommand("clear");

		jb5 = new JButton("Upload");
		jb5.addActionListener(this);
		jb5.setActionCommand("focus");

		jt1 = new JTextField(20);
		jt2 = new JTextField(20);
		jt3 = new JTextField(10);
		jt5 = new JTextField(20);

		this.setLayout(new GridLayout(7, 1));
		// this.setLayout(new FlowLayout(FlowLayout.LEFT));
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

		jp4.setLayout(new GridLayout(4, 1));
		jp4.add(jl6);
		jp4.add(jl3);
		jp4.add(jl5);
		jp4.add(jb4);

		jp7.add(jl7);
		jp7.add(jt5);
		jp7.add(jb5);
		jp7.setLayout(new FlowLayout(FlowLayout.LEFT));

		jp8.add(jl8);
		jp8.add(jt6);
		jp8.add(jcb1);
		jp8.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.add(jp1);
		this.add(jp7);
		this.add(jp8);
		this.add(jp2);
		this.add(jp3);
		this.add(jp5);
		this.add(jp4);

		this.setTitle("Blue");
		this.setSize(500, 650);
		this.setResizable(false);
		this.setLocation(300, 100);
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
		} catch (Exception ex) {
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
			n = Integer.parseInt(jtf.getText());
			flag = jcb.isSelected();
			flagFocus = jcb1.isSelected();
			try {
				if (flagFocus) {
					saveFocus(focusname);
					d = Double.parseDouble(jt6.getText());
				}
				saveMT(mtname);
				saveRF(rfname);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			calculeBlue(n, mtContext, rfContext);

			Object[][] tableData = new Object[n][3];
			for (int i = 0; i < n; i++) {
				tableData[i][0] = i + 1 + "gram";
				tableData[i][1] = correct[i];
				tableData[i][2] = len[i];
			}
			Object[] columnTitle = { "N-gram", "Count_Cor", "Count_words" };
			model.setDataVector(tableData, columnTitle);
			jtb = new JTable(model);
			jsp = new JScrollPane(jtb);
			jp5.add(jsp);
			jl6
					.setText("MT_length : " + lens + "    " + " RF_length : "
							+ lenr);
			jl3.setText("bp : " + bp);
			jl5.setText("Blue : " + blue);

		}
		if (e.getActionCommand().equals("clear")) {
			mtname = new ArrayList<String>();
			rfname = new ArrayList<String>();
			mtFile = null;
			rfFile = null;
			mtContext = null;
			rfContext = null;
			correct = null;
			len = null;
			lens = 0;
			lenr = 0;
			bp = 0;
			blue = 0;
			jt1.setText("");
			jt2.setText("");
			jt5.setText("");
			jl3.setText("");
			jl5.setText("");
			jl6.setText("");
			jp6.removeAll();
			model.setRowCount(0);
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
