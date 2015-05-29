package impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import dao.saveSourceDao;
public class saveSourceDaoImpl implements saveSourceDao {

	public ArrayList[] saveSource(List<String> Namelist) {
		File[] sourceFile = null;
		ArrayList[] sourceContext = null;
		if (!Namelist.isEmpty()) {
			sourceFile = new File[Namelist.size()];
			sourceContext = new ArrayList[Namelist.size()];
			for (int i = 0; i < Namelist.size(); i++) {
				sourceContext[i] = new ArrayList<String>();
				sourceFile[i] = new File(Namelist.get(i));
				if (!sourceFile[i].exists() || !sourceFile[i].isFile()) {
					javax.swing.JOptionPane.showMessageDialog(null,
							"sorry,The path is faulT !");
				}
				String temp = null;
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile[i])));
					while ((temp = br.readLine()) != null) {
						sourceContext[i].add(temp);
					}
					br.close();
				} catch (Exception e) {
				}
			}
		}
		return sourceContext;
	}

}
