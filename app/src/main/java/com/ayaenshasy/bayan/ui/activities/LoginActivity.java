package com.ayaenshasy.bayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivityLoginBinding;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.matchParent.setOnClickListener(View -> {
            checkIfUserExists();
        });
    }

//    private void getPassword(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference passwordRef = database.getReference("Password");
//
//        passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                     password = dataSnapshot.getValue(String.class);
//                     Log.e(";getPassword",password);
//                    AppPreferences.getInstance(getBaseContext()).setStringPreferences(Constant.password,password);
//
//                } else {
//                 }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors that occurred during the database operation
//            }
//        });
//
//    }

    private void checkIfUserExists() {
        loaderDialog();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        String desiredValue = binding.identifier.getText().toString();

        Query query = usersRef.orderByChild("id").equalTo(desiredValue);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getPhone().equals(binding.etPhoneNumber.getText().toString())) {
                            Toast.makeText(LoginActivity.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
//                            preferences.setBooleanPreference(AppPreferences.IS_FIRST_TIME, false);
                            preferences.setUserProfile(user);
                            startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "تأكد من البيانات المدخلةmmmm ", Toast.LENGTH_SHORT).show();
                            loader_dialog.dismiss();
                        }
                    }
                } else {
                    loader_dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "تأكد من البيانات المدخلةa ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loader_dialog.dismiss();
                // Handle any errors or cancellations
            }
        });
    }


}