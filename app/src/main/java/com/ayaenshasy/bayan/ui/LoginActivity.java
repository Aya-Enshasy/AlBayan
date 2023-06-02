package com.ayaenshasy.bayan.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ayaenshasy.bayan.databinding.ActivityLoginBinding;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String password ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getPassword();
        binding.matchParent.setOnClickListener(View->{checkIfUserIsExist();});
    }

    private void getPassword(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference passwordRef = database.getReference("Password");

        passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                     password = dataSnapshot.getValue(String.class);
                     Log.e(";getPassword",password);
                  } else {
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred during the database operation
            }
        });

    }

    private void checkIfUserIsExist(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");

        String desiredValue = binding.identifier.getText().toString();
        Query query = usersRef.orderByValue().equalTo(desiredValue);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         String value = snapshot.getValue(String.class);
                        if (value.equals(binding.identifier.getText().toString())&&binding.password.getText().toString().equals(password)) {
                            Toast.makeText(LoginActivity.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                            AppPreferences.getInstance(getBaseContext()).setStringPreferences(Constant.LOGIN,Constant.LOGIN);
                            startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "كلمة المرور خاطئة", Toast.LENGTH_SHORT).show();

                        }

                    }
                } else {
                    Toast.makeText(LoginActivity.this, "خطا في رقم الهوية", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("vv",databaseError.getMessage());
            }
        });

    }


}