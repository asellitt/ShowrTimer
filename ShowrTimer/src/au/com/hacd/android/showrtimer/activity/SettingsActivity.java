package au.com.hacd.android.showrtimer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import au.com.hacd.android.showrtimer.R;
import au.com.hacd.android.showrtimer.settings.Settings;

public class SettingsActivity extends ListActivity {
	private static final String TAG = "SettingsActivity";

	private Settings settings;
	
	private ArrayAdapter<String> majorAdaptor;
	private List<String> majorItems;
	private List<Integer> majorInts;
	
	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SettingsActivity.TAG, ">>> onCreate()");
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings);
		
		this.settings = Settings.getInstance(this.getApplicationContext());
		
		// prepopulate ui
		EditText minorEditText = (EditText) this.findViewById(R.id.minorText);
		minorEditText.setText(this.settings.get("minor").toString());
		
		// convert major Integers into display strings
		this.majorInts = ((List<Integer>) this.settings.get("major"));
		this.majorItems = new ArrayList<String>();
		for(Integer major : this.majorInts) {
			this.majorItems.add(major.toString() + " minute(s)");
		}
		
		// bind major display strings to list view
		this.majorAdaptor = new ArrayAdapter<String>(
				this.getApplicationContext(), 
				android.R.layout.simple_list_item_1,
				this.majorItems
		);
		this.setListAdapter(majorAdaptor);
		
		Log.d(SettingsActivity.TAG, "<<< onCreate()");
	}
	
	public void addMajorClicked(View v) {
		Log.d(SettingsActivity.TAG, ">>> addMajorClicked()");
		
		EditText major = (EditText) this.findViewById(R.id.majorText);
		
		// validate input
		String majorInput = major.getText().toString();
		if(majorInput.matches("[0-9]*")) {
			Log.d(SettingsActivity.TAG, "major interval valid");
			
			Integer i = new Integer(majorInput);
			// if not duplicate
			if(!this.majorInts.contains(i)) {
				Log.d(SettingsActivity.TAG, "major interval not duplicate, adding");
				this.majorInts.add(i);
				Collections.sort(this.majorInts);
				this.settings.put("major", this.majorInts);
				
				this.majorItems.add(i + " minute(s)");
				Collections.sort(this.majorItems);
				this.majorAdaptor.notifyDataSetChanged();
			}
			else {
				Log.d(SettingsActivity.TAG, "major interval duplicate, discarding");
			}
		}
		else {
			Log.e(SettingsActivity.TAG, "major interval invalid, discarding");
		}
		
		// clear text
		major.setText("");
		
		Log.d(SettingsActivity.TAG, ">>> addMajorClicked()");
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
		Log.d(SettingsActivity.TAG, ">>> onOptionsItemSelected()");
		
		boolean success = false;
		
		// if this is the main menu item
		if(item.getItemId() == R.id.mainItem) {
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity(intent);
			success = true;
		}
		
		Log.d(SettingsActivity.TAG, "<<< onOptionsItemSelected()");
		
		return success;
	}
}