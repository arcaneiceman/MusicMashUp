package com.tracktrixlite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import com.tracktrixlite.Tools;
public class RecorderStream{

	//public String FileName=null;
//	private static final int RECORDER_BPP = 16;
	private String AUDIO_RECORDER_TEMP_FILE;
	private String songname;
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private AudioRecord recorder = null;
	private int bufferSize = 0;
	private Thread recordingThread = null;
	private boolean isRecording = false;

	
	public RecorderStream(String Fname,int totalsongbytes){
		//FileName=Fname;
		AUDIO_RECORDER_TEMP_FILE=Fname+"-temp";
		songname=Fname;
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
		recordingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				writeAudioDataToFile();
			}
		},"AudioRecorder Thread");
	}

	public void startRecording(){
		recorder.startRecording();
		isRecording = true;
		recordingThread.start();
	}

	public void stopRecording(){
		if(null != recorder){
			isRecording = false;

			recorder.stop();
			recorder.release();

			recorder = null;
			recordingThread = null;
		}
		
		//FillGap(getTempFilename());
		//Tools.copyWaveFile(getTempFilename()+"-f",getFilename(),bufferSize);
		//deleteTempFile();
	}

	
	
	private void writeAudioDataToFile(){
		byte data[] = new byte[bufferSize];
		String filename = Tools.getTempFilename(AUDIO_RECORDER_TEMP_FILE);
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int read = 0;

		if(null != os){
			while(isRecording){
				read = recorder.read(data, 0, bufferSize);

				if(AudioRecord.ERROR_INVALID_OPERATION != read){
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	private String getFilename(){
//		String filepath = Environment.getExternalStorageDirectory().getPath();
//		File file = new File(filepath,AUDIO_RECORDER_FOLDER);
//
//		if(!file.exists()){
//			file.mkdirs();
//		}
//
//		return (file.getAbsolutePath() + "/" + songname + "-rec" + AUDIO_RECORDER_FILE_EXT_WAV);
//	}
//	
//	private String getTempFilename(){
//		String filepath = Environment.getExternalStorageDirectory().getPath();
//		File file = new File(filepath,AUDIO_RECORDER_FOLDER);
//
//		if(!file.exists()){
//			file.mkdirs();
//		}
//
//		File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
//
//		if(tempFile.exists())
//			tempFile.delete();
//
//		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
//	}
//	private void deleteTempFile() {
//		File file = new File(getTempFilename());
//		file.delete();
//	}

}
