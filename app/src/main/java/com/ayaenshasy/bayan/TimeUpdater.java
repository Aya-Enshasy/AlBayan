package com.ayaenshasy.bayan;
import android.os.Handler;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUpdater {
    private Timer timer;
    private SimpleDateFormat timeFormat;
    private TextView textView;

    public TimeUpdater(TextView textView) {
        timeFormat = new SimpleDateFormat("hh:mm a");
        timer = new Timer();
        this.textView = textView;
    }

    public void startUpdatingTime() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                String currentTime = timeFormat.format(calendar.getTime());

                // Update the TextView with the current time
                new Handler(textView.getContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                        System.out.println("Current date: " + year + "-" + (month + 1) + "-" + dayOfMonth);

                        String[] daysOfWeek = {"الاحد", "الاثنين", "الثلاثاء", "الاربعاء", "الخميس", "الجمعة", "السبت"};
                        System.out.println("Day of the week: " + daysOfWeek[dayOfWeek - 1]);
                        textView.setText(daysOfWeek[dayOfWeek - 1]+" "+year + "-" + (month + 1) + "-" + dayOfMonth+"  -  "+currentTime);
                    }
                });
            }
        };

        // Schedule the task to run every second (1000 milliseconds)
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stopUpdatingTime() {
        timer.cancel();
    }
}

