package com.ayaenshasy.bayan.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Parent;
import com.ayaenshasy.bayan.model.user.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AppPreferences {

    private static AppPreferences instance;
    public static final String PREF_NAME = "AlBayanPreferences";
    private SharedPreferences prefs;
    private Gson gson = new GsonBuilder().serializeNulls().create();

    public static final String SELECTED_LANGUAGE = "language";
    public static final String USER_ROLE = "role";
    public static final String USER_DATA = "USER_DATA";
    public static final String PARENT_DATA = "PARENT_DATA";
    public static final String IS_PARENT = "IS_PARENT";
//    public static final String is_Login = "is_Login";
//    public static final String IS_FIRST_TIME = "IS_FIRST_TIME";

    public static synchronized AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void clearPreferences() {
        prefs.edit().putString(USER_DATA, "{}").apply();
    }

    public void setParentProfile(Parent parentProfile) {
        setBooleanPreference(IS_PARENT,true);
        prefs.edit().putString(PARENT_DATA, gson.toJson(parentProfile)).apply();
    }

    public Parent getParentProfile() {
        String userProfileJson = prefs.getString(PARENT_DATA, "{}");
        return gson.fromJson(userProfileJson, Parent.class);
    }

    public void setUserProfile(User userProfile) {
        setBooleanPreference(IS_PARENT,false);
        prefs.edit().putString(USER_DATA, gson.toJson(userProfile)).apply();
    }

    public User getUserProfile() {
        String userProfileJson = prefs.getString(USER_DATA, "{}");
        return gson.fromJson(userProfileJson, User.class);
    }

    public boolean setStringPreference(String key, String value) {
        return prefs.edit().putString(key, value).commit();
    }

    public String getStringPreference(String key) {
        return prefs.getString(key, "");
    }

    public boolean setUserRole(Role role) {
        String roleValue = role.name();
        return prefs.edit().putString(USER_ROLE, roleValue).commit();
    }

    public Role getUserRole() {
        return getUserProfile().getRole();
//        Role roleValue = getUserProfile().getRole();
//        if (!roleValue.isEmpty()) {
//            return Role.valueOf(roleValue);
//        } else {
//            return Role.ADMIN;
//        }
    }

    public boolean setIntegerPreference(String key, int value) {
        return prefs.edit().putInt(key, value).commit();
    }

    public int getIntegerPreference(String key) {
        return prefs.getInt(key, -1);
    }

    public boolean setBooleanPreference(String key, boolean value) {
        return prefs.edit().putBoolean(key, value).commit();
    }

    public boolean getBooleanPreference(String key) {
        return prefs.getBoolean(key, false);
    }

    public boolean setLongPreference(String key, double value) {
        return prefs.edit().putLong(key, (long) value).commit();
    }

    public Long getLongPreference(String key) {
        return prefs.getLong(key, -1);
    }
}
