package com.blockhead7360.ghphtmlig;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class GHPagesHTMLGenerator {

	String version = "1.0";

	String filepath = null;

	JDialog dialog = null;
	JProgressBar progress = null;

	public GHPagesHTMLGenerator() {

		Runnable go = new Runnable() {
			public void run() {
				int op = JOptionPane.showOptionDialog(null, "Welcome to the GitHub Pages HTML Index Guide Generator\nVersion " + version + " by Dilan (blockhead7360.com)", "GHPHTMLIGG by Blockhead7360", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[] {"Create Index Guide", "Close"}, "Create Index Guide");
				if (op == JOptionPane.YES_OPTION) 
					findFolder();
				else System.exit(0);   
			}
		};

		SwingUtilities.invokeLater(go);




	}

	public void findFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select your project directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int val = fc.showDialog(null, "Select");
		if (val == 0) {
			if (!fc.getSelectedFile().isDirectory() || fc.getSelectedFile() == null) {
				errorFindFolder("The file you selected is not a directory");
				return;
			}

			filepath = fc.getSelectedFile().getPath();
			

			new SwingWorker<String, String>(){

				@Override
				protected String doInBackground() throws Exception {
					progress = new JProgressBar();
					loadingFrame();	
					dialog.setTitle("Generating files! Please wait...");
					progress.setMaximum(countFiles(fc.getSelectedFile()));
					generateAndPlace(fc.getSelectedFile());
					dialog.setTitle("Complete!");
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					return null;

				}
				
			}.execute();
			
		}
		else	
			System.exit(0);
	}

	public void errorFindFolder(String error) {
		JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
		findFolder();
	}

	boolean overwriteIndexFiles = false;

	public void generateAndPlace(File f) {

		List<String> files = new ArrayList<String>();
		
		for (File fx : f.listFiles()) {

			if (fx.getName().contains("DS_Store")) continue;
			if (fx.getName().contains("index.html")) continue;

			if (fx.isDirectory()) {
				generateAndPlace(fx);
				files.add("d" + fx.getName());
			}
			else
				files.add("f" + fx.getName());
		}

		String[] slasharray = filepath.split(File.separator);
		String start = slasharray[slasharray.length - 1];

		String currentfilepath = start + f.getPath().substring(filepath.length(), f.getPath().length());

		List<String> list = new ArrayList<String>();

		list.add("<!DOCTYPE html>");
		list.add("<html>");
		list.add("<body>");
		list.add("<h1>" + currentfilepath + "</h1>");

		List<String> temp_dir = new ArrayList<String>();
		List<String> temp_file = new ArrayList<String>();
		for (String d : files) {
			if (d.charAt(0) == 'd') 
				temp_dir.add(d.substring(1));
			else
				temp_file.add(d.substring(1));
		}

		files.clear();
		files.addAll(temp_dir);
		files.addAll(temp_file);

		for (String t : files) {
			list.add("<p><a href=\"" + t + "\">" + "/" + t + "</a><p>");
		}
		list.add("<p style=\"font-size:8px\">Powered by <a href=\"http://blockhead7360.com/ghpindex\">GitHub Pages HTML Index Generator</a> by Blockhead7360</p>");
		list.add("</body>");
		list.add("</html>");
		try {
			File i = new File(f, "index.html");
			if (i.exists()) i.delete();
			i.createNewFile();
			Files.write(i.toPath(), list, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			errorFindFolder("Unable to write to a file");
			return;
		}
		progress.setValue(progress.getValue() + 1);
	}

	public int countFiles(File f) {
		int number = 1;
		
		if (f.isDirectory()) {
			for (File i : f.listFiles(new FileFilter() {
				public boolean accept(File f) {
					return (f.isDirectory());
				}
				})) {
				number += countFiles(i);
			}
		}

		return number;
	}

	public void loadingFrame() {
		JOptionPane optionPane = new JOptionPane(progress, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);


		dialog = new JDialog();
		dialog.setTitle("Preparing...");
		dialog.setModal(false);
		dialog.addWindowListener(new WindowAdapter() 
		{
		  public void windowClosed(WindowEvent e)
		  {
			  System.exit(0);
		  }
		});
		dialog.setContentPane(optionPane);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();


		dialog.setVisible(true);
	}

	public static void main(String[] args) {
		new GHPagesHTMLGenerator();
	}


}
