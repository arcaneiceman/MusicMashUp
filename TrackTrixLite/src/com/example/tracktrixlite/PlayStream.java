package com.example.tracktrixlite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;


public class PlayStream extends Thread {

	AudioTrack AudioInputBuffer;
	File songfile;
	FileInputStream in;
	RandomAccessFile in2;
	//byte[] byteData = null; 
	boolean CenterFilter;
	boolean playing;
	boolean badboolean;
	byte[] Buffer=new byte[8*1024] ; //8KB buffer

	public PlayStream(){
		//Dummby Constructor
		playing=false;
		CenterFilter=false;
	}
	public PlayStream(String FileName){
		AudioInputBuffer = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, 44100, AudioTrack.MODE_STREAM);
		songfile=new File(FileName);
		CenterFilter=false;
		playing=false;
		System.out.println("PlayStream Init Success!");
	}

	public void run(){
		
		while(badboolean){
			while(playing){
				try {
					int retval=in.read(Buffer);
					if(retval==-1){
						Stop();
					}	
				} catch (Exception E){
					E.printStackTrace();
				}//Read into buffer

				if(CenterFilter){
					//apply center filter
					Buffer=CenterChannelFilter(Buffer);
				}
				AudioInputBuffer.write(Buffer, 0, Buffer.length);	//writing into buffer		
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}
	public void Play(){
		if (playing)
		{
			return;
		}
		try
		{
			if(in!=null){in=null;}
			in=new FileInputStream(songfile);			
			AudioInputBuffer.play();
			playing = true;     
			badboolean=true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Couldnt find file!");
		}
	}

	public void Pause(){
		try
		{
			AudioInputBuffer.pause();
			playing = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void Stop(){
		try {
			AudioInputBuffer.pause();
			AudioInputBuffer.flush();
			in.close();
			playing=false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Reset(){//and destroy
		try {
			in.close();
			AudioInputBuffer.pause();
			AudioInputBuffer.flush();
			AudioInputBuffer.release();
			in=null;
			songfile=null;
			Buffer=null;
			playing=false;
			badboolean=false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] CenterChannelFilter(byte[] incomingBuffer){
		for(int i=0; i<incomingBuffer.length; i=i+4){
			int inversionvariable1= (int)incomingBuffer[i+2] & 0xff;
			inversionvariable1=inversionvariable1*(-1);
			
			int inversionvariable2= (int)incomingBuffer[i+3] & 0xff;
			inversionvariable2=inversionvariable2*(-1);
			
			int temp1= ((int)incomingBuffer[i])+inversionvariable1;
			int temp2= ((int)incomingBuffer[i+1])+inversionvariable2;
			incomingBuffer[i]=(byte) temp1;
			incomingBuffer[i+1]=(byte) temp2;
			incomingBuffer[i+2]=(byte) temp1;	
			incomingBuffer[i+3]=(byte) temp2;
		}
		return incomingBuffer;
	}
}
