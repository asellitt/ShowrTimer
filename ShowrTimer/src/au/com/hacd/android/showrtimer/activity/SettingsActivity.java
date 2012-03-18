package au.com.hacd.android.showrtimer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import au.com.hacd.android.showrtimer.R;

public class SettingsActivity extends Activity {
	private static final String TAG = "SettingsActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(SettingsActivity.TAG, ">>> onPrepareOptionsMenu()");
		
		// update menu items
		menu.setGroupVisible(R.id.settingsGroup, true);
		menu.setGroupVisible(R.id.mainGroup, false);
		
		Log.d(SettingsActivity.TAG, "<<< onPrepareOptionsMenu()");
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(SettingsActivity.TAG, ">>> onCreateOptionsMenu()");
		
		// inflate menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		
		Log.d(SettingsActivity.TAG, "<<< onCreateOptionsMenu()");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if this is the main menu item
		if(item.getItemId() == R.id.mainItem) {
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity(intent);
			return true;
		}
		
		return false;
	}
}