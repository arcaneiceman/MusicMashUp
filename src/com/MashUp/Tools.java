package com.MashUp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;

public class Tools {

	//Timing variable
	public static long starttime;
	public static long endtime;


	//ALL STATIC Variables
	private static final int RECORDER_BPP = 16;
	private static final int RECORDER_SAMPLERATE = 44100;
	public static final String APP_FOLDER = "Tracktrix-Lite";
	public static final String AUDIO_FILE_EXT_WAV = ".wav";
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	public static String StoragePath=android.os.Environment.getExternalStorageDirectory()+"/"+APP_FOLDER+"/";
	public static boolean slowmode=false;
	public static String Background="bg_three";


	private static ByteArrayOutputStream FillGap(String PathtoRecFile, long difference, long RecFileLength) throws Exception{
		ByteArrayOutputStream out= new ByteArrayOutputStream();
		//		//Fill the start gap
		//		long time=endtime-starttime;
		//		int numofbytes=(44100*2*((int)time))/1000;
		//		byte[] startgap= new byte[numofbytes];
		//		out.write(startgap);
		//		//End of FillStartGap
		FileInputStream rec= new FileInputStream(PathtoRecFile);
		byte[] inputdata = new byte[(int)RecFileLength];
		rec.read(inputdata);//input data should now hold entire recording
		rec.close();//close input stream
		out.write(inputdata);
		deleteFile(PathtoRecFile);
		inputdata=null;
		byte[] emptygaparray= new byte[(int) difference];
		out.write(emptygaparray); // the output now holds the song
		out.close();
		emptygaparray=null;
		return out;
	}

	public static String RenderAudio(String PathtoSong, String PathtoRec, String SongName){
		/*
		 * First We have to decide what type of song is coming....
		 */
		System.out.println("RENDER : PATH TO AUDIO is : " +PathtoSong);
		if(PathtoSong.endsWith(".mp3")|| PathtoSong.endsWith(".MP3")){
			//The Song is an MP3 File
			return RenderMP3(PathtoSong,PathtoRec,SongName);
		}
		else{
			//The Song is a WAV File. 
			return RenderWav(PathtoSong,PathtoRec,SongName);
		}
	}

