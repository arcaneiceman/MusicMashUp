package com.example.tracktrixlite;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	AudioPlayer Player;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment())
			.commit();
		}
		//ResetAll();
		Player= new AudioPlayer();
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

	public void ResetAll(){
		//Starting Initializations:
		Player= new AudioPlayer();

		//Starting Up UI defaults
		Button Filter0=(Button) findViewById(R.id.radio0);
		Button Filter1=(Button) findViewById(R.id.radio1);
		Button LyricsButton= (Button) findViewById(R.id.lyrics_button);
		Filter0.setEnabled(false);
		Filter1.setEnabled(false);
		LyricsButton.setEnabled(false);
	}
	
	public void play_pause_button_pressed(View view){
		System.out.println("Play/Pause Button Pressed");

		Button playBtn=(Button) findViewById(R.id.play_pause_button);
		Button Filter0=(Button) findViewById(R.id.radio0);
		Button Filter1=(Button) findViewById(R.id.radio1);
		Button LyricsButton= (Button) findViewById(R.id.lyrics_button);

		if(Player.currentsongplayer.playing){
			//if already playing
			Player.PauseSong();
			playBtn.setText("Play");
			playBtn.setBackgroundResource(R.drawable.play_icon);
		}
		else{
			//not previously playing

			Player.PlaySong(); //Actual Code line

			/*Effects Area*/
			playBtn.setText("Pause");
			playBtn.setBackgroundResource(R.drawable.pause_icon);
			Filter0.setEnabled(true);
			Filter1.setEnabled(true);
			LyricsButton.setEnabled(true);
			//playBtn.setC
		}

	}

	public void stopbutton_pressed(View view){
		Button Filter0=(Button) findViewById(R.id.radio0);
		Button Filter1=(Button) findViewById(R.id.radio1);
		Button LyricsButton= (Button) findViewById(R.id.lyrics_button);
		
		Player.StopSong();// Actual Code
		
		//UI Effects Area 
		Filter0.setEnabled(false);
		Filter1.setEnabled(false);
		LyricsButton.setEnabled(false);
		
		System.out.println("Stop Button Pressed");
	}

	public void pausebutton_pressed(View view){
		System.out.println("Pause Button Pressed");
	}
	
	public void openfile_pressed(View view){
		//HARD CODED TO OPEN ONE FILE RIGHT NOW
		String path= android.os.Environment.getExternalStorageDirectory()+ "/Music/ComeBackDown.wav";
		System.out.println("Path is : " + path);
		Player.LoadSong(path);
		Player.PlaySong();
	}

	public void deactivate_filter(View view){
		System.out.println("Deacticate Filter button pressed");
		//assuming its safe
		Player.currentsongplayer.CenterFilter=false;
	}

	public void activate_filter(View view){
		System.out.println("Activate Filter button pressed");
		//assuming its safe
		Player.currentsongplayer.CenterFilter=true;
	}

}
