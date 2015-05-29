package impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Focusmod;
import model.Bleumod;
import dao.calculerBleuDao;

public class calculerBleuDaoImpl implements calculerBleuDao {
	
	double[] correct;// sauvegarder des nombre des mots que Mt mérite référence
	double[] len; // nombre de mots dans chaque N-gram
	int lens, lenr;// nombre de mots Mt et référence
	double bp;
	double blue;
	int line = 0;
	Bleumod bleu;
	List<String> warninglist = new ArrayList<String>();
	public Bleumod calculeBleu(int n, ArrayList[] mtCon, ArrayList[] rfCon,Focusmod focus,boolean flag) {
		bleu = new Bleumod();
		len = new double[n];
		correct = new double[n];
		String[] rfline = new String[rfCon.length];
		ArrayList[] focusContext = focus.getFocusContext();
		for (int i = 0; i < mtCon.length; i++) {
			for (int j = 0; j < mtCon[i].size(); j++) {
				for (int m = 0; m < rfCon.length; m++) {
					if (flag) {
						rfline[m] = rfCon[m].get(j).toString().toLowerCase();
					} else {
						rfline[m] = rfCon[m].get(j).toString();
					}
				}
				if (focus.isFlagFocus()) {
					calculeFocusUnePhase(n, mtCon[i].get(j).toString(),focusContext[i].get(j).toString(), rfline,flag,focus.getD());
				} else {
					calculeUnePhase(n, mtCon[i].get(j).toString(), rfline,flag);
				}
			}
		}
//		System.out.println("Total_length_MT :" + lens + ", Total_length_RF "+" :  " + lenr);
//		for (int i = 0; i < n; i++) {
//			System.out.println(i + 1 + "-gram : " + correct[i] + " :" + len[i]);
//		}
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
			String warning = "WARNING : BLUE is zero, because no correct X-gram was found. Try -n with a lower valuer.";
			warninglist.add(warning);
//			System.out.println(warning);
		}
//		System.out.println("BP :" + bp + ", Ratio :" + (double) (lens) / lenr
//				+ ", Bleu :" + blue);
		bleu.setLens(lens);
		bleu.setLenr(lenr);
		bleu.setBp(bp);
		bleu.setLen(len);
		bleu.setCorrect(correct);
		bleu.setBlue(blue);
		return bleu;
	}

	public void calculeFocusUnePhase(int n, String mtline, String focusline,String[] rfline, boolean flag,double d) {
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
				String warning = "WARNING : Sentence " + line+ " 's focus, position " + (i + 1)+" is out of sentence border. Ignoring it";
				warninglist.add(warning);
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
				for (int m = 0; m < rfli.length - i; m++) {
					for (int e = 0; e <= i; e++) {
						rf += rfli[m + e];
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

	public void calculeUnePhase(int n, String mtline, String[] rfline,boolean flag) {
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
	

	public List<String> getWarninglist() {
		return warninglist;
	}

	public void setWarninglist(List<String> warninglist) {
		this.warninglist = warninglist;
	}
}
