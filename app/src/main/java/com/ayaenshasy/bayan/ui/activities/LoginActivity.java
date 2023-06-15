package com.ayaenshasy.bayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivityLoginBinding;
import com.ayaenshasy.bayan.model.user.Parent;
import com.ayaenshasy.bayan.model.user.Student;
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
    boolean isParent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.matchParent.setOnClickListener(View -> {
            checkIfUserExists();
        });
        changeUser();
        binding.tvParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isParent = true;
                changeUser();
                animateUnderline(view);

            }
        });
        binding.tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isParent = false;
                changeUser();
                animateUnderline(view);
            }
        });
    }

    private void animateUnderline(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        float startX = location[0];
        float endX = startX + view.getWidth();

        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.underline, "x", startX, endX);
        animator.setDuration(200);
        animator.start();
    }

    @SuppressLint("ResourceAsColor")
    void changeUser() {
        String text1 = "موظف ";
        String text2 = "ولي أمر ";

        if (!isParent) {
            // Create a SpannableString with the desired text
            SpannableString spannableString = new SpannableString(text1);

            // Apply the UnderlineSpan to the entire text
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Use the spannableString as needed, e.g., set it to a TextView
            binding.tvUser.setText(spannableString);
            binding.tvParent.setText(text2);
            binding.tvUser.setTextColor(R.color.orange);
            binding.tvParent.setTextColor(R.color.black);
        } else {         // Create a SpannableString with the desired text
            SpannableString spannableString = new SpannableString(text2);
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvParent.setText(spannableString);
            binding.tvUser.setText(text1);
            binding.tvUser.setTextColor(R.color.black);
            binding.tvParent.setTextColor(R.color.orange);
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
        Toast.makeText(this, "user", Toast.LENGTH_SHORT).show();

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
        Toast.makeText(this, "parent", Toast.LENGTH_SHORT).show();
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
                            Parent parent=new Parent();
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