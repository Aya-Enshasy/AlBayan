package com.ayaenshasy.bayan.base;


import static com.ayaenshasy.bayan.utils.AppPreferences.IS_PARENT;
import static com.ayaenshasy.bayan.utils.AppPreferences.PREF_NAME;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.SplashActivity;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppContextWrapper;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.google.firebase.FirebaseApp;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    public Dialog loader_dialog;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public AppPreferences preferences;
    Context context;
    public Role role;
    public String role_name;
    public User user;
    public boolean isParent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        preferences = AppPreferences.getInstance(this);
        context = this;
//        role = preferences.getUserRole();
//        role_name = preferences.getUserRole().name();
        user = preferences.getUserProfile();
        isParent = preferences.getBooleanPreference(IS_PARENT);

        loader_dialog = new Dialog(this);

        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sp.edit();


    }

    protected void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    protected void loaderDialog() {
        loader_dialog.setContentView(R.layout.loader_dialog);
        loader_dialog.setCancelable(false);
        loader_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        loader_dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        loader_dialog.show();
    }

    public void showNotification(String title, String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "M_CH_ID")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            String channelId = getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(message);
            channel.enableVibration(true);
            MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.notification);
            mp.start();
            channel.setSound(sound, audioAttributes);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

}
