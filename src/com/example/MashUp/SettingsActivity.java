package com.example.MashUp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridLayout;

import java.util.List;

import com.MashUp.MainActivity;
import com.MashUp.Tools;
import com.example.MashUp.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */




import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// show the current value in the settings screen
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			initSummary(getPreferenceScreen().getPreference(i));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference p = findPreference(key);
		if (p instanceof ListPreference) {
			ListPreference editTextPref = (ListPreference) p;
			if (editTextPref.getKey().equals("renderMode")) {
				if (editTextPref.getValue().equals("1")) {
					Tools.slowmode=true;
					System.out.println("is Slow set");
				} else {
					Tools.slowmode=false;
					System.out.println("is Fast set");
				}
			} else if (editTextPref.getKey().equals("backgroundTheme")) {
				Tools.Background = editTextPref.getValue();
				int id = getResources().getIdentifier(Tools.Background, "drawable", getPackageName());
				
//				((GridLayout) findViewById(R.id.screen)).setBackground(getResources().getDrawable(id));
//				((GridLayout) findViewById(R.id.screen_song_list)).setBackground(getResources().getDrawable(id));
			}
			Tools.updateSettingsFile(Tools.slowmode, Tools.Background);
		}
	}

	private void initSummary(Preference p) {
		if (p instanceof PreferenceCategory) {
			PreferenceCategory pGrp = (PreferenceCategory) p;
			for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
				initSummary(pGrp.getPreference(i));
			}
		} else {
			updatePreferences(p);
		}
	}

	private void updatePreferences(Preference p) {
		if (p instanceof ListPreference) {
			ListPreference editTextPref = (ListPreference) p;
			if (editTextPref.getKey().equals("renderMode")) {
				if (Tools.slowmode) {
					editTextPref.setValue("1");
				} else {
					editTextPref.setValue("2");
				}
			} else if (editTextPref.getKey().equals("backgroundTheme")) {
				editTextPref.setValue(Tools.Background);
			}
		}
	}

}