import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.util.ArrayList;
import java.util.HashMap;  
import java.util.List;
import java.util.Map;  
import java.util.Scanner;
  
public class TestTranslate {
	static Map<String, String> mapTrn = null;
	
	public TestTranslate(){
		String cpFileName = "";  
        cpFileName = javax.swing.JOptionPane.showInputDialog("La Path de Corpus :"); 
        File cpFile = new File(cpFileName);   
        if(!cpFile.exists() || !cpFile.isFile()){  
            javax.swing.JOptionPane.showMessageDialog(null, "Desole,La path est fault！");  
        }      
        try {  
            String tmpStr = null;  
            String[] strs;  
              
            //Nouveau streamFile
            BufferedReader br = new BufferedReader(  
                    new InputStreamReader(  
                            new FileInputStream(cpFile)));    
            //On utilise Map pour sauvegarder le croups 
            mapTrn = new HashMap<String, String>();           
            while((tmpStr = br.readLine()) != null){     
                if(!tmpStr.equals("")){                   
                    strs = tmpStr.split("=");   
                    mapTrn.put(strs[0].toLowerCase(), strs[1]);   
                }  
            }
            br.close();
         } catch (Exception e) {  
             //e.printStackTrace();  
             javax.swing.JOptionPane.showMessageDialog(null, "Desole,read est fault！");  
         }  
        
	}
    public static void main(String[] args) {  
    	new TestTranslate();
    	String stem = "System Traducton : ";
     	System.out.println("tappez votre source :");
     	Scanner src = new Scanner(System.in);
     	String[] ss = src.nextLine().split(" ");
     	for(int i=0;i<ss.length;i++){
     		stem +=translate(ss[i])+" ";  	
     	} 
     	String reference = "Il y une pomme sur la table";
     	double blue = Blue.blue(stem, reference);
     	System.out.println(stem);
     	System.out.println("Blue : "+ blue);	
    } 
      
    /** 
     * traduction 
     */  
    public static String translate(String s){  
    	 //obtenir la source de croups   
             
            String str = ""; 
           if(mapTrn.get(s.toLowerCase())!= null){
        	   str = mapTrn.get(s.toLowerCase()).toString();
           }else{
        	   str = "desole, on ne trouve pas la traduction";
           }
		return str;      
    }  
}  

