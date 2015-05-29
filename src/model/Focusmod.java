package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Focusmod {
public static double d;
public static List<String> focusname;
public static File[] focusFile;// créer des files selon de nombre de focus
public static ArrayList[] focusContext;// sauvegarder des contexte de chaque
public  boolean flagFocus = false;
public  boolean isFlagFocus() {
	return flagFocus;
}

public void setFlagFocus(boolean flagFocus) {
	this.flagFocus = flagFocus;
}

public static double getD() {
	return d;
}
public static void setD(double d) {
	Focusmod.d = d;
}
public static List<String> getFocusname() {
	return focusname;
}
public static void setFocusname(List<String> focusname) {
	Focusmod.focusname = focusname;
}
public static File[] getFocusFile() {
	return focusFile;
}
public static void setFocusFile(File[] focusFile) {
	Focusmod.focusFile = focusFile;
}
public static ArrayList[] getFocusContext() {
	return focusContext;
}
public static void setFocusContext(ArrayList[] focusContext) {
	Focusmod.focusContext = focusContext;
}


}
