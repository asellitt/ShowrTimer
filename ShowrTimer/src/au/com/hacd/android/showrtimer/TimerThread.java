package au.com.hacd.android.showrtimer;

import android.util.Log;

public class TimerThread extends Thread implements Runnable {
	private static final String TAG = "TimerRunner";
	
	private ShowrTimerActivity activity;
	private volatile boolean stop;
	
	public TimerThread(ShowrTimerActivity activity) {
		this.activity = activity;
	}
	
	public void run() {
		Log.d(TimerThread.TAG, ">>> run()");

		try {
			// while the thread is supposed to run
			while (!stop) {
				// wait one second
				synchronized (this) {
					this.wait(1000);					
				}
				if(!stop) {
					activity.increment();
					
					// update UI on UI thread
					activity.runOnUiThread(new Runnable() {
						
						public void run() {
							activity.update();
						}
					});
				}
			}
		}
		catch (InterruptedException ie) {
			// if the timer is interrupted, its probably because its time to end it
			Log.e(TimerThread.TAG, "run() interrupted whilst sleeping");
		}
		
		stop = false;
		Log.d(TimerThread.TAG, "<<< run()");
	}

	public void halt() {
		this.stop = true;
	}
}
