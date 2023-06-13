package com.ayaenshasy.bayan.ui.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivityAddUserBinding;
import com.ayaenshasy.bayan.databinding.ActivityEditUserProfileBinding;
import com.ayaenshasy.bayan.databinding.ActivitySplashBinding;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditUserProfileActivity extends BaseActivity {
    ActivityEditUserProfileBinding binding;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    String image1;
    ActivityResultLauncher<String> al1;
    boolean img = false;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        clickListener();
        userImage();
        requestStoragePermission();
        getData();
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        User user = preferences.getUserProfile();
        binding.name.setText(user.getName());
        binding.identifier.setText(user.getIdNumber() + "");
        Glide.with(getBaseContext()).load(user.getImageUri()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(binding.userImage);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void clickListener() {
        binding.userImage.setOnClickListener(View -> {
            al1.launch("image/*");
        });
        binding.addBtn.setOnClickListener(View -> {
            if (binding.name.getText().toString().equals(""))
                Toast.makeText(this, "اضف الاسم", Toast.LENGTH_SHORT).show();
            else
//                editUser();

                closeKeyboard();
        });
        binding.backArrow.setOnClickListener(View -> {
            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void userImage() {
        al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        Glide.with(getBaseContext()).load(result.toString()).transform(new RoundedCorners(8)).error(R.drawable.ic_user_circle_svgrepo_com).into(binding.userImage);

                        if (result != null) {
                            storageReference = firebaseStorage.getReference("images/" + result.getLastPathSegment());
                            StorageTask<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(result);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    img = true;
                                                    image1 = task.getResult().toString();
                                                    Glide.with(getBaseContext()).load(image1).transform(new RoundedCorners(8)).error(R.drawable.ic_user_circle_svgrepo_com).into(binding.userImage);
                                                    Log.e("UploadActivity1", image1);

                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            e.printStackTrace();
                                            Toast.makeText(getBaseContext(), "Image Uploaded Failed!!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });


    }

//    private void editUser() {
//        loaderDialog();
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", binding.name.getText().toString());
//        map.put("image", image1);
//        map.put("id", binding.identifier.getText().toString());
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference usersRef = database.getReference("users");
//
//        User user = new User(binding.identifier.getText().toString(), binding.name.getText().toString(), image1);
//
//        usersRef.child(binding.identifier.getText().toString()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//
//                Toast.makeText(EditUserProfileActivity.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
//                 AppPreferences.getInstance(getBaseContext()).setStringPreferences(Constant.USER_NAME, binding.name.getText().toString());
//                AppPreferences.getInstance(getBaseContext()).setStringPreferences(Constant.USER_IMAGE,image1);
//                finish();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("TAG", "Error adding document", e);
//                Toast.makeText(EditUserProfileActivity.this, "حاول مجددا", Toast.LENGTH_SHORT).show();
//                loader_dialog.dismiss();
//            }
//        });
//
//
//    }
}