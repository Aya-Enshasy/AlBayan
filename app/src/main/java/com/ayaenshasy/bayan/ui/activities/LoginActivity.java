package com.ayaenshasy.bayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.SplashActivity;
import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivityLoginBinding;
import com.ayaenshasy.bayan.fcm.MyFirebaseMessagingService;
import com.ayaenshasy.bayan.model.user.Parent;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
     boolean isParent = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

         btnActions();
        binding.tvUser.setBackgroundResource(R.drawable.login_color);
        binding.tvParent.setTextColor(getColor(R.color.orange));
        binding.tvParent.setTextColor(getColor(R.color.black));

        changeUser();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void btnActions() {
        binding.matchParent.setOnClickListener(View -> {
            checkIfUserExists();
        });

        binding.tvParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isParent = true;
                changeUser();
                binding.tvParent.setBackgroundResource(R.drawable.login_color);
                binding.tvUser.setBackgroundResource(R.drawable.transparent);

//                animateUnderline(view);

            }
        });
        binding.tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isParent = false;
                changeUser();
                binding.tvUser.setBackgroundResource(R.drawable.login_color);
                binding.tvParent.setBackgroundResource(R.drawable.transparent);

//                animateUnderline(view);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void changeUser() {
        String text1 = "موظف ";
        String text2 = "ولي أمر ";

        if (!isParent) {
            // Create a SpannableString with the desired text
            SpannableString spannableString = new SpannableString(text1);

            // Apply the UnderlineSpan to the entire text
//            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            binding.tvUser.setText(spannableString);
            binding.tvParent.setText(text2);

            binding.tvUser.setTextColor(getColor(R.color.black));
            binding.tvParent.setTextColor(getColor(R.color.orange));
        } else {
            SpannableString spannableString = new SpannableString(text2);
//            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvParent.setText(spannableString);
            binding.tvUser.setText(text1);
            binding.tvUser.setTextColor(getColor(R.color.orange));
            binding.tvParent.setTextColor(getColor(R.color.black));
        }
    }

    private void checkIfUserExists() {
        loaderDialog();
        if (isParent) {
            loginParent();
        } else {
            loginUser();
        }
    }

    void loginUser() {

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

                            showNotification("مرحبا", "مرحبا بك في تطبيق البيان");
                        } else {
                            Toast.makeText(LoginActivity.this, "تأكد من البيانات المدخلة ", Toast.LENGTH_SHORT).show();
                            loader_dialog.dismiss();
                        }
                    }
                } else {
                    loader_dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "تأكد من البيانات المدخلة ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loader_dialog.dismiss();
                // Handle any errors or cancellations
            }
        });
    }

    void loginParent() {
        // Assuming you have a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Specify the parent ID and phone number to search
        String parentIdToSearch = binding.identifier.toString();
        String phoneNumberToMatch = binding.etPhoneNumber.toString();
        ;

        // Query the "students" table to find a student with the given parent ID
        databaseReference.child("students").orderByChild("parentId").equalTo(parentIdToSearch)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isParentFound = false;

                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            // Get the student record
                            Student student = studentSnapshot.getValue(Student.class);

                            // Check if the phone number matches
                            if (student.getPhone().equals(phoneNumberToMatch)) {
                                isParentFound = true;
                                break;
                            }
                        }

                        if (isParentFound) {
                            Parent parent = new Parent();
                            parent.setId(parentIdToSearch);
                            parent.setPhoneNumber(phoneNumberToMatch);
                            preferences.setParentProfile(parent);
                            // Parent ID and phone number match a student record
                            // Perform your desired action here
                        } else {
                            // Parent ID or phone number did not match any student record
                            // Handle the case accordingly
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any database errors
                    }
                });
    }
}