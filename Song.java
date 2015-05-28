import java.io.File;


public class Song {
	private String title;
	private String artist;
	private String album;
	private File file;
	private boolean search;
	
	Song(String n, String a, String album, File f){
		this.title = n;
		this.artist = a;
		this.album = album;
		this.file = f;
		this.search = true;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getArtist(){
		return this.artist;
	}
	
	public String getAlbum(){
		return this.album;
	}
	
	public File getFile(){
		return this.file;
	}
	
	public void setSearch(boolean s){
		this.search = s;
	}
	
	public boolean getSearch(){
		return this.search;
	}
}

