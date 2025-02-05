package com.smritiraksha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class EmergencyReceiver extends BroadcastReceiver {

    private static final String TAG = "EmergencyReceiver";
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEmergency = intent.getBooleanExtra("isEmergency", false);

        if (isEmergency) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.sos_sound);
                mediaPlayer.setLooping(true);
            }

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                Log.d(TAG, "Emergency alarm started.");
            }
        } else {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "Emergency alarm stopped.");
            }
        }
    }
}
