package com.ramis.keepchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.content.Intent;
import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class Settings extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private final String PACKAGE_NAME = "com.ramis.keepchat";

	public static final String PREF_KEY_SNAP_IMAGES = "pref_key_snaps_images";
	public static final String PREF_KEY_SNAP_VIDEOS = "pref_key_snaps_videos";
	public static final String PREF_KEY_STORIES_IMAGES = "pref_key_stories_images";
	public static final String PREF_KEY_STORIES_VIDEOS = "pref_key_stories_videos";
	public static final String PREF_KEY_TOASTS_DURATION = "pref_key_toasts_duration";
	public static final String PREF_KEY_SAVE_LOCATION = "pref_key_save_location";
	public static final String PREF_KEY_ANALYTICS = "pref_key_analytics";

	private File source;
	private File dest;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesMode(
				Context.MODE_WORLD_READABLE);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// check if preference exists in SharedPreferences
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		if (sharedPref.contains(PREF_KEY_SAVE_LOCATION) == false) {
			// set default value
			String root = Environment.getExternalStorageDirectory().toString();
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(PREF_KEY_SAVE_LOCATION, root + "/keepchat");
			editor.apply();
		}

		// set on click listener
		Preference locationChooser = findPreference(PREF_KEY_SAVE_LOCATION);
		locationChooser
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {

						// opens new activity which asks the user to choose path
						final Intent chooserIntent = new Intent(getActivity(),
								DirectoryChooserActivity.class);
						startActivityForResult(chooserIntent, 0);
						return true;
					}
				});

		updateSummary(PREF_KEY_SNAP_IMAGES);
		updateSummary(PREF_KEY_SNAP_VIDEOS);
		updateSummary(PREF_KEY_STORIES_IMAGES);
		updateSummary(PREF_KEY_STORIES_VIDEOS);
		updateSummary(PREF_KEY_TOASTS_DURATION);
		updateSummary(PREF_KEY_SAVE_LOCATION);

		source = new File(Environment.getDataDirectory(), "data/"
				+ PACKAGE_NAME + "/shared_prefs/" + PACKAGE_NAME
				+ "_preferences" + ".xml");
		dest = new File(getActivity().getExternalFilesDir(null), PACKAGE_NAME
				+ "_preferences" + ".xml");

		if (source.exists()) {
			try {
				copy(source, dest);
			} catch (IOException e) {
			}
			try {
				copy(source, dest);
			} catch (IOException e) {
			}
		}
		
		boolean analytics = sharedPref.getBoolean(PREF_KEY_ANALYTICS, false);

		if (analytics == true){
			GoogleAnalytics.getInstance(MainActivity.context).setAppOptOut(true);
		} else {
			GoogleAnalytics.getInstance(MainActivity.context).setAppOptOut(false);
		}

		Tracker t = GoogleAnalytics.getInstance(MainActivity.context).newTracker(R.xml.app_tracker);

		t.setScreenName("Settings");
		
		t.send(new HitBuilders.EventBuilder()
        .setCategory("Activity Opened")
        .setAction("Settings")
        .build());
	}

	@Override
	// function runs when the path chooser activity returns
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(
						PREF_KEY_SAVE_LOCATION,
						data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
				editor.apply();
				updateSummary(PREF_KEY_SAVE_LOCATION);
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updateSummary(key);

		if (source.exists()) {
			try {
				copy(source, dest);
			} catch (IOException e) {
			}
			try {
				copy(source, dest);
			} catch (IOException e) {
			}
		}

	}

	private void updateSummary(String key) {
		if (key.equals(PREF_KEY_SNAP_IMAGES)
				|| key.equals(PREF_KEY_SNAP_VIDEOS)
				|| key.equals(PREF_KEY_STORIES_IMAGES)
				|| key.equals(PREF_KEY_STORIES_VIDEOS)
				|| key.equals(PREF_KEY_TOASTS_DURATION)) {
			ListPreference changedPref = (ListPreference) findPreference(key);
			changedPref.setSummary(changedPref.getEntry());
		} else if (key.equals(PREF_KEY_SAVE_LOCATION)) {
			Preference changedPref = findPreference(key);
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			changedPref.setSummary(sharedPref.getString(PREF_KEY_SAVE_LOCATION,
					""));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void copy(File src, File dst) throws IOException {

		if (dst.exists()) {
			dst.delete();
		}
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

}
