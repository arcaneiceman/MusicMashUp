package com.tracktrixlite;

public interface PlayStream {

	void Play();

	void Pause();

	void Stop();

	void Reset();

	boolean getPlaying();
	
	void setPlaying(boolean s);
	
	String getSongName();
	
	void setFilterStatus(boolean a);
	
	boolean getFilterStatus();


//	AudioTrack AudioInputBuffer;
//	File SongFile;
//	String PathtoSong;
//	String SongName;
//	FileInputStream in;
//	RandomAccessFile in2;
//	//byte[] byteData = null; 
//	boolean CenterFilter;
//	boolean playing;
//	boolean badboolean;
//	byte[] Buffer=new byte[4*1024] ; //4KB buffer
//	Thread playingThread;
//
//	public PlayStream(){
//		//Dummby Constructor
//		playing=false;
//		CenterFilter=false;
//		SongName="unintialized";
//	}
//	public PlayStream(String FullPath, String Sname){
//		AudioInputBuffer = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, 44100, AudioTrack.MODE_STREAM);
//		SongFile=new File(FullPath);
//		PathtoSong=FullPath;
//		SongName=Sname;
//		CenterFilter=false;
//		playing=false;
//		if(in!=null){in=null;}
//		try {
//			in=new FileInputStream(SongFile);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		playingThread =new Thread(new Runnable() {
//			@Override
//			public void run() {
//				writeintoBuffer();
//			}
//		},"AudioPlayer Thread");
//		
//		System.out.println("PlayStream Init Success!");
//	}
//
//	
//	private void writeintoBuffer(){
//		while(badboolean){
//			while(playing){
//				try {
//					int retval=in.read(Buffer);
//					if(retval==-1){
//						AudioSystem.StopSong();
//					}	
//				} catch (Exception E){
//					E.printStackTrace();
//				}//Read into buffer
//
//				if(CenterFilter){
//					//apply center filter
//					Buffer=Tools.CenterChannelFilter(Buffer);
//				}
//				AudioInputBuffer.write(Buffer, 0, Buffer.length);	//writing into buffer		
//			}
//			
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//
//				e.printStackTrace();
//			}
//		}
//	}
//	public void Play(){
//		if (playing)
//		{
//			return;
//		}
//		try
//		{			
//			AudioInputBuffer.play();
//			playing = true;     
//			badboolean=true;
//			if(!playingThread.isAlive()){
//				playingThread.start();
//			}
//			
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			System.out.println("Couldnt find file!");
//		}
//	}
//
//	public void Pause(){
//		try
//		{
//			AudioInputBuffer.pause();
//			AudioInputBuffer.flush();
//			
//			playing = false;
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	public void Stop(){
//		try {
//			AudioInputBuffer.flush();
//			AudioInputBuffer.pause();
//			AudioInputBuffer.flush();
//			playing=false;
//			//playingThread=null;
//			in.close();
//			in=null;
//			in=new FileInputStream(SongFile);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void Reset(){//and destroy
//		try {
//			playing=false;
//			badboolean=false;
//			playingThread=null;
//			in.close();
//			AudioInputBuffer.pause();
//			AudioInputBuffer.flush();
//			AudioInputBuffer.release();
//			in=null;
//			SongFile=null;
//			Buffer=null;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	
}
