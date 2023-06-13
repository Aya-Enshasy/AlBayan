package com.ayaenshasy.bayan.utils;

import android.content.Intent;

import com.ayaenshasy.bayan.ui.activities.LoginActivity;

public class Constant {
    public static String LOGIN = "LOGIN";
    public static String Remembrance_Id = "Remembrance_Id";
    public static String Remembrance_Name = "Remembrance_Name";
    public static String Remembrance_List = "Remembrance_List";
    public static String AYA_ID = "AYA_ID";
    public static String USER_ID = "USER_ID";
    public static String USER_NAME = "USER_NAME";
    public static String USER_IMAGE = "USER_IMAGE";
    public static String password = "password";
    public static int REQUEST_IMAGE_GALLERY = 1;
    public static int REQUEST_IMAGE_PERMISSION = 2;


//    public void reLogin(AppPreferences preferenceHelper) {
//        preferenceHelper.clearUserData();
//        finishAffinity();
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }


}
