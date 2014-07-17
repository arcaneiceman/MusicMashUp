package com.example.MashUp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.MashUp.MainActivity;
import com.MashUp.Tools;
import com.example.MashUp.R;

import android.net.Uri;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SongListActivity extends Activity {

	private ArrayList<Song> songList;
	private ListView songView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_list);
		songView = (ListView) findViewById(R.id.song_list);
		int id = getResources().getIdentifier(Tools.Background,
				"drawable", getPackageName());
		((RelativeLayout) findViewById(R.id.screen_song_list))
				.setBackground(getResources().getDrawable(id));

		String fontPath = "fonts/ARROY.TTF";
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		((TextView) findViewById(R.id.TOP)).setTypeface(tf);

		songList = new ArrayList<Song>();
		getSongList();
		Collections.sort(songList, new Comparator<Song>() {
			public int compare(Song a, Song b) {
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		SongAdapter songAdt = new SongAdapter(this, songList);
		songView.setAdapter(songAdt);
	}

	public void songClicked(View view) {
		MainActivity.currSong = songList.get(Integer.parseInt(view.getTag()
				.toString()));
		// Integer.parseInt(view.getTag().toString());
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(Intent.EXTRA_TEXT, "song_changed");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	public void getSongList() {
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null,
				null);
		if (musicCursor != null && musicCursor.moveToFirst()) {
			// get columns
			int titleColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			// add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				String path = musicCursor
						.getString(musicCursor
								.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA));
				if (path.contains(".mp3"))
					songList.add(new Song(thisId, thisTitle, thisArtist, path));
			} while (musicCursor.moveToNext());
		}
	}
}
