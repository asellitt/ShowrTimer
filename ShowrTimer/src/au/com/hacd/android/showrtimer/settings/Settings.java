package au.com.hacd.android.showrtimer.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {

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
		if(Settings.instance == null) {
			Settings.instance = new Settings(context);
			instance.load();
		}
		
		return Settings.instance;
	}
	
	public Object get(String key) {
		return this.settings.get(key);
	}
	
	private void load() {
		// load the minor interval
		this.settings.put("minor", this.preferences.getInt("minor", 30));
		
		// load the major intervals list
		String majorKey = "major";
		List<Integer> majorVals = new ArrayList<Integer>();
		majorVals.add(this.preferences.getInt(majorKey + "_0", 1));
		majorVals.add(this.preferences.getInt(majorKey + "_1", 5));
		majorVals.add(this.preferences.getInt(majorKey + "_2", 7));
		
		int index = 3;
		while(this.preferences.getInt(majorKey + "_" + index, 0) != 0) {
			majorVals.add(this.preferences.getInt(majorKey + "_" + index, 0));
		}
		
		this.settings.put(majorKey, majorVals);
	}
}
