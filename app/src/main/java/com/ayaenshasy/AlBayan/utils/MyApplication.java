package com.ayaenshasy.AlBayan.utils;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

public class MyApplication extends Application {
    public static final String TAG = MyApplication.class.getSimpleName();
    static MyApplication mAppInstance;


    public static Context getContext() {
        return getInstance().getApplicationContext();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;
        AppPreferences preferences = AppPreferences.getInstance(this);
        String language = preferences.getStringPreference(AppPreferences.SELECTED_LANGUAGE);
        if (TextUtils.isEmpty(language)) {
            language = "ar";
            preferences.setStringPreference(AppPreferences.SELECTED_LANGUAGE, language);
        }
    }

    public static synchronized MyApplication getInstance() {
        return mAppInstance;
    }

}
