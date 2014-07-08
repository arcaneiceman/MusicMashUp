package com.tracktrixlite;

import java.io.File;

import android.os.Environment;

public class AudioSystem {

	public static PlayStream currentsongplayer=null;
	public static RecorderStream micstream=null;
	private static final String AUDIO_RECORDER_FOLDER = "Tractrix-Lite";
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	public static boolean songloaded=false;

	public static void LoadSong(String filename){
		System.out.println("Loading "+ filename);
		if(currentsongplayer!=null){
			currentsongplayer=null;
		}
		currentsongplayer = new PlayStream(filename);
		songloaded=true;
	}

	public static void PlaySong(){
		currentsongplayer.Play();
		//currentsongplayer.start();
	}
	
	public static void PauseSong(){
		currentsongplayer.Pause();
	}
	
	public static void StopSong(){
		currentsongplayer.Stop();
	}
	
	
	public static void SystemReset(){
		currentsongplayer.Reset();
		currentsongplayer=null;
		songloaded=false;
		//other things to reset
	}
	
	public static void StartRecording(){
		if(currentsongplayer==null){
			System.out.println("There is no song to start recording with");
			return;
		}
		//if recording file already exists, then remove it.
		File checkfile=new File(Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER + "/" + currentsongplayer.songname + "-rec" + AUDIO_RECORDER_FILE_EXT_WAV);
		if(checkfile.exists()){
			checkfile.delete();
		}
		
		//micstream=new RecorderStream(currentsongplayer.songname,currentsongplayer.f);
		StopSong();
		PlaySong();
		micstream.startRecording();
	}
	
	public static void StopRecording(){
		micstream.stopRecording();
		StopSong();
		
		System.out.println("Recording Stopped");
		
	}
	
}
