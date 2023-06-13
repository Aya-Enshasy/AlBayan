package com.ayaenshasy.bayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivityAddUserBinding;
import com.ayaenshasy.bayan.databinding.ActivityChangePasswordBinding;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePasswordActivity extends BaseActivity {
    ActivityChangePasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        binding.addBtn.setOnClickListener(View->{changePassword();});

        binding.backArrow.setOnClickListener(View->{finish();});

    }

//    private void changePassword() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//
//        DatabaseReference passwordRef = database.getReference("Password").child(AppPreferences.getInstance(getBaseContext()).getStringPreferences(Constant.password));
//
//        loaderDialog();
//
//        passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
//                     dataSnapshot.getRef().setValue(binding.password.getText().toString());
//                    Toast.makeText(ChangePasswordActivity.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
//                    finish();
//                } else {
//                    // Password does not exist or is null
//                    Toast.makeText(ChangePasswordActivity.this, "كلمة المرور غير صحيحة", Toast.LENGTH_SHORT).show();
//                }
//                loader_dialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                loader_dialog.dismiss();
//                Toast.makeText(ChangePasswordActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.e("Password", databaseError.getMessage());
//            }
//        });
//    }

}