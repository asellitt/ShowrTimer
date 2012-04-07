package au.com.hacd.android.showrtimer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import au.com.hacd.android.showrtimer.R;
import au.com.hacd.android.showrtimer.settings.Settings;

public class SettingsActivity extends ListActivity {
	private static final String TAG = "SettingsActivity";

	private Settings settings;
	
	private ArrayAdapter<String> majorAdaptor;
	private List<String> majorItems;
	private List<Integer> majorInts;
	
	private String[] minorArray;
	
	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		Log.d(SettingsActivity.TAG, ">>> onCreate()");
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings);
		
		this.settings = Settings.getInstance(this.getApplicationContext());
		
		// prepopulate ui
		Spinner minorSpinner = (Spinner) this.findViewById(R.id.minorSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, 
	            R.array.minor_array, 
	            android.R.layout.simple_spinner_item
        );
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    minorSpinner.setAdapter(adapter);
	    minorSpinner.setOnItemSelectedListener(new OnMinorSelectedListener());
	    
	    // set the spinner in the correct position
	    minorArray = this.getResources().getStringArray(R.array.minor_array);
	    Log.d(SettingsActivity.TAG, "presetting spinner index");
	    for(int i = 0; i < minorArray.length; i++) {
	    	Log.d(
	    			SettingsActivity.TAG, 
	    			"checking: array=" + minorArray[i] + ", setting=" + this.settings.get(Settings.MINOR_SETTING)
			);
	    	
	    	if(minorArray[i].equalsIgnoreCase("" + this.settings.get(Settings.MINOR_SETTING))) {
	    		minorSpinner.setSelection(i);
	    		break;
	    	}
	    }

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
	    this.registerForContextMenu(this.getListView());
	    
		Log.d(SettingsActivity.TAG, "<<< onCreate()");
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		Log.d(SettingsActivity.TAG, ">>> onCreateContextMenu()");
		
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		
		Log.d(SettingsActivity.TAG, "<<< onCreateContextMenu()");
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		Log.d(SettingsActivity.TAG, ">>> onContextItemSelected()");
		
		// remove the item from the list
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		majorAdaptor.remove(majorAdaptor.getItem(info.position));
		
		Log.d(SettingsActivity.TAG, "<<< onContextItemSelected()");
		
		return true;
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
	
	private class OnMinorSelectedListener implements OnItemSelectedListener {

		private static final String TAG = "OnMinorSelectedListener";
		
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			Log.d(OnMinorSelectedListener.TAG, ">>> onItemSelected()");
			
			String val = parent.getItemAtPosition(pos).toString();
			Log.d(OnMinorSelectedListener.TAG, "index:" + pos + ", value:" + val);
			
			settings.put(Settings.MINOR_SETTING, Integer.parseInt(val));
			
			Log.d(OnMinorSelectedListener.TAG, "<<< onItemSelected()");
		}

		public void onNothingSelected(AdapterView parent) { }
	}
}