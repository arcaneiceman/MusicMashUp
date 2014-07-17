package com.MashUp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.AsyncTask;


public class FastPlayStream implements PlayStream{
	File SongFile;
	String PathtoSong;
	String SongName;
	FileInputStream in;
	boolean CenterFilter;
	boolean playing;
	boolean ispause;
	boolean isRun;
	AsyncTask<String, String, String> pThread;


	//time stuff:
	boolean trip=false;


	protected MediaExtractor extractor;
	protected MediaCodec decoder;
	protected AudioTrack audioTrack;
	protected int inputBufferIndex;
	protected int outputBufferIndex;
	MediaCodec.BufferInfo bufferInfo;
	MediaFormat format;
	String mime;

	ByteBuffer[] InputBuffers;
	ByteBuffer[] OutputBuffers;

	public FastPlayStream(){
		//Dummby Constructor
		playing=false;
		CenterFilter=false;
		SongName="unintialized";
	}


	public FastPlayStream(String FullPath, String Sname){
		SongName=Sname;
		PathtoSong=FullPath;
		// create our AudioTrack instance
		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, 
				44100, 
				AudioFormat.CHANNEL_OUT_STEREO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				AudioTrack.getMinBufferSize (44100,AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT), 
				AudioTrack.MODE_STREAM
				);
		CenterFilter=false;
		playing=false;
		ispause=false;
		isRun=false;
		extractor = new MediaExtractor();
		try {
			extractor.setDataSource(PathtoSong);
		} catch (Exception e) {
			return;
		}
		format = extractor.getTrackFormat(0);
		mime = format.getString(MediaFormat.KEY_MIME);

		// the actual decoder
		decoder = MediaCodec.createDecoderByType(mime);
		decoder.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
		int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
		assert(sampleRate==44100);

		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, 
				sampleRate, 
				AudioFormat.CHANNEL_OUT_STEREO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				AudioTrack.getMinBufferSize (sampleRate,AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT), 
				AudioTrack.MODE_STREAM
				);
		extractor.selectTrack(0);
		format = extractor.getTrackFormat(0);
		mime = format.getString(MediaFormat.KEY_MIME);

		System.out.println("PlayStream Init Success!");
	}

	private void decodeandwrite(){
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		decoder = MediaCodec.createDecoderByType(mime);
		decoder.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
		int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
		assert(sampleRate==44100);
		decoder.start();
		audioTrack.play();
		InputBuffers = decoder.getInputBuffers();
		OutputBuffers = decoder.getOutputBuffers();
		ByteBuffer inputBuffer;
		ByteBuffer outputBuffer;
		byte[] outputdata;
		try{
			while(playing){
				if(ispause){
					Thread.sleep(10);
					continue;
				}
				inputBufferIndex=decoder.dequeueInputBuffer(-1);
				if(inputBufferIndex>=0){
					inputBuffer = InputBuffers[inputBufferIndex];
					inputBuffer.clear();
					int sampleSize =extractor.readSampleData(inputBuffer, 0 /* offset */);
					//place some code here because -1 sample size means we stop
					if(sampleSize==-1){
						//end of stream
						System.out.println("End of Stream");
						MainActivity.StaticStopCall();
						if(AudioSystem.recording){
							MainActivity.StaticStopRecordingCall();
						}
						break;
					}

					long presentationTimeUs = extractor.getSampleTime();
					extractor.advance();
					decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);

				}
				bufferInfo = new MediaCodec.BufferInfo();
				outputBufferIndex= decoder.dequeueOutputBuffer(bufferInfo, 50);//check this maybe we should increase time out

				while(outputBufferIndex >=0){//make sure... this could be an if only
					outputBuffer = OutputBuffers[outputBufferIndex];

					outputBuffer.position(bufferInfo.offset);
					outputBuffer.limit(bufferInfo.offset+ bufferInfo.size);

					outputdata= new byte[bufferInfo.size];
					outputBuffer.get(outputdata);

					if(CenterFilter){
						outputdata=Tools.CenterChannelFilter(outputdata);
					}
					audioTrack.write(outputdata, 0, outputdata.length);
					if(trip){
						System.out.println("StartTime measured");
						Tools.starttime=System.currentTimeMillis();
						trip=false;
					}
					decoder.releaseOutputBuffer(outputBufferIndex, false);
					outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
				}
			}	
			Stop();
		}
		catch(Exception e){
			System.out.println("EVERYTHINGS AN EXCEPTION THESE DAYS");
			try{
				MainActivity.StaticStopCall();
				if(AudioSystem.recording){
					MainActivity.StaticStopRecordingCall();
				}
			}
			catch(Exception ee){
				System.err.print("Shit anoyher exception");
			}
		}
	}
	public void Play(){
		ispause=false;
		playing=true;
		audioTrack.play();
		if(!isRun){
			pThread= new AsyncTask<String , String , String>(){
				@Override
				protected String doInBackground(String... arg0) {
					isRun=true;
					decodeandwrite();
					isRun=false;
					return null;
				}
			};
			pThread.execute(SongName);
		}

	}

	public void Pause(){
		audioTrack.pause();
		audioTrack.flush();
		ispause=true;
	}

	public void Stop(){
		try {
			audioTrack.flush();
			audioTrack.pause();
			audioTrack.flush();
			playing=false;
			extractor = new MediaExtractor();
			extractor.setDataSource(PathtoSong);
			extractor.selectTrack(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Reset(){//and destroy
		if(decoder != null)
		{
			decoder.stop();
			decoder.release();
			decoder = null;
		}
		if(audioTrack != null)
		{
			audioTrack.flush();
			audioTrack.release();
			audioTrack = null;	
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

	public boolean getispause() {
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
