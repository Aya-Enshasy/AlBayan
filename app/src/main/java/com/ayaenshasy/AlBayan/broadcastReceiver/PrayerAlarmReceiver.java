package com.ayaenshasy.AlBayan.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.ayaenshasy.AlBayan.R;

import android.widget.Toast;

public class PrayerAlarmReceiver extends BroadcastReceiver {


         private MediaPlayer mediaPlayer;

        @Override
        public void onReceive(Context context, Intent intent) {
            // Play the sound when the alarm is triggered
//            playAdanSound(context);
        }

//        private void playAdanSound(Context context) {
//            // Initialize and play the sound here
//            mediaPlayer = MediaPlayer.create(context, R.raw.pray);
//            mediaPlayer.start();
//
//            // Show a toast or perform any other action
//            Toast.makeText(context, "Prayer time! Adan sound is playing.", Toast.LENGTH_SHORT).show();
//
//            // Release the media player when sound playback is complete
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    mediaPlayer.release();
//                    mediaPlayer = null;
//                }
//            });
//        }
    }


