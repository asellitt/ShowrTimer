package au.com.hacd.android.showrtimer.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
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

	private float brightness;
	
	private int seconds;
	private int minutes;

	private TimerThread timer;
	
	private SoundPlayer major;
	private SoundPlayer minor;
	
	private Settings settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(MainActivity.TAG, ">>> onCreate()");
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

		// ensure the screen stays on
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// save brightness
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		this.brightness = lp.screenBrightness;
		
		// set brightness
		lp.screenBrightness = 1;
		this.getWindow().setAttributes(lp);
		
		// set max volume
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	    am.setStreamVolume(
	    		AudioManager.STREAM_MUSIC, 
	    		am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 
	    		0
		);
		
		this.timer = new TimerThread(this);
		
		this.major = new SoundPlayer(this.getApplicationContext(), R.raw.major);
		this.minor = new SoundPlayer(this.getApplicationContext(), R.raw.minor);

		this.settings = Settings.getInstance(this.getApplicationContext());
		
		this.reset();
		this.update();
		
		Log.d(MainActivity.TAG, "<<< onCreate()");
	}
	
	@Override
	public void onDestroy() {
		Log.d(MainActivity.TAG, ">>> onDestroy()");
		super.onDestroy();
		
		// stop the timer thread
		this.timer.stop();
		
		// restore brightness
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.screenBrightness = this.brightness;
		this.getWindow().setAttributes(lp);
		
		Log.d(MainActivity.TAG, "<<< onDestroy()");
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
		Log.d(MainActivity.TAG, ">>> onOptionsItemSelected()");
		
		boolean success = false;
		// if this is the settings menu item
		if(item.getItemId() == R.id.settingsItem) {
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			success = true;
		}
		
		Log.d(MainActivity.TAG, "<<< onOptionsItemSelected()");
		
		return success;
	}

	public void startPauseClicked(View v) {
		Log.d(MainActivity.TAG, ">>> startPauseClicked()");

		// if the timer has not started yet
		if (!this.timer.isAlive()) {
			// start it
			Log.d(MainActivity.TAG, "startPauseClicked() : Timer not started. Starting now");
			this.timer = new TimerThread(this);
			this.timer.start();
			
			// update button text
			updateButtonText(R.id.startPauseButton, R.string.pause);
			updateButtonText(R.id.stopResetButton, R.string.stop);
		}
		// if the timer has started
		else {
			// stop it
			Log.d(MainActivity.TAG, "startPauseClicked() : Timer started. Pausing now");
			this.timer.halt();
			synchronized (this.timer) {
				this.timer.notify();
			}
			
			// update button text
			updateButtonText(R.id.startPauseButton, R.string.start);
			updateButtonText(R.id.stopResetButton, R.string.clear);
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
		
		// reset the display
		this.clear();
		
		// update button text
		updateButtonText(R.id.startPauseButton, R.string.start);
		updateButtonText(R.id.stopResetButton, R.string.clear);
		
		Log.d(MainActivity.TAG, "<<< stopRestartClicked()");
	}

	/**
	 * Increments the timer
	 */
	public void increment() {
		Log.d(MainActivity.TAG, ">>> increment()");
		
		if (this.seconds == 59) {
			this.seconds = 0;
			this.minutes++;
		} 
		else {
			this.seconds++;
		}
		
		Log.d(MainActivity.TAG, "<<< increment()");
	}

	/**
	 * Updates the display
	 */
	@SuppressWarnings("unchecked")
	public void update() {
		Log.d(MainActivity.TAG, ">>> update()");
		
		// set time
		TextView minutes = (TextView) this.findViewById(R.id.minutesView);
		TextView seconds = (TextView) this.findViewById(R.id.secondsView);
		
		String minStr = String.format("%02d", this.minutes);
		String secStr = String.format("%02d", this.seconds);
		
		minutes.setText(minStr);
		seconds.setText(secStr);
		
		// check if major interval
		if(
				((List<Integer>) this.settings.get("major")).contains(this.minutes) &&
				this.seconds == 0
		) {
			Log.d(MainActivity.TAG, "major interval encountered");
			setTextColor(Color.RED, R.id.minutesView, R.id.secondsView, R.id.separatorView);
			this.major.start();
		}
		// check if minor interval
		else if (
				((this.seconds + 60) % ((Integer) this.settings.get("minor")) == 0) &&
				!(this.seconds == 0 && this.minutes == 0)
		) {
			Log.d(MainActivity.TAG, "minor interval encountered");
			setTextColor(Color.YELLOW, R.id.minutesView, R.id.secondsView, R.id.separatorView);
			this.minor.start();
		}
		// no interval
		else {
			setTextColor(Color.WHITE, R.id.minutesView, R.id.secondsView, R.id.separatorView);
		}
		
		Log.d(MainActivity.TAG, "<<< update()");
	}
	
	/**
	 * Resets the timer
	 */
	private void reset() {
		Log.d(MainActivity.TAG, ">>> reset()");
		
		this.seconds = 0;
		this.minutes = 0;
		
		Log.d(MainActivity.TAG, "<<< reset()");
	}
	
	/**
	 * Resets the display
	 */
	private void clear() {
		Log.d(MainActivity.TAG, ">>> clear()");
		
		this.reset();
		this.update();
		
		Log.d(MainActivity.TAG, "<<< clear()");
	}
	
	private void updateButtonText(int buttonId, int stringId) {
		Button btn = (Button) this.findViewById(buttonId);
		btn.setText(stringId);
	}
	
	private void setTextColor(int color, int ... textIds) {
		for(int textId : textIds) {
			TextView txt = (TextView) this.findViewById(textId);
			txt.setTextColor(color);
			txt.setShadowLayer(10, 1, 1, color);
		}
	}
}