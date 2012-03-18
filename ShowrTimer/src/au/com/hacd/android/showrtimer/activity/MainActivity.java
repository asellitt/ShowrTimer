package au.com.hacd.android.showrtimer.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import au.com.hacd.android.showrtimer.R;
import au.com.hacd.android.showrtimer.settings.Settings;
import au.com.hacd.android.showrtimer.sound.SoundPlayer;
import au.com.hacd.android.showrtimer.timer.TimerThread;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private int seconds;
	private int minutes;

	private TimerThread timer;
	
	private SoundPlayer major;
	private SoundPlayer minor;
	
	private Settings settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

		// ensure the screen stays on
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		this.timer = new TimerThread(this);
		
		this.major = new SoundPlayer(this.getApplicationContext(), R.raw.major);
		this.minor = new SoundPlayer(this.getApplicationContext(), R.raw.minor);

		this.settings = Settings.getInstance(this.getApplicationContext());
		
		this.reset();
		this.update();
	}
	
	@Override
	public void onDestroy() {
		// stop the timer thread
		this.timer.stop();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(MainActivity.TAG, ">>> onPrepareOptionsMenu()");
		
		// update menu items
		menu.setGroupVisible(R.id.mainGroup, true);
		menu.setGroupVisible(R.id.settingsGroup, false);
		
		Log.d(MainActivity.TAG, "<<< onPrepareOptionsMenu()");
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(MainActivity.TAG, ">>> onCreateOptionsMenu()");
		
		// inflate menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		
		Log.d(MainActivity.TAG, "<<< onCreateOptionsMenu()");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if this is the settings menu item
		if(item.getItemId() == R.id.settingsItem) {
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		}
		
		return false;
	}

	public void startPauseClicked(View v) {
		Log.d(MainActivity.TAG, ">>> startPauseClicked()");

		// if the timer has not started yet
		if (!this.timer.isAlive()) {
			// start it
			Log.d(MainActivity.TAG, "startPauseClicked() : Timer not started. Starting now");
			this.timer = new TimerThread(this);
			this.timer.start();
		}
		// if the timer has started
		else {
			// stop it
			Log.d(MainActivity.TAG, "startPauseClicked() : Timer started. Pausing now");
			this.timer.halt();
			synchronized (this.timer) {
				this.timer.notify();
			}
		}

		Log.d(MainActivity.TAG, "<<< startPauseClicked()");
	}

	public void stopRestartClicked(View v) {
		Log.d(MainActivity.TAG, ">>> stopRestartClicked()");
		
		// if the thread has started
		if(this.timer.isAlive()) {
			// stop the thread
			Log.d(MainActivity.TAG, "stopRestartClicked() : Timer started. Stopping now");
			timer.halt();
			synchronized (this.timer) {				
				this.timer.notify();
			}
		}
		// if the thread has stopped
		else {
			Log.d(MainActivity.TAG, "stopRestartClicked() : Timer stopped. Clearing display");
			// reset the display
			this.clear();
		}
		
		Log.d(MainActivity.TAG, "<<< stopRestartClicked()");
	}

	/**
	 * Increments the timer
	 */
	public void increment() {
		if (this.seconds == 59) {
			this.seconds = 0;
			this.minutes++;
		} 
		else {
			this.seconds++;
		}
	}

	/**
	 * Updates the display
	 */
	@SuppressWarnings("unchecked")
	public void update() {
		// set time
		TextView minutes = (TextView) this.findViewById(R.id.minutesView);
		TextView seconds = (TextView) this.findViewById(R.id.secondsView);
		
		String minStr = String.format("%02d", this.minutes);
		String secStr = String.format("%02d", this.seconds);
		
		minutes.setText(minStr);
		seconds.setText(secStr);
		
		// set actions
		Button startPause = (Button) this.findViewById(R.id.startPauseButton);
		Button stopReset = (Button) this.findViewById(R.id.stopResetButton);
		
		if(this.timer.isAlive()) {
			startPause.setText(R.string.stop);
			stopReset.setText(R.string.stop);
		}
		else {
			startPause.setText(R.string.start);
			stopReset.setText(R.string.clear);
		}
		
		// check if major interval
		if(
				((List<Integer>) this.settings.get("major")).contains(this.minutes) &&
				this.seconds == 0
		) {
			Log.d(MainActivity.TAG, "major interval encountered");
			minutes.setTextColor(Color.CYAN);
			seconds.setTextColor(Color.CYAN);
			this.major.start();
		}
		// check if minor interval
		else if (
				((this.seconds + 60) % ((Integer) this.settings.get("minor")) == 0) &&
				!(this.seconds == 0 && this.minutes == 0)
		) {
			Log.d(MainActivity.TAG, "minor interval encountered");
			minutes.setTextColor(Color.YELLOW);
			seconds.setTextColor(Color.YELLOW);
			this.minor.start();
		}
		// no interval
		else {
			minutes.setTextColor(Color.WHITE);
			seconds.setTextColor(Color.WHITE);
		}
	}
	
	/**
	 * Resets the timer
	 */
	private void reset() {
		this.seconds = 0;
		this.minutes = 0;
	}
	
	/**
	 * Resets the display
	 */
	private void clear() {
		this.reset();
		this.update();
	}
}