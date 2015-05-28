import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MPlayerPanel extends JPanel {

	// three subpanels
	JPanel topPanel, bottomPanel; 
	JScrollPane centerPanel;
	SongDatabase sd = new SongDatabase();
	ArrayList <Song> songs = new ArrayList <Song>();
	ArrayList <Song> currentCollection = new ArrayList <Song>();
	ArrayList <Song> searchTempCollection = new ArrayList <Song>();
	PlayerThread currThread;

	// boxes, textfield, check box
	JButton playButton, stopButton, exitButton, loadMp3Button, saveButton, openButton;

	// the checkbox that specifies whether the songs should be sorted by Artist (or by Title)
	JCheckBox sortBox = new JCheckBox("Sort by Artist");

	// the text field used to search for a song
	JTextField searchBox;

	int selectedSong = -1; // the index of the row that corresponds to the selected song
	private JTable table = null;
	private final JFileChooser fc = new JFileChooser(); // for opening a window to select a file

	MPlayerPanel() {

		this.setLayout(new BorderLayout());
		// Create panels: top, center, bottom
		// Create the top panel that has buttons: Load mp3, Save Library, Load Library 
		// It also has a textfield to search for a song and "sort by artist" checkbox
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,4));

		// create buttons
		loadMp3Button = new JButton("Load mp3");
		saveButton = new JButton("Save Library");
		openButton = new JButton("Load Library");
		searchBox = new JTextField(5);
		exitButton = new JButton("Exit");
		playButton = new JButton("Play");
		stopButton = new JButton("Stop");

		// add a listener for each button
		loadMp3Button.addActionListener(new MyButtonListener());
		saveButton.addActionListener(new MyButtonListener());
		openButton.addActionListener(new MyButtonListener());
		sortBox.addActionListener(new MyButtonListener());
		exitButton.addActionListener(new MyButtonListener());
		playButton.addActionListener(new MyButtonListener());
		stopButton.addActionListener(new MyButtonListener());
		searchBox.addActionListener(new MyButtonListener());


		// add buttons, textfield and a checkbox to the top panel
		topPanel.add(loadMp3Button);
		topPanel.add(saveButton);
		topPanel.add(openButton);
		topPanel.add(searchBox);
		topPanel.add(sortBox);

		this.add(topPanel, BorderLayout.NORTH);

		// create the bottom panel and add three buttons to it
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1,3));
		bottomPanel.add(playButton);
		bottomPanel.add(stopButton);
		bottomPanel.add(exitButton);

		this.add(bottomPanel, BorderLayout.SOUTH);

		// the panel in the center that shows mp3 songs
		centerPanel = new JScrollPane();
		this.add(centerPanel, BorderLayout.CENTER );

		// file chooser: set the default directory to the current directory 
		fc.setCurrentDirectory(new File("."));

	}


	/** A inner listener class for buttons, textfield and checkbox **/
	class MyButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == loadMp3Button) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Select a directory with mp3 songs");
				// open a window to select a directory
				int returnVal = fc.showOpenDialog(MPlayerPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File dir = fc.getSelectedFile();
					ArrayList <File> files = new ArrayList <File>();
					
					try {
						String dirpath = dir.getCanonicalPath();
						sd.traverseMp3(dirpath, files);
						songs = sd.retrieveSongs(files);
						
						sd.alphabetize(songs, true);
						currentCollection = songs;
						
					} catch (IOException e1) {
						System.out.println("IOException Error");
					}


					// FILL IN CODE 
					// Traverse this directory and all of its subdirectories recursively 
					// to find mp3 files
					// Then extract the song title and the artist for each mp3 file, 
					// create a Song object, add it to the collections of songs and display
					

					// Example of display a table (everything is hardcoded)
					String[][] tableElems = sd.createTableElems(songs);
					String[] columnNames = {"Title", "Artist"};

					// creating a table and adding it to the centerPanel
					table = new JTable(tableElems, columnNames );
					centerPanel.getViewport().add (table);
					updateUI();					

				}
			}
			else if (e.getSource() == saveButton) {
				// save the song catalog into a file
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setDialogTitle("Select a file to save the library file");
				int returnVal = fc.showSaveDialog(MPlayerPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// FILL IN CODE
					// Save the information about songs into a textfile
					PrintWriter printWriter;
					try {
						printWriter = new PrintWriter (file);
						printWriter.println(currentCollection.size());
						for(Song s: currentCollection){
							printWriter.println(s.getTitle());
							printWriter.println(s.getArtist());
							printWriter.println(s.getAlbum());
							printWriter.println(s.getFile().getPath());
							printWriter.println();
						}
						if (printWriter != null){
							printWriter.close ();
						}
					} catch (FileNotFoundException g) {
					System.out.println("Could not find the file.");
					}



				}
			}

			else if (e.getSource() == openButton) {
				// FILL IN CODE
				// open a textfile, parse it, create Songs objects ,etc..
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setDialogTitle("Select a library file to load.");
				int returnVal = fc.showOpenDialog(MPlayerPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					FileReader fileReader;
					ArrayList <String> line = new ArrayList<String>();
					try{
						String l;
						fileReader = new FileReader(file);
						BufferedReader br = new BufferedReader(fileReader);
						while((l = br.readLine()) != null){
							line.add(l);
						}
					} catch (FileNotFoundException g) {
						System.out.println("Could not find the file.");
						} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int num = Integer.parseInt(line.get(0));
					line.remove(0);
					ArrayList <Song> library = new ArrayList <Song> ();
					for(int i = 0; i < num; i++){
						int index = i*5;
						File f = new File(line.get(index+3));
						Song s = new Song(line.get(index), line.get(index+1), line.get(index+2), f);
						library.add(s);
					}
					sd.alphabetize(library, true);
					currentCollection = library;
					
					String[][] tableElems = sd.createTableElems(library);
					String[] columnNames = {"Title", "Artist"};
					
					table = new JTable(tableElems, columnNames );
					centerPanel.getViewport().add (table);
					updateUI();	
					

				}
			}

			else if (e.getSource() == playButton) {

				selectedSong = table.getSelectedRow();
				ArrayList <Song> currentlyShown = new ArrayList<Song>();
				for(Song s: currentCollection){
					if(s.getSearch()==true){
						currentlyShown.add(s);
					}
				}
				// FILL IN CODE
				// Use JLayer library to play a song
				Song s = currentlyShown.get(selectedSong);
				try {
					if(currThread != null){
						currThread.stop();
						String path = s.getFile().getCanonicalPath();
						currThread = new PlayerThread(path);
						currThread.start();
					} else{
						String path = s.getFile().getCanonicalPath();
						currThread = new PlayerThread(path);
						currThread.start();
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}



			}
			else if (e.getSource() == stopButton) {
				// FILL IN CODE
				if(currThread.isAlive()){
					currThread.stop();
				}



			}
			else if (e.getSource() == exitButton) {
				System.exit(0);
			}

			else if (e.getSource() == sortBox) {
				// FILL IN CODE
				if(sortBox.isSelected()){
					sd.alphabetize(songs, false);
				}
				else{
					sd.alphabetize(songs, true);
				}
				
				String[][] tableElems = sd.createTableElems(songs);
				String[] columnNames = {"Title", "Artist"};
				
				table = new JTable(tableElems, columnNames );
				centerPanel.getViewport().add (table);
				updateUI();	


			}
			else if (e.getSource() == searchBox) {
				String[][] tableElems;
				String[] columnNames = {"Title", "Artist"};;
				String searchT = searchBox.getText();
				if(searchT.equals("")){
					sd.showAll(currentCollection);
					tableElems = sd.createTableElems(currentCollection);
				}else{
					sd.search(currentCollection, searchT);
					tableElems = sd.createTableElems(currentCollection);
				}
				
				
				table = new JTable(tableElems, columnNames);
				centerPanel.getViewport().add(table);
				
				
			}
			updateUI();
		} // actionPerformed
	} // ButtonListener

	class PlayerThread extends Thread { 
		Player pl;
		PlayerThread(String filename) {
		 
		try {
		// filename here contains mp3 you want to play file = new FileInputStream(filename);
		FileInputStream file = new FileInputStream(filename);
		pl = new Player(file);
		} catch (JavaLayerException | FileNotFoundException e) {
			e.getMessage();
		}
		}
		public void run() {
			try { 
				pl.play();
			}
			catch(Exception e) {
				e.getMessage(); 
			}
		} 
	}




}
