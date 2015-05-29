package dao;

import java.util.ArrayList;

import model.Bleumod;
import model.Focusmod;

public interface calculerBleuDao {
	public Bleumod calculeBleu(int n, ArrayList[] mtCon, ArrayList[] rfCon,Focusmod focus,boolean flag);
	public void calculeUnePhase(int n, String mtline, String[] rfline,boolean flag);
	public void calculeFocusUnePhase(int n, String mtline,String focusline, String[] rfline,boolean flag,double d);
}
