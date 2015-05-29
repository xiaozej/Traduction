package model;

public class Bleumod {
	double[] correct;// sauvegarder des nombre des mots que Mt mérite référence
	double[] len; // nombre de mots dans chaque N-gram
	int lens, lenr;// nombre de mots Mt et référence
	double bp;
	double blue;
	public double[] getCorrect() {
		return correct;
	}
	public void setCorrect(double[] correct) {
		this.correct = correct;
	}
	public double[] getLen() {
		return len;
	}
	public void setLen(double[] len) {
		this.len = len;
	}
	public int getLens() {
		return lens;
	}
	public void setLens(int lens) {
		this.lens = lens;
	}
	public int getLenr() {
		return lenr;
	}
	public void setLenr(int lenr) {
		this.lenr = lenr;
	}
	public double getBp() {
		return bp;
	}
	public void setBp(double bp) {
		this.bp = bp;
	}
	public double getBlue() {
		return blue;
	}
	public void setBlue(double blue) {
		this.blue = blue;
	}
	
}
