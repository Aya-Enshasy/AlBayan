package com.ayaenshasy.AlBayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.base.BaseActivity;
import com.ayaenshasy.AlBayan.databinding.ActivityLoginBinding;
import com.ayaenshasy.AlBayan.model.user.Parent;
import com.ayaenshasy.AlBayan.model.user.Student;
import com.ayaenshasy.AlBayan.model.user.User;
import com.ayaenshasy.AlBayan.utils.AppPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
        loginUser();
    }

    void loginUser() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersCollection;
        if (isParent) {
            usersCollection = firestore.collection("parent");
        } else {
            usersCollection = firestore.collection("users");
        }

        String desiredValue = binding.identifier.getText().toString();

        Query query = usersCollection.whereEqualTo("id", desiredValue);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            User user = document.toObject(User.class);
                            if (user != null && user.getPhone().equals(binding.etPhoneNumber.getText().toString())) {
                                // Update the fcm_token value
                                String newFcmToken = preferences.getStringPreference(AppPreferences.DEVICE_TOKEN);
                                user.setFcm(newFcmToken);

                                // Update the document in Firestore
                                usersCollection.document(document.getId())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(LoginActivity.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                                                if (isParent) {
                                                    preferences.setParentProfile(user);
                                                } else {
                                                    preferences.setUserProfile(user);
                                                }

                                                startActivity(new Intent(getBaseContext(), BottomNavigationBarActivity.class));
                                                finish();
                                                showNotification("مرحبا", "مرحبا بك في تطبيق البيان");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "فشل تحديث قيمة fcm_token", Toast.LENGTH_SHORT).show();
                                                loader_dialog.dismiss();
                                            }
                                        });
                                return;
                            }
                        }
                        Toast.makeText(LoginActivity.this, "تأكد من البيانات المدخلة ", Toast.LENGTH_SHORT).show();
                        loader_dialog.dismiss();
                    } else {
                        loader_dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "تأكد من البيانات المدخلة ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loader_dialog.dismiss();
                    // Handle any errors
                }
            }
        });
    }

}