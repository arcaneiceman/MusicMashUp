package com.example.tracktrixlite;

public class AudioPlayer {

	public PlayStream currentsongplayer;
	//microhonestream
	
	AudioPlayer(){
		//intialize everything
		currentsongplayer=new PlayStream();
	}
	public void LoadSong(String filename){
		System.out.println("Loading "+ filename);
		if(currentsongplayer!=null){
			currentsongplayer=null;
		}
		currentsongplayer = new PlayStream(filename);
	}

	public void PlaySong(){
		currentsongplayer.Play();
		currentsongplayer.start();
	}
	
	public void PauseSong(){
		currentsongplayer.Pause();
	}
	
	public void StopSong(){
		currentsongplayer.Stop();
	}
	
	public void SystemReset(){
		currentsongplayer.Reset();
		currentsongplayer=null;
		//other things to reset
	}
}
