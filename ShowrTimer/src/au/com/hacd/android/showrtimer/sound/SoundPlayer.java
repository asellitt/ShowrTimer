package au.com.hacd.android.showrtimer.sound;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class SoundPlayer {
	private static final String TAG = "SoundPlayer";
	
	private Context context;
	private int track;
	
	private MediaPlayer player; 
	
	public SoundPlayer(Context context, int track) {		
		this.context = context;
		this.track = track;
	}
	
	public void start() {
		Log.d(SoundPlayer.TAG, ">>> start()");
		
		this.player = MediaPlayer.create(this.context, this.track);
		
		this.player.setOnCompletionListener(new OnCompletionListener() {
			
			public void onCompletion(MediaPlayer mp) {
				player.release();
			}
		});
		
		this.player.start();
		
		Log.d(SoundPlayer.TAG, "<<< start()");
	}

}
