package com.tracktrixlite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;

public class Tools {

	private static final int RECORDER_BPP = 16;
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final String AUDIO_RECORDER_FOLDER = "Tractrix-Lite";
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private static ByteArrayOutputStream FillGap(String RecFileName, long difference, long RecFileLength) throws Exception{
		ByteArrayOutputStream out= new ByteArrayOutputStream();
		FileInputStream rec= new FileInputStream(RecFileName);
		byte[] inputdata = new byte[(int)RecFileLength];
		rec.read(inputdata);//input data should now hold entire recording
		rec.close();//close input stream
		out.write(inputdata);
		deleteTempFile(RecFileName);
		inputdata=null;
		byte[] emptygaparray= new byte[(int) difference];
		out.write(emptygaparray); // the output now holds the song
		out.close();
		emptygaparray=null;
		return out;
	}
	public static void RenderAudio(String SongFileName, String RecFileName){
		File SongFile=new File(SongFileName);
		File RecFile= new File(RecFileName);
		long sizeofSong=SongFile.length()-44; // Size of song minus the 44 kb Header
		long sizeofRec=RecFile.length();
		assert(sizeofSong>sizeofRec);// make sure the recording is smaller
		long difference=sizeofSong-sizeofRec;
		try {			
			byte[] recordingbytes=FillGap(RecFileName,difference,sizeofRec).toByteArray();
			FileInputStream songinput= new FileInputStream(SongFileName);
			byte[] songbytes = new byte[(int)sizeofSong];
			songinput.read(songbytes);
			songinput.close();
			songbytes=CenterChannelFilter(songbytes);//filter the song
			for(int i=0; i<songbytes.length; i=i+4){//for each 
				songbytes[i]=(byte) ((((int)songbytes[i])/2) +((int)recordingbytes[i]));
				songbytes[i+1]=(byte) ((((int)songbytes[i+1])/2) +((int)recordingbytes[i+1]));
				songbytes[i+2]=(byte) ((((int)songbytes[i+2])/2) +((int)recordingbytes[i+2]));
				songbytes[i+3]=(byte) ((((int)songbytes[i+3])/2) +((int)recordingbytes[i+3]));
			}
			FileOutputStream out= new FileOutputStream(SongFileName+"-f");
			out.write(songbytes);
			out.close();
			copyWaveFile(SongFileName+"-f",SongFileName+"-rec",AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING));
			deleteTempFile(SongFileName+"-f");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not Fill Gap");
		}
			}



	public static void copyWaveFile(String inFilename,String outFilename,int bufferSize){
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			//AppLog.logString("File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while(in.read(data) != -1){
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void WriteWaveFileHeader(
			FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels,
			long byteRate) throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R';  // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';  // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;  // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8);  // block align
		header[33] = 0;
		header[34] = RECORDER_BPP;  // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}


	public static String getTempFilename(String AUDIO_RECORDER_TEMP_FILE){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

		if(tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

	private static void deleteTempFile(String AUDIO_RECORDER_TEMP_FILE) {
		File file = new File(getTempFilename(AUDIO_RECORDER_TEMP_FILE));
		file.delete();
	}

	public static byte[] CenterChannelFilter(byte[] incomingBuffer){
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

		public static String getFilename(String AUDIO_RECORDER_TEMP_FILE){
			String filepath = Environment.getExternalStorageDirectory().getPath();
			File file = new File(filepath,AUDIO_RECORDER_FOLDER);
	
			if(!file.exists()){
				file.mkdirs();
			}
	
			return (file.getAbsolutePath() + AUDIO_RECORDER_TEMP_FILE +"-TrackTrixMix");
		}
}

