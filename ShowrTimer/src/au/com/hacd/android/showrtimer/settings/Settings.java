package au.com.hacd.android.showrtimer.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Settings {
	
	/*
	 * Settings constants
	 */
	public static final String MINOR_SETTING = "minor";
	public static final String MAJOR_SETTING = "major";
	
	private static final String TAG = "Settings";
	
	private static Settings instance;
	
	private Map<String, Object> settings;
	
	private static final String PREFERENCE_FILE_NAME = "Settings";
	private SharedPreferences preferences;
	private Editor editor;
	
	private Settings(Context context) {
		Settings.instance = this;
		this.settings = new TreeMap<String, Object>();
		
		// load the preference file
		this.preferences = context.getSharedPreferences(Settings.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		this.editor = this.preferences.edit();
	}
	
	public static Settings getInstance(Context context) {
		Log.d(Settings.TAG, ">>> getInstance()");
		
		if(Settings.instance == null) {
			Settings.instance = new Settings(context);
			Log.d(Settings.TAG, "loading settings");
			instance.load();
		}
		
		Log.d(Settings.TAG, "<<< getInstance()");
		
		return Settings.instance;
	}
	
	public Object get(String key) {
		Log.d(Settings.TAG, ">>> get()");
		
		Log.d(Settings.TAG, "<<< get()");
		
		return this.settings.get(key);
	}
	
	public void put(String key, Integer value) {
		Log.d(Settings.TAG, ">>> put()");
		
		this.editor.putInt(key, value);
		this.editor.apply();
		
		// refresh map
		this.load();
		
		Log.d(Settings.TAG, "<<< put()");
	}
	
	public void put(String key, List<Integer> value) {
		Log.d(Settings.TAG, ">>> put()");
		
		// convert major into comma separated string
		String major = "";
		for(Integer m : value) {
			major += m + ",";
		}
		major.substring(0, major.length() - 1);
		
		this.editor.putString(key, major);
		this.editor.apply();
		
		// refresh map
		this.load();
		
		Log.d(Settings.TAG, "<<< put()");
	}
	
	private void load() {
		Log.d(Settings.TAG, ">>> load()");
		
		// load the minor interval
		Log.d(Settings.TAG, "loading minor settings");
		Integer minor = this.preferences.getInt(MINOR_SETTING, 30);
		Log.d(Settings.TAG, "saved minor value: " + minor);
		this.settings.put(MINOR_SETTING, minor);
		
		// load the major intervals list
		Log.d(Settings.TAG, "loading major settings");
		String majors = this.preferences.getString(MAJOR_SETTING, "1,5,7");
		Log.d(Settings.TAG, "saved major value: " + majors);
		
		// parse the major intervals
		String[] major = majors.split(",");
		List<Integer> majorVals = new ArrayList<Integer>();
		for(String m : major) {
			majorVals.add(Integer.parseInt(m));
		}
		
		this.settings.put(MAJOR_SETTING, majorVals);
		
		Log.d(Settings.TAG, "<<< load()");
	}
}
