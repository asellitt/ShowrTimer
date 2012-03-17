package au.com.hacd.android.showrtimer.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Settings {

	private static Settings instance;
	private Map<String, Object> settings;	
	
	private Settings() {
		Settings.instance = this;
		this.settings = new TreeMap<String, Object>();
	}
	
	public static Settings getInstance() {
		if(Settings.instance == null) {
			Settings.instance = new Settings();
			instance.load();
		}
		
		return Settings.instance;
	}
	
	public Object get(String key) {
		return this.settings.get(key);
	}
	
	private void load() {
		String minorKey = "minor";
		Integer minorVal = 10;
		this.settings.put(minorKey, minorVal);
		
		String majorKey = "major";
		List<Integer> majorVals = new ArrayList<Integer>();
		majorVals.add(1);
		majorVals.add(5);
		majorVals.add(7);
		this.settings.put(majorKey, majorVals);
	}
}
