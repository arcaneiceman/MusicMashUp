package com.tracktrixlite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

public class SlowPlayStream implements PlayStream{

	AudioTrack AudioInputBuffer;
	File SongFile;
	String PathtoSong;
	String SongName;
	FileInputStream in;
	//byte[] byteData = null; 
	boolean CenterFilter;
	boolean playing;
	boolean ispause;
	boolean isRun;
	boolean trip=false;
	byte[] Buffer=new byte[4*1024] ; //4KB buffer
	Thread playingThread;
	AsyncTask<String, String, String> pThread;

	public SlowPlayStream(){
		//Dummby Constructor
		playing=false;
		CenterFilter=false;
		SongName="unintialized";
	}
	public SlowPlayStream(String FullPath, String Sname){
		AudioInputBuffer = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, 44100, AudioTrack.MODE_STREAM);
		SongFile=new File(FullPath);
		PathtoSong=FullPath;
		SongName=Sname;
		CenterFilter=false;
		playing=false;
		ispause=false;
		isRun=false;
		if(in!=null){in=null;}
		try {
			in=new FileInputStream(SongFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("PlayStream Init Success!");
	}


	private void writeintoBuffer(){
		while(playing){
			try{
				if(ispause){
					Thread.sleep(10);
					continue;
				}
				int retval=in.read(Buffer);
				if(retval==-1){
					MainActivity.StaticStopCall();
					if(AudioSystem.recording){
						MainActivity.StaticStopRecordingCall();
					}
					//break;
				}	
				if(CenterFilter){
					//apply center filter
					Buffer=Tools.CenterChannelFilter(Buffer);
				}
				AudioInputBuffer.write(Buffer, 0, Buffer.length);	//writing into buffer	
				if(trip){//this means the first buffer of song has been placed
					Tools.starttime=System.currentTimeMillis();
					trip=false;
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.err.println("PlayThread Execption");
			}

		}
	}
	public void Play(){
		playing=true;
		ispause=false;
		AudioInputBuffer.play();
		if(!isRun){
			pThread= new AsyncTask<String , String , String>(){
				@Override
				protected String doInBackground(String... arg0) {
					isRun=true;
					writeintoBuffer();
					isRun=false;
					return null;
				}
			};
			pThread.execute(SongName);
		}
	}

	public void Pause(){
		AudioInputBuffer.pause();
		AudioInputBuffer.flush();
		ispause=true;
	}

	public void Stop(){
		try {
			AudioInputBuffer.flush();
			AudioInputBuffer.pause();
			AudioInputBuffer.flush();
			playing=false;
			in.close();
			in=null;
			in=new FileInputStream(SongFile);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Reset(){//and destroy
		try {
			playing=false;
			ispause=true;
			playingThread=null;
			in.close();
			AudioInputBuffer.pause();
			AudioInputBuffer.flush();
			AudioInputBuffer.release();
			in=null;
			SongFile=null;
			Buffer=null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getPlaying(){
		return playing;
	}

	public void setPlaying(boolean s){
		playing=s;
	}

	public String getSongName(){
		return SongName;
	}

	public boolean getFilterStatus(){
		return CenterFilter;
	}

	public void setFilterStatus(boolean a){
		CenterFilter=a;
	}
	
	public boolean getispause(){
		return ispause;
	}
	

	public void setTrip(boolean a){
		trip=a;
	}
	
	public boolean getTrip(){
		return trip;
	}
	public String getCurrentSongFullPath() {
		// TODO Auto-generated method stub
		return PathtoSong;
	}
}
