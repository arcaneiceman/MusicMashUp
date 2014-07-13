package com.tracktrixlite;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.decoder.Bitstream;
import com.decoder.BitstreamException;
import com.decoder.Decoder;
import com.decoder.Header;
import com.decoder.SampleBuffer;

public class FastPlayStream implements PlayStream{
	AudioTrack AudioInputBuffer;
	File SongFile;
	String PathtoSong;
	String SongName;
	Bitstream bitStream;
	RandomAccessFile in2;
	//byte[] byteData = null; 
	boolean CenterFilter;
	boolean playing;
	boolean badboolean;
	//byte[] Buffer=new byte[4*1024] ; //4KB buffer
	Thread playingThread;
	//Decoder decoder;

	public FastPlayStream(){
		//Dummby Constructor
		playing=false;
		CenterFilter=false;
		SongName="unintialized";
	}
	public FastPlayStream(String FullPath, String Sname){
		AudioInputBuffer = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, 44100, AudioTrack.MODE_STREAM);
		SongFile=new File(FullPath);
		PathtoSong=FullPath;
		SongName=Sname;
		CenterFilter=false;
		playing=false;

		if(bitStream!=null){bitStream=null;}
		try {
			InputStream inputStream = new BufferedInputStream(new FileInputStream(FullPath), 8 * 1024);
			bitStream = new Bitstream(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		playingThread =new Thread(new Runnable() {
			@Override
			public void run() {
				writeintoBuffer();
			}
		},"AudioPlayer Thread");
		//playingThread.setPriority(9);
		//decoder= new Decoder();
		System.out.println("PlayStream Init Success!");
	}

	private void writeintoBuffer(){

		Decoder decoder= new Decoder();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		while(badboolean){
			while(playing){
				try {
					os.reset();
					Header frameHeader = bitStream.readFrame();
					if (frameHeader == null) {
						playing=false;
					}

					SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitStream);
					bitStream.closeFrame();
					short[] pcm =output.getBuffer(); 
					for (short s : pcm) {
						os.write(s & 0xff);
						os.write((s >> 8 ) & 0xff);
					}
					byte[] Buffer=os.toByteArray();
					if(CenterFilter){
						//apply center filter
						Buffer=Tools.CenterChannelFilter(Buffer);
					}
					AudioInputBuffer.write(Buffer, 0, Buffer.length);	//writing into buffer	
				} 
				catch (Exception E){
					E.printStackTrace();
				}

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
			AudioInputBuffer.play();
			playing = true;     
			badboolean=true;
			if(!playingThread.isAlive()){
				playingThread.start();
			}

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
			AudioInputBuffer.flush();
			playing = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void Stop(){
		try {
			AudioInputBuffer.flush();
			AudioInputBuffer.pause();
			AudioInputBuffer.flush();
			playing=false;
			//playingThread=null;
			bitStream.close();
			bitStream=null;
			bitStream = new Bitstream(new FileInputStream(PathtoSong));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Reset(){//and destroy
		try {
			playing=false;
			badboolean=false;
			playingThread=null;
			bitStream.close();
			AudioInputBuffer.pause();
			AudioInputBuffer.flush();
			AudioInputBuffer.release();
			bitStream=null;
			SongFile=null;
		} catch (BitstreamException e) {
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
}
