package sevelet;
import utile.getoption;
import view.Blue_interface;
import impl.calculerBleuDaoImpl;
import impl.saveSourceDaoImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import model.Bleumod;
import model.Focusmod;

import dao.calculerBleuDao;
import dao.saveSourceDao;

public class Bleu {

	/**
	 * @param args
	 */
	static double d=0.2;
	static int n=4;
	static boolean flag = false;
	static boolean flagFocus = false;
	
	// sauvegarder des paths de fichier Mt
	static List<String> mtname = new ArrayList<String>();
	
	// sauvegarder des paths de fichier référence
	static List<String> rfname = new ArrayList<String>();
	
	// sauvegarder des paths de fichier focusname
	static List<String> focusname = new ArrayList<String>();
	
	static ArrayList[] mtContext = null;// sauvegarder des contexte de chaque Mt
	static ArrayList[] rfContext = null;// sauvegarder des contexte de chaque
	static ArrayList[] focusContext = null;// sauvegarder des contexte de chaque
	static Focusmod focus;
	
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
			Pattern pattern = Pattern.compile("[0-9]*");
			
			if (args.length == 0) {
				Blue_interface bl = new Blue_interface();
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
				saveSourceDao saveSource = new saveSourceDaoImpl();
				if (flagFocus){  
					focus = new Focusmod();
					focus.setD(d);
					focus.setFlagFocus(flagFocus);
					focus.setFocusname(focusname);		
					focusContext = saveSource.saveSource(focusname);
					focus.setFocusContext(focusContext);
					}else{
						focus = new Focusmod();
					}
				
				mtContext = saveSource.saveSource(mtname);
				rfContext = saveSource.saveSource(rfname);
				
				calculerBleuDaoImpl calculerBleu = new calculerBleuDaoImpl();
				Bleumod bleu = new Bleumod();
				bleu = calculerBleu.calculeBleu(n, mtContext, rfContext,focus,flag);
				System.out.println("Total_length_MT :" + bleu.getLens() + ", Total_length_RF "+" :  " + bleu.getLenr());
				for (int i = 0; i < n; i++) {
					System.out.println(i + 1 + "-gram : " + bleu.getCorrect()[i] + " :" + bleu.getLen()[i]);
				}
				System.out.println("BP :" + bleu.getBp() + ", Ratio :" + (double) bleu.getLens() / bleu.getLenr()
						+ ", Bleu :" + bleu.getBlue());	
				if(calculerBleu.getWarninglist().size()>0){
					for(int i=0;i<calculerBleu.getWarninglist().size();i++){
						System.out.println(calculerBleu.getWarninglist().get(i).toString());
					}
				}
			}
		} catch (Exception e) {
			System.out
					.println("Choose option: -l lowercase, -f add focus, -d(0-1) add weight -n(n>=1) ngram -h help.");
			System.out
					.println("Executive order: option (optional), TraductionMachine.txt reference.txt");
			System.exit(0);
		}
	}

}
