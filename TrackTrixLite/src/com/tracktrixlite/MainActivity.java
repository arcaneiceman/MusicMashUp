package com.tracktrixlite;

import com.example.tracktrixlite.R;

import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

	//UI Variables
	Button restartBtn;
	Button renderBtn;
	Button saveBtn;
	Button Filter0;
	Button Filter1;
	Button LyricsButton;
	TextView SongNameField;
	TextView SongAlbumName;
	TextView SongArtist;
	Button playBtn;
	Button stopBtn;
	Button recordBtn;
	
	
	public void LoadUIVariables(){
		SongNameField = (TextView) findViewById(R.id.SongTitle);
		SongAlbumName = (TextView) findViewById(R.id.AlbumName);
		SongArtist    = (TextView) findViewById(R.id.Artist);
		Filter0=(Button) findViewById(R.id.radio0);
		Filter1=(Button) findViewById(R.id.radio1);
		LyricsButton= (Button) findViewById(R.id.lyrics_button);
		playBtn= (Button) findViewById(R.id.play_pause_button);
		restartBtn=(Button) findViewById(R.id.restart_button);
		renderBtn=(Button) findViewById(R.id.render_button);
		saveBtn= (Button) findViewById(R.id.save_button);
		stopBtn=(Button) findViewById(R.id.stop_button);
		recordBtn= (Button) findViewById(R.id.record_button);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LoadUIVariables();
		ResetUI();
		Tools.createAppDirectory();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void ResetUI(){
		Filter0.setEnabled(false);
		Filter1.setEnabled(false);
		LyricsButton.setEnabled(false);
		playBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		recordBtn.setEnabled(false);
		restartBtn.setEnabled(false);
		renderBtn.setEnabled(false);
		saveBtn.setEnabled(false);
		SongNameField.setText("No Selection");
		SongAlbumName.setText("N/A");
		SongArtist.setText("N/A");
	}
	
	public void play_pause_button_pressed(View view){
		System.out.println("Play/Pause Button Pressed");
		if(AudioSystem.songloaded==false){
			return;
		}
		
		if(AudioSystem.currentsongplayer.playing){
			//if already playing
			AudioSystem.PauseSong();
			playBtn.setBackgroundResource(R.drawable.play_icon);
		}
		else{
			//not previously playing

			AudioSystem.PlaySong(); //Actual Code line

			/*Effects Area*/
			playBtn.setBackgroundResource(R.drawable.pause_icon);
			Filter0.setEnabled(true);
			Filter1.setEnabled(true);
			LyricsButton.setEnabled(true);
			//playBtn.setC
		}

	}

	public void stopbutton_pressed(View view){
		if(AudioSystem.songloaded==false){
			return;
		}
		AudioSystem.StopSong();// Actual Code
		
		//UI Effects Area 
		Filter0.setEnabled(false);
		Filter1.setEnabled(false);
		LyricsButton.setEnabled(false);
		playBtn.setBackgroundResource(R.drawable.play_icon);
		System.out.println("Stop Button Pressed");
	}
	
	public void openfile_pressed(View view){
		//HARD CODED TO OPEN ONE FILE RIGHT NOW
		if(AudioSystem.songloaded==true){
			AudioSystem.SystemReset();
		}
		
		String path= android.os.Environment.getExternalStorageDirectory()+ "/Music/All.mp3";
		System.out.println("Path is : " + path);
		if(AudioSystem.LoadSong(path)){
			System.out.println("Successful Load");
			AudioSystem.PlaySong();
			
			//set UI Variables
			playBtn.setBackgroundResource(R.drawable.pause_icon);
			SongNameField.setText("All in All");
			SongAlbumName.setText("No Name Face");
			SongArtist.setText("LifeHouse");
			playBtn.setEnabled(true);
			LyricsButton.setEnabled(true);
			Filter0.setEnabled(true);
			Filter1.setEnabled(true);
			stopBtn.setEnabled(true);
			recordBtn.setEnabled(true);
		}
		else{
			System.out.println("Could not Open this Song : Song Format Unsupported");
		}
		
		
	}
	
	public void resetbutton_pressed(View view){
		if(AudioSystem.songloaded==false){
			return;
		}
		AudioSystem.SystemReset();
		ResetUI();
	}

	public void recordbutton_pressed(View view){
		if(AudioSystem.songloaded==false){
			return;
		}
		
		if(AudioSystem.recording==false){
			//not recording already so start:
			playBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			recordBtn.setText("Stop Rec");
			if(AudioSystem.currentsongplayer.playing==false){
				playBtn.setBackgroundResource(R.drawable.pause_icon);
			}
			//Actual Stuff
			AudioSystem.StartRecording();
		}
		else{
			//recording so stop recording
			AudioSystem.StopRecording();
			recordBtn.setText("Record");
			recordBtn.setEnabled(false);
			LyricsButton.setEnabled(false);
			Filter0.setEnabled(false);
			Filter1.setEnabled(false);
			restartBtn.setEnabled(true);
		}
		
	}
	
	public void lyricsbutton_pressed(View view){
		if(AudioSystem.songloaded==false){
			return;
		}
		System.out.println("Lyrics Button Pressed");
	}
	
	public void renderbutton_pressed(View view){
		if(AudioSystem.songloaded==false){
			return;
		}
		//Tools.RenderAudio(AudioSystem.currentsongplayer.songname, AudioSystem.currentsongplayer.songname+"-f");
	}
	
	
	public void deactivate_filter(View view){
		System.out.println("Deacticate Filter button pressed");
		//assuming its safe
		AudioSystem.currentsongplayer.CenterFilter=false;
	}

	public void activate_filter(View view){
		System.out.println("Activate Filter button pressed");
		//assuming its safe
		AudioSystem.currentsongplayer.CenterFilter=true;
	}

}
