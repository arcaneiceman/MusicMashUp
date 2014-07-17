package com.MashUp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.MashUp.SettingsActivity;
import com.example.MashUp.Song;
import com.example.MashUp.SongListActivity;
import com.example.MashUp.R;

import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	static public boolean isSlow = false;
	static public Song currSong;
	static public boolean noFilter = true;
	static public String Background = "bg_three";
	private PopupWindow pw;

	static private boolean firsttrip=true;
	// UI Variables
	Button restartBtn;
	Button renderBtn;
	Button Filter0;
	Button Filter1;
	Button LyricsButton;
	TextView SongNameField;
	TextView SongAlbumName;
	TextView SongArtist;
	TextView Status;
	Button playBtn;
	Button stopBtn;
	Button recordBtn;
	ProgressDialog dialog;
	ProgressDialog Renderdialog;
	MediaMetadataRetriever fetchmetadata;
	ActionBar actionBar;

	public void LoadUIVariables() {
		SongNameField = (TextView) findViewById(R.id.SongTitle);
		SongAlbumName = (TextView) findViewById(R.id.AlbumName);
		SongArtist = (TextView) findViewById(R.id.Artist);
		Filter0 = (Button) findViewById(R.id.radio0);
		Filter1 = (Button) findViewById(R.id.radio1);
		LyricsButton = (Button) findViewById(R.id.lyrics_button);
		playBtn = (Button) findViewById(R.id.play_pause_button);
		restartBtn = (Button) findViewById(R.id.restart_button);
		renderBtn = (Button) findViewById(R.id.render_button);
		stopBtn = (Button) findViewById(R.id.stop_button);
		recordBtn = (Button) findViewById(R.id.record_button);
		Status= (TextView) findViewById(R.id.TOP);
	}

	public void SetFonts() {
		String fontPath = "fonts/ARROY.TTF";
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		((TextView) findViewById(R.id.TOP)).setTypeface(tf);
		((TextView) findViewById(R.id.SongTitle)).setTypeface(tf);
		((TextView) findViewById(R.id.AlbumName)).setTypeface(tf);
		((TextView) findViewById(R.id.Artist)).setTypeface(tf);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((SeekBar) findViewById(R.id.seekBar1)).setEnabled(false);
		((BitmapDrawable) ((GridLayout) findViewById(R.id.screen))
				.getBackground()).toString();
		int id = getResources().getIdentifier(MainActivity.Background,
				"drawable", getPackageName());
		((GridLayout) findViewById(R.id.screen)).setBackground(getResources()
				.getDrawable(id));
		if (noFilter) {
			((RadioButton) findViewById(R.id.radio0)).setChecked(true);
		} else {
			((RadioButton) findViewById(R.id.radio1)).setChecked(true);
		}
		LoadUIVariables();
		SetFonts();

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(id));
		actionBar.show();

		// Get the message from the intent
		Intent intent = getIntent();
		if (intent != null) {
			String message = intent.getStringExtra(Intent.EXTRA_TEXT);
			
			System.out.println("Intent message is " + message);
			if(!firsttrip){
				//So that this does not trigger on first create
				System.out.println("Current song is " + currSong.getPath());
				openfile_pressed(currSong.getPath());
				System.out.println("Temporary");
			}
//			}
//			System.out.println("Intent message is " + message);
			//System.out.println("Current song is " + currSong.getPath());
			//openfile_pressed();
//			if (message != null) {
//				if (message.equals("song_changed")) {
//					openfile_pressed();
//					return;
//				}
//			}
		}
		if(firsttrip){
			ResetUI();
			Tools.createAppDirectory();
			Tools.createSettingsfile();// if setting file is not created, create it
			Tools.loadSettings();
			firsttrip=false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.openfilebhv) {
			Intent intent = new Intent(MainActivity.this,
					SongListActivity.class);
			startActivity(intent);
		} else if (id == R.id.reset_button) {
			resetbutton_pressed();
		} else if (id == R.id.settingsbhv) {
			Intent intent = new Intent(MainActivity.this,
					SettingsActivity.class);
			startActivity(intent);
		} else if (id == R.id.exit_button) {
			systemexit_pressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		int id = getResources().getIdentifier(MainActivity.Background,
				"drawable", getPackageName());
		((GridLayout) findViewById(R.id.screen)).setBackground(getResources()
				.getDrawable(id));
		actionBar.setBackgroundDrawable(getResources().getDrawable(id));
		invalidateOptionsMenu();
	}

	public void EnableButton(Button button) {
		button.setEnabled(true);
		button.setAlpha(1f);
	}

	public void DisableButton(Button button) {
		button.setEnabled(false);
		button.setAlpha(0.5f);
	}

	public void ResetUI() {
		DisableButton(Filter0);
		DisableButton(Filter1);
		DisableButton(LyricsButton);
		DisableButton(playBtn);
		DisableButton(stopBtn);
		DisableButton(recordBtn);
		DisableButton(restartBtn);
		DisableButton(renderBtn);
		SongNameField.setText("No Selection");
		SongAlbumName.setText("N/A");
		SongArtist.setText("N/A");
		Status.setText("Status:StartUp");
	}

	public void play_pause_button_pressed(View view) {
		System.out.println("Play/Pause Button Pressed");
		if (AudioSystem.songloaded == false) {
			return;
		}

		if(AudioSystem.currentsongplayer.getPlaying()){
			//the song is loaded and playbackloop is active and UI is initialized
			if(AudioSystem.currentsongplayer.getispause()){
				//pause is true which means we should play!
				playBtn.setBackgroundResource(R.drawable.pause_icon);
				AudioSystem.PlaySong(); //Actual Code line
				Status.setText("Status:Playing");
			}
			else{
				//pause is false which means we should pause
				playBtn.setBackgroundResource(R.drawable.play_icon);
				AudioSystem.PauseSong();
				Status.setText("Status:Paused");
			}
		}
		else{
			//the song is loaded but playbackloop is not active so we play the song and set UI controls
			AudioSystem.PlaySong(); //Actual Code line

			/*Effects Area*/
			playBtn.setBackgroundResource(R.drawable.pause_icon);
			Status.setText("Status:Playing");
		}
		//		if (AudioSystem.currentsongplayer.getPlaying()) {
		//			// if already playing
		//			AudioSystem.PauseSong();
		//			playBtn.setBackgroundResource(R.drawable.playbutton);
		//			//DisableButton(stopBtn);
		//		} else {
		//			// not previously playing
		//
		//			AudioSystem.PlaySong(); // Actual Code line
		//
		//			/* Effects Area */
		//			playBtn.setBackgroundResource(R.drawable.pausebutton);
		//			EnableButton(stopBtn);
		//			EnableButton(Filter0);
		//			EnableButton(Filter1);
		//			EnableButton(LyricsButton);
		//			// playBtn.setC
		//		}

	}
	
	public void restartbutton_pressed(View view){
		System.out.println("Restart Button Pressed");
		//restart recording
		EnableButton(LyricsButton);
		EnableButton(Filter0);
		EnableButton(Filter1);
		EnableButton(recordBtn);
		DisableButton(restartBtn);
		DisableButton(renderBtn);
		DisableButton(playBtn);
		DisableButton(stopBtn);
		recordBtn.setText("StopRec");
		Status.setText("Status:Rec");
		if(AudioSystem.currentsongplayer.getPlaying()==false){
			playBtn.setBackgroundResource(R.drawable.pause_icon);
		}
		//Actual Stuff
		AudioSystem.StartRecording();
	}

	public void stopbutton_pressed(View view) {
		if (AudioSystem.songloaded == false) {
			return;
		}
		if(AudioSystem.currentsongplayer.getPlaying()){
			//if the song is loaded loop is running
			AudioSystem.StopSong();// Actual Code
			//UI Effects Area 
			playBtn.setBackgroundResource(R.drawable.play_icon);
			Status.setText("Status:Stopped");
		}	
//		System.out.println("Stop Button Pressed");
//		AudioSystem.StopSong();// Actual Code
//		playBtn.setBackgroundResource(R.drawable.play_icon);
//		System.out.println("Stop Button Pressed");
	}

	public void openfile_pressed(String path) {

		// HARD CODED TO OPEN ONE FILE RIGHT NOW
		if (AudioSystem.songloaded == true) {
			System.out.println("Song loaded was true , restarting system");
			AudioSystem.SystemReset();
		}

		//String path = android.os.Environment.getExternalStorageDirectory() + "/Music/All.mp3";
		// Log.e("MSG", "Path is : " + path);
		//System.out.println("Path is : " + currSong.getPath());
		fetchmetadata = new MediaMetadataRetriever();
		//fetchmetadata.setDataSource(currSong.getPath());
		fetchmetadata.setDataSource(path);
		new ConversionThread().execute(path);

	}

	public void resetbutton_pressed() {
		if (AudioSystem.songloaded == false) {
			return;
		}
		AudioSystem.SystemReset();
		ResetUI();
		System.gc();
	}

	public void recordbutton_pressed(View view) {
		if (AudioSystem.songloaded == false) {
			return;
		}
		System.out.println("Record Button Pressed");
		if (AudioSystem.recording == false) {
			// not recording already so start:
			//Reset Timers:
			Tools.starttime=0;
			Tools.endtime=0;
			DisableButton(playBtn);
			DisableButton(stopBtn);
			recordBtn.setText("Stop Rec");
			if (AudioSystem.currentsongplayer.getPlaying() == false) {
				playBtn.setBackgroundResource(R.drawable.pause_icon);
			}
			Status.setText("Status:Rec");
			// Actual Stuff
			AudioSystem.StartRecording();
		} else {
			// recording so stop recording
			AudioSystem.StopRecording();
			recordBtn.setText("Record");
			DisableButton(recordBtn);
			DisableButton(LyricsButton);
			DisableButton(Filter0);
			DisableButton(Filter1);
			EnableButton(restartBtn);
			EnableButton(renderBtn);
			Status.setText("Status:Rec Stopped");
		}

	}

	public String fetchLyrics(Song song) {
		boolean first_exception = false;
		String lyrics_default = "--- No Lyrics available ---";

		try {
			String url = "http://www.azlyrics.com/lyrics/"
					+ song.getArtist().toLowerCase() + "/"
					+ song.getTitle().toLowerCase() + ".html";

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String result = sb.toString();

			String temp_ = result.substring(result
					.indexOf("start of lyrics -->") + 19);

			temp_ = temp_.substring(0, temp_.indexOf("end of lyrics") - 5);
			temp_ = temp_.replace("<br />", "");
			temp_ = temp_.replaceAll("\\<.*?>", "");
			lyrics_default = temp_;

		} catch (Exception e) {
			lyrics_default = "--- No Lyrics available ---";
			first_exception = true;
		}

		if (first_exception) {
			try {
				String url = "http://www.lyricsmode.com/lyrics/"
						+ song.getArtist().toLowerCase().toCharArray()[0] + "/"
						+ song.getArtist().toLowerCase().replace(" ", "_")
						+ "/" + song.getTitle().toLowerCase().replace(" ", "_")
						+ ".html";

				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				String result = sb.toString();

				String temp_ = result
						.substring(result
								.indexOf("<p id=\"lyrics_text\" class=\"ui-annotatable\">") + 43);
				temp_ = temp_.substring(0, temp_.indexOf("<p id=") - 4);
				temp_ = temp_.replace("<br />", "");
				temp_ = temp_.replaceAll("\\<.*?>", "");
				lyrics_default = temp_;
			} catch (Exception e1) {
				lyrics_default = "--- No Lyrics available ---";
			}
		}

		return lyrics_default;
	}

	public void lyricsbutton_pressed(View view) {
		// if (AudioSystem.songloaded == false) {
		// return;
		// }

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popup, null, false);
		pw = new PopupWindow(layout,
				(this.findViewById(R.id.screen)).getWidth() - 40,
				(this.findViewById(R.id.screen)).getHeight() - 40, true);
		// The code below assumes that the root container has an id called
		// 'main'
		pw.setAnimationStyle(R.style.PopupWindowAnimation);
		pw.showAtLocation(this.findViewById(R.id.screen), Gravity.CENTER, 0, 0);
		TextView lyricsView = (TextView) layout.findViewById(R.id.LyricsText);

		lyricsView.setText(fetchLyrics(currSong));

		ImageButton btncancel = (ImageButton) layout
				.findViewById(R.id.imageView_close);
		btncancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				pw.dismiss();
			}
		});
		// System.out.println("Lyrics Button Pressed");
	}

	public void renderbutton_pressed(View view) {
		if (AudioSystem.songloaded == false) {
			return;
		}
		System.out.println("Render Button Pressed");

		AsyncTask<String, String, String> RenderThread= new AsyncTask<String , String , String>(){
			@Override
			protected void onPreExecute(){
				super.onPreExecute();
				Renderdialog=ProgressDialog.show(MainActivity.this, "", "Rendering... This may take a few minutes");
				Renderdialog.setCancelable(false);
			}

			@Override
			protected String doInBackground(String... arg0) {
				return Tools.RenderAudio(AudioSystem.currentsongplayer.getCurrentSongFullPath(), AudioSystem.micstream.getCurrentRecordingFullPath(), AudioSystem.currentsongplayer.getSongName());

			}

			@Override
			protected void onPostExecute(String NewSongPath){
				Renderdialog.dismiss();
				if(NewSongPath!=null){
					//once its done resetting, reset the system and load new song path and play like nothing happend
					AudioSystem.SystemReset();
					ResetUI();
					System.out.println("System Reset! Now I would have started like we just started with the new song");
//					//Call new song! 
				}
				else{
					//Answer From Render was Null
					System.err.println("Render was Unsuccessful");
				}
			}
		};
		RenderThread.execute("hello");
	}

	public void systemexit_pressed() {
		System.gc();
		System.exit(1);
	}

	public void deactivate_filter(View view) {
		System.out.println("Deacticate Filter button pressed");
		// assuming its safe
		AudioSystem.currentsongplayer.setFilterStatus(false);
		;
		((RadioButton) findViewById(R.id.radio0)).setChecked(true);
		noFilter = true;
	}

	public void activate_filter(View view) {
		System.out.println("Activate Filter button pressed");
		// assuming its safe
		AudioSystem.currentsongplayer.setFilterStatus(true);
		((RadioButton) findViewById(R.id.radio1)).setChecked(true);
		noFilter = false;
	}

	public class ConversionThread extends AsyncTask<String, String, String> {

		public boolean success;
		public String Path;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = ProgressDialog.show(MainActivity.this, "",
					"Loading... This may take a few minutes");
			dialog.setCancelable(true);
			success = false;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			success = AudioSystem.LoadSong(params[0]);
			System.out.println("returning");
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			System.out.println("Post");
			dialog.dismiss();
			if (success) {
				System.out.println("Successful Load");
				AudioSystem.PlaySong();

				// set UI Variables
				playBtn.setBackgroundResource(R.drawable.pause_icon);

				SongNameField
				.setText(fetchmetadata
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
				SongAlbumName
				.setText(fetchmetadata
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
				SongArtist
				.setText(fetchmetadata
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

				EnableButton(playBtn);
				EnableButton(LyricsButton);
				EnableButton(Filter0);
				EnableButton(Filter1);
				EnableButton(stopBtn);
				EnableButton(recordBtn);
			} else {
				System.out
				.println("Could not Open this Song : Song Format Unsupported");
			}

		}

	}

	public static void StaticStopCall() {
		System.out.println("Here in static call");
		//playBtn.setBackgroundResource(R.drawable.play_icon);
	}

	public static void StaticStopRecordingCall(){
		AudioSystem.StopRecording();
		//		recordBtn.setText("Record");
		//		recordBtn.setEnabled(false);
		//		LyricsButton.setEnabled(false);
		//		Filter0.setEnabled(false);
		//		Filter1.setEnabled(false);
		//		//you can now restart or render
		//		restartBtn.setEnabled(true);
		//		renderBtn.setEnabled(true);
	}


}
