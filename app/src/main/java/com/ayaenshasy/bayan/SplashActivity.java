package com.ayaenshasy.bayan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivitySplashBinding;
import com.ayaenshasy.bayan.ui.activities.BottomNavigationBarActivity;
import com.ayaenshasy.bayan.ui.activities.LoginActivity;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    ActivitySplashBinding binding;
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        runActivity();
        Log.e("onNewToken", preferences.readString(AppPreferences.DEVICE_TOKEN));

//        getPassword();
//        getUserData();
    }

    private void runActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
          if (preferences.getUserProfile().getRole() == null) {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                } else {
              preferences.getUserProfile();
              startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
                    finish();
                }


            }
        }, 1000);
    }

//    private void getUserData(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        String user_id = AppPreferences.getInstance(getBaseContext()).getStringPreferences(Constant.USER_ID);
//
//        DatabaseReference usersRef = database.getReference("users").child(user_id);
//
//        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                if (dataSnapshot.exists()) {
//                    String name = dataSnapshot.child("name").getValue(String.class);
//                    String image = dataSnapshot.child("image").getValue(String.class);
//                     AppPreferences.getInstance(getBaseContext()).setStringPreferences(Constant.USER_NAME,name);
//                    AppPreferences.getInstance(getBaseContext()).setStringPreferences(Constant.USER_IMAGE,image);
//
//                    // Do something with the retrieved user data
//                    Log.e("UserData", "Name: " + name);
//                    Log.e("UserData", "img  : " + image);
//             }}
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                // Handle any errors or cancellations
//            }
//        });
//
//
//    }
//    private void getPassword(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference passwordRef = database.getReference("Password");
//
//        passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    password = dataSnapshot.getValue(String.class);
//                    if (AppPreferences.getInstance(getBaseContext()).getStringPreferences(Constant.LOGIN).equals("")||
//                         !   AppPreferences.getInstance(getBaseContext()).getStringPreferences(Constant.password).equals(password)){
//                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
//                        finish();
//                    }else {
//                        startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
//                        finish();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors that occurred during the database operation
//            }
//        });
//
//    }

}