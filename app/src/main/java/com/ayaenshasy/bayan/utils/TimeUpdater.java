package com.ayaenshasy.bayan.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.broadcastReceiver.PrayerAlarmReceiver;
import com.ayaenshasy.bayan.model.PrayerTimingsClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUpdater extends BaseActivity {
    private Timer timer;
    private SimpleDateFormat timeFormat;
    private TextView textView, time_difference, pray_date;
    TextView fajer, dohor, aser, magrep, aisha;
    private PrayerTimingsClass prayerTimesHelper;
    private static final String KEY_FAJR = "fajr";
    private static final String KEY_DHUHR = "dhuhr";
    private static final String KEY_ASR = "asr";
    private static final String KEY_MAGHRIB = "maghrib";
    private static final String KEY_ISHA = "isha";
    public static final String KEY_PRAY_DATE = "pray_date";
    public static final String KEY_PRAY_TIME = "pray_time";
    Context context;

    public TimeUpdater(TextView textView, Context context) {
        timeFormat = new SimpleDateFormat("hh:mm a");
        timer = new Timer();
        prayerTimesHelper = new PrayerTimingsClass();
        fetchPrayerTime();
        this.textView = textView;
        this.context = context;
    }

    public TimeUpdater(TextView fajer, TextView dohor, TextView aser, TextView magrep, TextView aisha, TextView textView, TextView time_difference, TextView pray_date, Context context) {
        timeFormat = new SimpleDateFormat("hh:mm a");
        timer = new Timer();
        prayerTimesHelper = new PrayerTimingsClass();
        fetchPrayerTimes(fajer, dohor, aser, magrep, aisha);
        this.fajer = fajer;
        this.dohor = dohor;
        this.aser = aser;
        this.magrep = magrep;
        this.aisha = aisha;
        this.textView = textView;
        this.time_difference = time_difference;
        this.pray_date = pray_date;
        this.context = context;
    }

    public void startUpdatingTime() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                String currentTime = timeFormat.format(calendar.getTime());

                // Update the TextView with the current time
                new Handler(context.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                        String[] daysOfWeek = {"الاحد", "الاثنين", "الثلاثاء", "الاربعاء", "الخميس", "الجمعة", "السبت"};
                        textView.setText(daysOfWeek[dayOfWeek - 1] + " " + year + "-" + (month + 1) + "-" + dayOfMonth + "  -  " + currentTime);


                        prayerTimesHelper.getPrayerTimes("Palestine", new PrayerTimingsClass.PrayerTimesListener() {
                            @Override
                            public void onPrayerTimesReceived(PrayerTimingsClass.PrayerTimes prayerTimes) {

//                                checkNextPrayerTime(prayerTimes);

                                if (isPrayerTime(prayerTimes.getFajr())) {
                                    getAdanSound(prayerTimes.getDhuhr());
                                } else if (isPrayerTime(prayerTimes.getDhuhr())) {
                                    getAdanSound(prayerTimes.getAsr());
                                } else if (isPrayerTime(prayerTimes.getAsr())) {
                                    getAdanSound(prayerTimes.getMaghrib());
                                } else if (isPrayerTime(prayerTimes.getMaghrib())) {
                                    getAdanSound(prayerTimes.getIsha());
                                } else if (isPrayerTime(prayerTimes.getIsha())) {
                                    getAdanSound(prayerTimes.getFajr());
                                }


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

    private void getAdanSound(String prayerTimes ){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, PrayerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date nextPrayerTimeDate = null;
        try {
            nextPrayerTimeDate = sdf.parse(prayerTimes);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar nextPrayerTimeCalendar = Calendar.getInstance();
        nextPrayerTimeCalendar.setTime(nextPrayerTimeDate);
        long nextPrayerTimeMillis = nextPrayerTimeCalendar.getTimeInMillis();

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextPrayerTimeMillis, pendingIntent);
        }
    }

    public void stopUpdatingTime() {
        timer.cancel();
    }

    private void fetchPrayerTimes(TextView fajer, TextView dohor, TextView aser, TextView magrep, TextView aisha) {
        prayerTimesHelper.getPrayerTimes("Palestine", new PrayerTimingsClass.PrayerTimesListener() {
            @Override
            public void onPrayerTimesReceived(PrayerTimingsClass.PrayerTimes prayerTimes) {
                // Update the UI with the prayer times
                fajer.setText(prayerTimes.getFajr());
                dohor.setText(prayerTimes.getDhuhr());
                aser.setText(prayerTimes.getAsr());
                magrep.setText(prayerTimes.getMaghrib());
                aisha.setText(prayerTimes.getIsha());

                // Cache the prayer times
                cachePrayerTimes(prayerTimes);
                checkNextPrayerTime(prayerTimes);

            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle error
            }
        });
    }

    private void fetchPrayerTime() {
        prayerTimesHelper.getPrayerTimes("Palestine", new PrayerTimingsClass.PrayerTimesListener() {
            @Override
            public void onPrayerTimesReceived(PrayerTimingsClass.PrayerTimes prayerTimes) {
                // Update the UI with the prayer times


                // Cache the prayer times
                cachePrayerTimes(prayerTimes);
                checkNextPrayerTim(prayerTimes);

            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle error
            }
        });
    }

    private boolean isPrayerTime(String prayerTime) {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTimeString = sdf.format(currentTime.getTime());

        return currentTimeString.equals(prayerTime);
    }

    private void cachePrayerTimes(PrayerTimingsClass.PrayerTimes prayerTimes) {

        AppPreferences.getInstance(context).setStringPreference(KEY_FAJR, prayerTimes.getFajr());
        AppPreferences.getInstance(context).setStringPreference(KEY_DHUHR, prayerTimes.getDhuhr());
        AppPreferences.getInstance(context).setStringPreference(KEY_ASR, prayerTimes.getAsr());
        AppPreferences.getInstance(context).setStringPreference(KEY_MAGHRIB, prayerTimes.getMaghrib());
        AppPreferences.getInstance(context).setStringPreference(KEY_ISHA, prayerTimes.getIsha());


    }

    private void checkNextPrayerTime(PrayerTimingsClass.PrayerTimes prayerTimes) {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTimeString = sdf.format(currentTime.getTime());

        String nextPrayerName = "";
        String nextPrayerTime = "";

        if (currentTimeString.compareTo(prayerTimes.getFajr()) < 0) {
            nextPrayerName = "صلاة الفجر";
            nextPrayerTime = prayerTimes.getFajr();
        } else if (currentTimeString.compareTo(prayerTimes.getDhuhr()) < 0) {
            nextPrayerName = "صلاة الظهر";
            nextPrayerTime = prayerTimes.getDhuhr();
        } else if (currentTimeString.compareTo(prayerTimes.getAsr()) < 0) {
            nextPrayerName = "صلاة العصر";
            nextPrayerTime = prayerTimes.getAsr();
        } else if (currentTimeString.compareTo(prayerTimes.getMaghrib()) < 0) {
            nextPrayerName = "صلاة المغرب";
            nextPrayerTime = prayerTimes.getMaghrib();
        } else if (currentTimeString.compareTo(prayerTimes.getIsha()) < 0) {
            nextPrayerName = "صلاة العشاء";
            nextPrayerTime = prayerTimes.getIsha();
        }

        if (!nextPrayerName.isEmpty() && !nextPrayerTime.isEmpty()) {
            Log.d("NextPrayer", "Next prayer: " + nextPrayerName + " at " + nextPrayerTime);
            pray_date.setText("الوقت المتبقي ل" + nextPrayerName);
            AppPreferences.getInstance(context).setStringPreference(KEY_PRAY_DATE, "الوقت المتبقي ل" + nextPrayerName);

            try {

                Date currentTime2 = sdf.parse(currentTimeString);
                Date nextPrayer1 = sdf.parse(nextPrayerTime);

                long timeDifferenceMillis = nextPrayer1.getTime() - currentTime2.getTime();
                long minutes1 = (timeDifferenceMillis / (1000 * 60)) % 60;
                long hours = (timeDifferenceMillis / (1000 * 60 * 60)) % 24;
                Log.e("NextPrayer", "Time difference: " + minutes1 + " دقائق " + hours + " ساعة");
                AppPreferences.getInstance(context).setStringPreference(KEY_PRAY_TIME, minutes1 + " دقيقة " + "  " + hours + " ساعة");

                time_difference.setText(minutes1 + " دقيقة " + "  " + hours + " ساعة");
                // You can display or use the time difference as needed
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkNextPrayerTim(PrayerTimingsClass.PrayerTimes prayerTimes) {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTimeString = sdf.format(currentTime.getTime());

        String nextPrayerName = "";
        String nextPrayerTime = "";

        if (currentTimeString.compareTo(prayerTimes.getFajr()) < 0) {
            nextPrayerName = "صلاة الفجر";
            nextPrayerTime = prayerTimes.getFajr();
        } else if (currentTimeString.compareTo(prayerTimes.getDhuhr()) < 0) {
            nextPrayerName = "صلاة الظهر";
            nextPrayerTime = prayerTimes.getDhuhr();
        } else if (currentTimeString.compareTo(prayerTimes.getAsr()) < 0) {
            nextPrayerName = "صلاة العصر";
            nextPrayerTime = prayerTimes.getAsr();
        } else if (currentTimeString.compareTo(prayerTimes.getMaghrib()) < 0) {
            nextPrayerName = "صلاة المغرب";
            nextPrayerTime = prayerTimes.getMaghrib();
        } else if (currentTimeString.compareTo(prayerTimes.getIsha()) < 0) {
            nextPrayerName = "صلاة العشاء";
            nextPrayerTime = prayerTimes.getIsha();
        }

        if (!nextPrayerName.isEmpty() && !nextPrayerTime.isEmpty()) {
            Log.d("NextPrayer", "Next prayer: " + nextPrayerName + " at " + nextPrayerTime);
            AppPreferences.getInstance(context).setStringPreference(KEY_PRAY_DATE, "الوقت المتبقي ل" + nextPrayerName);

            try {

                Date currentTime2 = sdf.parse(currentTimeString);
                Date nextPrayer1 = sdf.parse(nextPrayerTime);

                long timeDifferenceMillis = nextPrayer1.getTime() - currentTime2.getTime();
                long minutes1 = (timeDifferenceMillis / (1000 * 60)) % 60;
                long hours = (timeDifferenceMillis / (1000 * 60 * 60)) % 24;
                Log.e("NextPrayer", "Time difference: " + minutes1 + " دقائق " + hours + " ساعة");
                AppPreferences.getInstance(context).setStringPreference(KEY_PRAY_TIME, minutes1 + " دقيقة " + "  " + hours + " ساعة");

                // You can display or use the time difference as needed
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}

