package com.ayaenshasy.AlBayan;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;
import com.ayaenshasy.AlBayan.broadcastReceiver.PrayerAlarmReceiver;
import com.ayaenshasy.AlBayan.model.PrayerTimingsClass;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PrayerService extends Service {
    private static final String CHANNEL_ID = "PrayerServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private PrayerAlarmReceiver alarmReceiver;
    private Timer timer;
    private SimpleDateFormat timeFormat;
    private PrayerTimingsClass prayerTimesHelper= new PrayerTimingsClass();;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Register the PrayerAlarmReceiver
        registerPrayerAlarmReceiver();

        // Schedule the alarms for prayer times
        schedulePrayerAlarms();

        // Start the service in the foreground
//        startForeground(NOTIFICATION_ID, createNotification());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel any scheduled alarms or cleanup tasks here
        cancelPrayerAlarms();

        // Unregister the PrayerAlarmReceiver
        unregisterPrayerAlarmReceiver();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerPrayerAlarmReceiver() {
        alarmReceiver = new PrayerAlarmReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.PRAYER_ALARM_ACTION");
        registerReceiver(alarmReceiver, filter);
    }

    private void unregisterPrayerAlarmReceiver() {
        if (alarmReceiver != null) {
            unregisterReceiver(alarmReceiver);
            alarmReceiver = null;
        }
    }

    private boolean isPrayerTime(String prayerTime) {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTimeString = sdf.format(currentTime.getTime());

        return currentTimeString.equals(prayerTime);
    }

    private void schedulePrayerAlarms() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
//                String currentTime = timeFormat.format(calendar.getTime());

                // Update the TextView with the current time
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();

                        prayerTimesHelper.getPrayerTimes("Palestine", new PrayerTimingsClass.PrayerTimesListener() {
                            @Override
                            public void onPrayerTimesReceived(PrayerTimingsClass.PrayerTimes prayerTimes) {
//                                if (isPrayerTime(prayerTimes.getFajr())) {
//                                    getAdanSound(prayerTimes.getDhuhr());
//                                } else if (isPrayerTime(prayerTimes.getDhuhr())) {
//                                    getAdanSound(prayerTimes.getAsr());
//                                } else if (isPrayerTime(prayerTimes.getAsr())) {
//                                    getAdanSound(prayerTimes.getMaghrib());
//                                } else if (isPrayerTime(prayerTimes.getMaghrib())) {
//                                    getAdanSound(prayerTimes.getIsha());
//                                } else if (isPrayerTime(prayerTimes.getIsha())) {
//                                    getAdanSound(prayerTimes.getFajr());
//                                }
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // Handle error
                            }
                        });
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void cancelPrayerAlarms() {
        // Cancel any scheduled alarms here
        // You can use the `alarmManager` and `pendingIntent` to cancel the alarms
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void getAdanSound(String prayerTime) {
        // Modify this method to schedule an alarm for the specified prayer time
        // You can use the code from your existing `getAdanSound()` method

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, PrayerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date prayerTimeDate;
        try {
            prayerTimeDate = sdf.parse(prayerTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        Calendar prayerTimeCalendar = Calendar.getInstance();
        prayerTimeCalendar.setTime(prayerTimeDate);
        long prayerTimeMillis = prayerTimeCalendar.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use setExactAndAllowWhileIdle for API level 23 and above
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prayerTimeMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, prayerTimeMillis, pendingIntent);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Prayer Service Channel";
            String description = "Channel for Prayer Service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(getApplicationContext(),SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Prayer Service")
                .setContentText("Running in the background")
                .setSmallIcon(R.drawable.parbel)
                .setContentIntent(pendingIntent);

        return builder.build();
    }
}
