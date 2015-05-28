import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;


public class SongDatabase {
	public ArrayList<Song> retrieveSongs(ArrayList <File> files){
		ArrayList <Song> songs = new ArrayList <Song>();
		for(File f: files){
			AudioFile af;
			try {
				af = AudioFileIO.read(f);
				Tag tag = af.getTag();
				Song s = new Song(tag.getFirst(FieldKey.TITLE), tag.getFirst(FieldKey.ARTIST), tag.getFirst(FieldKey.ALBUM), f);
				songs.add(s);
			} catch (CannotReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return songs;
	}
	
	public void alphabetize(ArrayList<Song> songs, boolean b){
		int maxIndex;
		if(b == true){
			for(int i = songs.size()-1; i > -1; i--){
				maxIndex = i;
				for(int j = 0; j < maxIndex; j++){
					String k = songs.get(j).getTitle();
					String l = songs.get(j+1).getTitle();
					Song temp = songs.get(j);
					if(k.compareTo(l) > 0){
						songs.set(j, songs.get(j+1));
						songs.set(j+1, temp);
					}
				}
			}
		} else{
			for(int i = songs.size()-1; i > -1; i--){
				maxIndex = i;
				for(int j = 0; j < maxIndex; j++){
					char k = songs.get(j).getArtist().charAt(0);
					char l = songs.get(j+1).getArtist().charAt(0);
					Song temp = songs.get(j);
					if(k > l){
						songs.set(j, songs.get(j+1));
						songs.set(j+1, temp);
					}
				}
			}
		}
	}
	
	
	public void traverseMp3(String directory, ArrayList <File> files){
		File dir = new File(directory);
		File[] liFile = dir.listFiles();
			for(File f: liFile){
				if(f.isFile()){
					String fname = f.getName();
					if (fname.endsWith(".mp3")){
						files.add(f);
					}	
				}
				else if(f.isDirectory()){
					try {
						traverseMp3(f.getCanonicalPath(), files);
					} catch (IOException e) {
						System.out.println("IOException Error");
					}
				}
			}
	}
	
	public String getArtist(File file){
		try {
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			String artist = tag.getFirst(FieldKey.ARTIST);
			
			return artist;
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	public String[][] createTableElems(ArrayList <Song> songs){
		ArrayList <Song> shown = new ArrayList <Song>();
		for(Song s: songs){
			if(s.getSearch()==true){
				shown.add(s);
			}
		}
		
		String[][] tableElem = new String[shown.size()][2];
		for(int i = 0; i < shown.size(); i++){
			tableElem[i][0] = shown.get(i).getTitle();
			tableElem[i][1] = shown.get(i).getArtist();
		}
		
		return tableElem;
	}
	
	public void search(ArrayList <Song> s, String title){
		for(Song sn: new ArrayList<Song>(s)){
			String ti = title.toLowerCase();
			String t = sn.getTitle().toLowerCase();
			if(!t.startsWith(ti)){
				sn.setSearch(false);
			}
		}
	}
	
	public void showAll(ArrayList <Song> s){
		for(Song sng: s){
			sng.setSearch(true);
		}
	}
	
	public String getAlbum(File file){
		try {
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			String album = tag.getFirst(FieldKey.ALBUM);
			
			return album;
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
}
