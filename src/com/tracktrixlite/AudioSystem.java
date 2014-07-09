package com.tracktrixlite;

import java.io.File;

import com.converter.Converter;
import com.decoder.JavaLayerException;

import android.os.Environment;

public class AudioSystem {

	public static PlayStream currentsongplayer=null;
	public static RecorderStream micstream=null;
	private static final String AUDIO_RECORDER_FOLDER = "Tractrix-Lite";
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";

	//State Variables
	public static boolean songloaded=false;
	public static boolean recording=false;

	public static boolean LoadSong(String PathtoSong){
		System.out.println("Loading "+ PathtoSong);
		File file=new File(PathtoSong);
		String FileName=file.getName();
		if(FileName.endsWith(".mp3")|| FileName.endsWith(".MP3")){
			//This song DOES end with mp3 so can process it! 

			int pos = FileName.lastIndexOf(".");// since it either ends with .mp3 or .MP3 it HAS to contain a "."
			String SongName= FileName.substring(0, pos);   // got SongName;
			String PathtoOutput=Tools.StoragePath+SongName+"-play"+Tools.AUDIO_FILE_EXT_WAV;

			Converter ConModule= new Converter();
			System.out.println("Starting Conversion");
			try {
				ConModule.convert(PathtoSong,PathtoOutput);
			} catch (JavaLayerException e) {
				System.out.println("Failed to Convert Song to WAV");
				e.printStackTrace();
				return false;
			}
			System.out.println("Done with Conversion");

			if(currentsongplayer!=null){
				currentsongplayer=null;
			}
			currentsongplayer = new PlayStream(PathtoOutput,SongName);
			songloaded=true;

			System.out.println("Successfully Loaded Song");
			return true;
		}
		else{
			//this song does not end with mp3 or has no extension.
			// call a  bad format error
			return false;
		}
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
		micstream=new RecorderStream(currentsongplayer.SongName);
		StopSong();
		PlaySong();
		micstream.startRecording();
		recording=true;
	}

	public static void StopRecording(){
		micstream.stopRecording();
		StopSong();

		System.out.println("Recording Stopped");
		recording=false;
	}

	public static void SongEnd(){
		if(recording){
			//song was ending and there was a recording
			StopRecording();
		}
		else{
			StopSong();
		}

	}
}
