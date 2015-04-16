import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


public class Blue {
	

	/**
	 * @param args
	 */
	static int count1 = 0;
	static int count2 = 0;
	static int count3 = 0;
	static int count4 = 0;
	static double blue1;
	static double blue2;
	static double blue3;
	static double blue4;
	static double bp;	
	// 1 au 4 gram
	public static double blue(String s,String r){
		String[] s1 = s.split(" ");
		String[] r1 = r.split(" ");
		int cl = s1.length;
		int rl = r1.length;
		if(cl>rl){
			bp = 1;
		}else{
			bp = Math.exp(1-rl/cl);
		}
		
		// 1-gram
		for(int i=0;i<s1.length;i++){
			for(int j=0;j<r1.length;j++){
				if(s1[i].equalsIgnoreCase(r1[j])){
					count1 = count1 + 1;
				}
			}
		}
		blue1 = (double)count1 / s1.length;
		
		//2-gram
		for(int i=0;i<s1.length-1;i++){
			String ss = s1[i]+s1[i+1];	
			for(int j=0;j<r1.length-1;j++){
				String rr = r1[j]+r1[j+1];
				if(ss.equalsIgnoreCase(rr)){
					count2 = count2 + 1;
				}
			}	
		}
		blue2 = (double) count2/(s1.length-1);
		
		//3-gram
		for(int i=0;i<s1.length-2;i++){
			String ss = s1[i]+s1[i+1]+s1[i+2];	
			for(int j=0;j<r1.length-2;j++){
				String rr = r1[j]+r1[j+1]+r1[j+2];
				if(ss.equalsIgnoreCase(rr)){
					count3 = count3 + 1;
				}
			}	
		}
		blue3 = (double) count3/(s1.length-2);
		
		//4-gram
		for(int i=0;i<s1.length-3;i++){
			String ss = s1[i]+s1[i+1]+s1[i+2]+s1[i+3];	
			for(int j=0;j<r1.length-3;j++){
				String rr = r1[j]+r1[j+1]+r1[j+2]+r1[j+3];
				if(ss.equalsIgnoreCase(rr)){
					count4 = count4 + 1;
				}
			}	
		}
		blue4 = (double) count4/(s1.length-3);
		return bp * Math.exp((blue1 + blue2 + blue3 + blue4)/4); 		
	}
//	public static void main(String[] args) {
//		stem = "il y a un fruit sur la table";
//		reference = "Il y une pomme sur la table";
//		blue = blue(stem,reference);	
//		System.out.println(blue);
//	}

}
