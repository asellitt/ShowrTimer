package au.com.hacd.android.showrtimer.activity;

import java.util.List;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.com.hacd.android.showrtimer.R;
import au.com.hacd.android.showrtimer.R.id;
import au.com.hacd.android.showrtimer.R.layout;
import au.com.hacd.android.showrtimer.R.string;
import au.com.hacd.android.showrtimer.settings.Settings;
import au.com.hacd.android.showrtimer.timer.TimerThread;

public class ShowrTimerActivity extends Activity {
	private static final String TAG = "ShowrTimerActivity";

	private int seconds;
	private int minutes;

	private TimerThread timer;
	
	private Settings settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.timer = new TimerThread(this);

		this.settings = Settings.getInstance();
		
		this.reset();
		this.update();
	}

	public void startPauseClicked(View v) {
		Log.d(ShowrTimerActivity.TAG, ">>> startPauseClicked()");

		// if the timer has not started yet
		if (!this.timer.isAlive()) {
			// start it
			Log.d(ShowrTimerActivity.TAG, "startPauseClicked() : Timer not started. Starting now");
			this.timer = new TimerThread(this);
			this.timer.start();
		}
		// if the timer has started
		else {
			// stop it
			Log.d(ShowrTimerActivity.TAG, "startPauseClicked() : Timer started. Pausing now");
			this.timer.halt();
			synchronized (this.timer) {
				this.timer.notify();
			}
		}

		Log.d(ShowrTimerActivity.TAG, "<<< startPauseClicked()");
	}

	public void stopRestartClicked(View v) {
		Log.d(ShowrTimerActivity.TAG, ">>> stopRestartClicked()");
		
		// if the thread has started
		if(this.timer.isAlive()) {
			// stop the thread
			Log.d(ShowrTimerActivity.TAG, "stopRestartClicked() : Timer started. Stopping now");
			timer.halt();
			synchronized (this.timer) {				
				this.timer.notify();
			}
		}
		// if the thread has stopped
		else {
			Log.d(ShowrTimerActivity.TAG, "stopRestartClicked() : Timer stopped. Clearing display");
			// reset the display
			this.clear();
		}
		
		Log.d(ShowrTimerActivity.TAG, "<<< stopRestartClicked()");
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
			Log.d(ShowrTimerActivity.TAG, "major interval encountered");
			minutes.setTextColor(Color.CYAN);
			seconds.setTextColor(Color.CYAN);
		}
		// check if minor interval
		else if (
				((this.seconds + 60) % ((Integer) this.settings.get("minor")) == 0) &&
				!(this.seconds == 0 && this.minutes == 0)
		) {
			Log.d(ShowrTimerActivity.TAG, "minor interval encountered");
			minutes.setTextColor(Color.YELLOW);
			seconds.setTextColor(Color.YELLOW);
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