	private static String RenderMP3(String PathtoSong,String PathtoRec, String SongName){
		try {
			String FilteredSong=FilterMP3ToWavConveterter(PathtoSong,SongName);
			if(FilteredSong==null){
				//Failed to convert
				return null;
			}

			File SongFile=new File(FilteredSong);
			File RecFile= new File(PathtoRec);
			long sizeofsong=SongFile.length()/2;
			long sizeofRec=RecFile.length();
			assert(sizeofsong>sizeofRec);
			System.out.println("Lenght of Song is " + sizeofsong + " half of this is : " + sizeofsong/2);
			System.out.println("Lenght of the Recording is : " + sizeofRec + " (Should probably be half)");
			FileInputStream songinputStream= new FileInputStream(SongFile);
			FileInputStream recinputStream= new FileInputStream(RecFile);
			FileOutputStream out= new FileOutputStream(Tools.getFilename(SongName, "-HL"));
			byte[] songbytes= new byte[4];
			byte[] recbytes=  new byte[2];
			for(int i=0; i<sizeofRec; i=i+2){//for each 
				songinputStream.read(songbytes);//songbytes now contains 1 sample of stero sound (4 bytes)
				recinputStream.read(recbytes);//recbytes now contains 1 sample of mono sound (2 bytes)

				songbytes[0]=(byte) ((((int)songbytes[0])/2) +((int)recbytes[0])/2);
				songbytes[1]=(byte) ((((int)songbytes[1])/2) +((int)recbytes[1])/2);
				songbytes[2]=(byte) ((((int)songbytes[2])/2) +((int)recbytes[0])/2);
				songbytes[3]=(byte) ((((int)songbytes[3])/2) +((int)recbytes[1])/2);

				out.write(songbytes);//writing the modified song byte buffer into the file
			}
			songinputStream.close();
			recinputStream.close();
			out.close();
			System.gc();
			System.out.println("Done with writing final headerless file");
			//song is now in songname with tag -HL for headerless
			String outputfilename=Tools.getFilename(SongName, "-final");
			copyWaveFile(Tools.StoragePath+SongName+"-HL"+Tools.AUDIO_FILE_EXT_WAV,outputfilename,AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING));
			deleteFile(Tools.StoragePath+SongName+"-HL"+Tools.AUDIO_FILE_EXT_WAV);
			deleteFile(Tools.StoragePath+SongName+"-f"+Tools.AUDIO_FILE_EXT_WAV);
			deleteFile(Tools.StoragePath+SongName+"-t"+Tools.AUDIO_FILE_EXT_WAV);
			System.out.println("Wrapping up");
			return outputfilename;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	private static String RenderWav(String PathtoSong,String PathtoRec, String SongName){
		try{
			System.out.println("Render Wav : Path to song is: " + PathtoSong);
			File SongFile=new File(PathtoSong);// already headerless song
			File RecFile= new File(PathtoRec);// mono rec track

			long sizeofsong=SongFile.length()/2;
			long sizeofRec=RecFile.length();
			assert(sizeofsong>sizeofRec);
			System.out.println("Lenght of Song is " + sizeofsong);
			System.out.println("Lenght of the Recording is : " + sizeofRec);
			FileInputStream songinputStream= new FileInputStream(SongFile);
			FileInputStream recinputStream= new FileInputStream(RecFile);
			FileOutputStream out= new FileOutputStream(Tools.getFilename(SongName, "-HL"));
			byte[] songbytes= new byte[4];
			byte[] recbytes=  new byte[2];
			for(int i=0; i<sizeofRec; i=i+2){//for each 
				songinputStream.read(songbytes);//songbytes now contains 1 sample of stero sound (4 bytes)
				recinputStream.read(recbytes);//recbytes now contains 1 sample of mono sound (2 bytes)
				
				songbytes=CenterChannelFilter(songbytes);//song bytes are now filtered
				
				songbytes[0]=(byte) ((((int)songbytes[0])/2) +((int)recbytes[0])/2);
				songbytes[1]=(byte) ((((int)songbytes[1])/2) +((int)recbytes[1])/2);
				songbytes[2]=(byte) ((((int)songbytes[2])/2) +((int)recbytes[0])/2);
				songbytes[3]=(byte) ((((int)songbytes[3])/2) +((int)recbytes[1])/2);

				out.write(songbytes);//writing the modified song byte buffer into the file
			}
			songinputStream.close();
			recinputStream.close();
			out.close();
			System.gc();
			System.out.println("Done with writing final headerless file");
			//song is now in songname with tag -HL for headerless
			String outputfilename=Tools.getFilename(SongName, "-final");
			copyWaveFile(Tools.StoragePath+SongName+"-HL"+Tools.AUDIO_FILE_EXT_WAV,outputfilename,AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING));
			deleteFile(Tools.StoragePath+SongName+"-HL"+Tools.AUDIO_FILE_EXT_WAV);
			deleteFile(Tools.StoragePath+SongName+"-t"+Tools.AUDIO_FILE_EXT_WAV);
			System.out.println("Wrapping up");
			return outputfilename;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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


	public static String getFilename(String FileName, String Tag){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,APP_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		File tempFile = new File(filepath,FileName+Tag+AUDIO_FILE_EXT_WAV);

		if(tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + FileName+Tag+AUDIO_FILE_EXT_WAV);
	}

	private static void deleteFile(String Path) {
		File file = new File(Path);
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

	public static void updateSettingsFile(boolean ConversionMode, String Background){
		/*
		 * Mode True = slow mode
		 * 
		 * Mode False = fast mode
		 */
		File tempFile= new File(StoragePath,"Settings.txt");
		if(!tempFile.exists()){
			createSettingsfile();
			System.out.println("Seems like the settings files didnt exist!");
		}
		try{
			FileWriter FW=new FileWriter(tempFile);
			if(ConversionMode){
				FW.write("mode=slow\n");
			}
			else{
				FW.write("mode=fast\n");
			}
			FW.write(Background);
			FW.flush();
			FW.close();
		}
		catch(Exception e){
			System.err.println("Couldnt change preference");
		}
	}

	public static void loadSettings(){
		System.out.println("in Load Settings");
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,APP_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}
		File tempFile = new File(StoragePath,"Settings.txt");
		//it should exist!!
		if(!tempFile.exists()){
			System.err.println("Something went wrong");
		}
		else{
			//it exists so:
			System.out.println("File Found");
			try {
				BufferedReader R = new BufferedReader(new FileReader(tempFile));
				String mode=R.readLine();
				System.out.println("Read line : " + mode);
				System.out.println("File Found");
				if(mode.contains("slow")){
					slowmode=true;
					System.out.println("In slow Mode");
				}
				else if(mode.contains("fast")){
					slowmode=false;
					System.out.println("In Fast Mode");
				}
				else{
					//it contained neiher whcih means someone messed with it...
					Tools.slowmode=false;
					System.out.println("Defaulted to Fast Mode");
				}
				Tools.Background=R.readLine();
				R.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createSettingsfile(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,APP_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		File tempFile = new File(StoragePath,"Settings.txt");
		if(!tempFile.exists()){//if file does not exist, create a new one
			System.out.println("File did not exit");
			try {
				tempFile.createNewFile();
				FileWriter FW= new FileWriter(tempFile);
				FW.write("mode=fast\n");
				FW.write("bg_three");
				FW.flush();
				FW.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("File Existed!");
	}
	public static void createAppDirectory(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,APP_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}
	}

	public static String FilterMP3ToWavConveterter(String PathtoInput, String SongName) throws IOException{
		String outfilename=Tools.getFilename(SongName, "-f");
		FileOutputStream outputStream=new FileOutputStream(outfilename);
		MediaExtractor extractor = new MediaExtractor();
		try {
			extractor.setDataSource(PathtoInput);
		} catch (Exception e) {
			System.err.println("NO WORK");
			outputStream.close();
			return null;
		}
		MediaFormat format = extractor.getTrackFormat(0);
		String mime = format.getString(MediaFormat.KEY_MIME);

		// the actual decoder
		MediaCodec decoder = MediaCodec.createDecoderByType(mime);
		decoder.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
		int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
		assert(sampleRate==44100);
		boolean done=false;
		decoder.start();
		ByteBuffer[] InputBuffers = decoder.getInputBuffers();
		ByteBuffer[] OutputBuffers = decoder.getOutputBuffers();
		ByteBuffer inputBuffer;
		ByteBuffer outputBuffer;
		int inputBufferIndex;
		int outputBufferIndex;
		byte[] outputdata;
		extractor.selectTrack(0);
		while(!done){
			//System.out.println("Ruuing in loop");
			inputBufferIndex=decoder.dequeueInputBuffer(-1);
			if(inputBufferIndex>=0){
				inputBuffer = InputBuffers[inputBufferIndex];
				inputBuffer.clear();
				int sampleSize =extractor.readSampleData(inputBuffer, 0 /* offset */);
				//place some code here because -1 sample size means we stop
				if(sampleSize==-1){
					//end of stream
					System.out.println("End of Stream");
					done=true;
					break;
				}
				long presentationTimeUs = extractor.getSampleTime();
				extractor.advance();
				decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
			}
			BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			outputBufferIndex= decoder.dequeueOutputBuffer(bufferInfo, 50);//check 
			while(outputBufferIndex >=0){//make sure... this could be an if only
				outputBuffer = OutputBuffers[outputBufferIndex];

				outputBuffer.position(bufferInfo.offset);
				outputBuffer.limit(bufferInfo.offset+ bufferInfo.size);

				outputdata= new byte[bufferInfo.size];
				outputBuffer.get(outputdata);
				//System.out.println("Lenght of outputdata is " + outputdata.length);
				outputdata=Tools.CenterChannelFilter(outputdata);

				outputStream.write(outputdata, 0, outputdata.length);

				decoder.releaseOutputBuffer(outputBufferIndex, false);
				outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
			}
		}
		outputStream.close();
		extractor.release();
		decoder.stop();
		decoder.release();
		System.out.println("Done with convertion outfilename: " +outfilename);
		return outfilename;
	}

	public static void MP3ToWavConverter(String PathtoInput, String PathtoOutput) throws IOException{
		File outFile= new File(PathtoOutput);
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		FileOutputStream outputStream=new FileOutputStream(outFile);
		MediaExtractor extractor = new MediaExtractor();
		try {
			extractor.setDataSource(PathtoInput);
		} catch (Exception e) {
			System.err.println("NO WORK");
			outputStream.close();
			return;
		}
		MediaFormat format = extractor.getTrackFormat(0);
		String mime = format.getString(MediaFormat.KEY_MIME);

		// the actual decoder
		MediaCodec decoder = MediaCodec.createDecoderByType(mime);
		decoder.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
		int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
		assert(sampleRate==44100);
		boolean done=false;
		decoder.start();
		ByteBuffer[] InputBuffers = decoder.getInputBuffers();
		ByteBuffer[] OutputBuffers = decoder.getOutputBuffers();
		ByteBuffer inputBuffer;
		ByteBuffer outputBuffer;
		int inputBufferIndex;
		int outputBufferIndex;
		byte[] outputdata;
		extractor.selectTrack(0);
		while(!done){
			//System.out.println("Ruuing in loop");
			inputBufferIndex=decoder.dequeueInputBuffer(-1);
			if(inputBufferIndex>=0){
				inputBuffer = InputBuffers[inputBufferIndex];
				inputBuffer.clear();
				int sampleSize =extractor.readSampleData(inputBuffer, 0 /* offset */);
				//place some code here because -1 sample size means we stop
				if(sampleSize==-1){
					//end of stream
					System.out.println("End of Stream");
					done=true;
					break;
				}
				long presentationTimeUs = extractor.getSampleTime();
				extractor.advance();
				decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
			}
			BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			outputBufferIndex= decoder.dequeueOutputBuffer(bufferInfo, 50);//check 
			while(outputBufferIndex >=0){//make sure... this could be an if only
				outputBuffer = OutputBuffers[outputBufferIndex];

				outputBuffer.position(bufferInfo.offset);
				outputBuffer.limit(bufferInfo.offset+ bufferInfo.size);

				outputdata= new byte[bufferInfo.size];
				outputBuffer.get(outputdata);

				//outputdata=Tools.CenterChannelFilter(outputdata);

				outputStream.write(outputdata, 0, outputdata.length);

				decoder.releaseOutputBuffer(outputBufferIndex, false);
				outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
			}
		}
		outputStream.close();
		extractor.release();
		decoder.stop();
		decoder.release();
	}
}

