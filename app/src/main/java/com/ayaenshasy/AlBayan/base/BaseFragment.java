package com.ayaenshasy.AlBayan.base;

import static android.content.Context.MODE_PRIVATE;

import static com.ayaenshasy.AlBayan.utils.AppPreferences.PREF_NAME;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.api.ApiInterface;
import com.ayaenshasy.AlBayan.api.Creator;
import com.ayaenshasy.AlBayan.utils.AppPreferences;


public abstract class BaseFragment extends Fragment {

    public Dialog loader_dialog ;
    public ApiInterface api;
       public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
      AppPreferences preferences;
    Context context = getActivity();
     @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader_dialog = new Dialog(getActivity());

         sp = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sp.edit();
         api =  Creator.getRetrofitInstance();

        preferences = AppPreferences.getInstance(getActivity());


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    protected void loaderDialog() {
        loader_dialog.setContentView(R.layout.loader_dialog);
        loader_dialog.setCancelable(false);
        loader_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        loader_dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        loader_dialog.show();
    }
    public void showPrayerNotification(String title, String message) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager)getActivity(). getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity(), "M_CH_ID")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set high priority for heads-up style
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        // Set full-screen intent to true for heads-up style
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            String channelId = getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(message);
            channel.enableVibration(true);
            channel.setSound(sound, audioAttributes);
            MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.notification);
            mp.start();
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);

            // Set full-screen intent for heads-up style
            notificationBuilder.setFullScreenIntent(pendingIntent, true);

            // Set activity's window flags to enable pop-up behavior
            Window window = getActivity().getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

 }
