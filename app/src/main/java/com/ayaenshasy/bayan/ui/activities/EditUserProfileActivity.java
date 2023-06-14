package com.ayaenshasy.bayan.ui.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivityEditUserProfileBinding;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EditUserProfileActivity extends BaseActivity {
    ActivityEditUserProfileBinding binding;
    FirebaseStorage firebaseStorage;
    Uri imageUri;
    //    ActivityResultLauncher<String> al1;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Role user_role;
    private Calendar calendar;
    String birthDate;
    Boolean isImageChange;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        getData();
        init();
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        User user = preferences.getUserProfile();
        binding.etName.setText(user.getName());
        binding.etId.setText(user.getId());
        binding.etPhone.setText(user.getPhone());
        binding.lazyDatePicker.setDate(new Date(user.getBirthDate()));
        Log.e("user.getGender()", user.getGender());    //todo
        if (!user.getGender().equals("انثى") || !user.getGender().equals("female")) {
            binding.radioMale.setChecked(true);
            binding.radioFemale.setChecked(false);
        } else {
            binding.radioFemale.setChecked(true);
            binding.radioMale.setChecked(false);
        }
        binding.radioFemale.setEnabled(false);
        binding.radioMale.setEnabled(false);
        binding.etId.setEnabled(false);
        binding.etPhone.setEnabled(false);
        binding.etParentId.setVisibility(View.GONE);
        binding.tvParentId.setVisibility(View.GONE);
        Glide.with(getBaseContext()).load(user.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(binding.imgUser);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        user_role = preferences.getUserRole();
        calendar = Calendar.getInstance();
        clickListener();
        requestStoragePermission();
    }

    private ActivityResultLauncher<String> al1 = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    // Handle the selected image URI here
                    imageUri = result;
                    // Perform any necessary actions with the selected image
                }
            }
    );

    private void clickListener() {
        binding.imgUser.setOnClickListener(View -> {
            al1.launch("image/*");
        });
        binding.btnSave.setOnClickListener(View -> {
            addUser();
            closeKeyboard();
        });
        binding.lazyDatePicker.setOnClickListener(View -> {
            showDatePickerDialog();
        });
        binding.backArrow.setOnClickListener(View -> {
            finish();
        });
    }

    private void addUser() {
        loaderDialog();
        updateUserData("users", user_role);
    }

    private void updateUserData(String dbName, Role role) {
        String id = binding.etId.getText().toString();
        String name = binding.etName.getText().toString();
        String birthDate = formatDate(binding.lazyDatePicker.getDate().toString());

        // Validate input fields before proceeding
        if (TextUtils.isEmpty(name)) {
            showErrorMessage("اضف الاسم");
            return;
        }

        if (TextUtils.isEmpty(birthDate)) {
            showErrorMessage("اضف تاريخ الميلاد");
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(dbName);
        DatabaseReference userRef = usersRef.child(id);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("name", name);
                    updatedData.put("birthDate", birthDate);

                    if (role == Role.STUDENT) {
                        String parentId = binding.etParentId.getText().toString();
                        if (TextUtils.isEmpty(parentId)) {
                            showErrorMessage("اضف رقم هوية الاب");
                            return;
                        }
                        updatedData.put("parentId", parentId);
                    }

                    deletePreviousImage(userRef, updatedData);
                } else {
                    showErrorMessage("User with ID " + id + " does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void deletePreviousImage(DatabaseReference userRef, Map<String, Object> updatedData) {
        userRef.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    deleteImageAndUploadNew(userRef, updatedData, imageUrl);
                } else {
                    updateUserWithNewData(userRef, updatedData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void deleteImageAndUploadNew(DatabaseReference userRef, Map<String, Object> updatedData, String imageUrl) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(aVoid -> {
            uploadNewUserImage(userRef, updatedData);
        }).addOnFailureListener(e -> {
            showErrorMessage("Failed to delete previous image");
        });
    }

    private void uploadNewUserImage(DatabaseReference userRef, Map<String, Object> updatedData) {
        if (imageUri != null) {
            String filename = UUID.randomUUID().toString();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + filename);
            UploadTask uploadTask = storageReference.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String newImageUrl = downloadUri.toString();
                    updatedData.put("image", newImageUrl);
                }

                updateUserWithNewData(userRef, updatedData);
            }).addOnFailureListener(e -> {
                showErrorMessage("Failed to upload the new image. Please try again.");
            });
        } else {
            updateUserWithNewData(userRef, updatedData);
        }
    }

    private void updateUserWithNewData(DatabaseReference userRef, Map<String, Object> updatedData) {
        userRef.updateChildren(updatedData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showSuccessMessage("تم تحديث بيانات المستخدم بنجاح");
                updateSharedPreferences(updatedData);
                finish();
            } else {
                showErrorMessage("فشل في تحديث بيانات المستخدم. يرجى المحاولة لاحقًا.");
            }
        });
    }

    private void updateSharedPreferences(Map<String, Object> updatedData) {
        User user = preferences.getUserProfile();
        String name = (String) updatedData.get("name");
        String birthDate = (String) updatedData.get("birthDate");
        user.setName(name);
        user.setBirthDate(birthDate);
        preferences.setUserProfile(user);
    }

    private void showErrorMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        loader_dialog.dismiss();
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        loader_dialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void showDatePickerDialog() {
        LazyDatePicker lazyDatePicker = findViewById(R.id.lazyDatePicker);
        lazyDatePicker.setDateFormat(LazyDatePicker.DateFormat.MM_DD_YYYY);

        lazyDatePicker.setOnDatePickListener(new LazyDatePicker.OnDatePickListener() {
            @Override
            public void onDatePick(Date dateSelected) {

            }
        });

        lazyDatePicker.setOnDateSelectedListener(new LazyDatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Boolean dateSelected) {

                //...
            }
        });

    }

    private String formatDate(String date) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        try {
            Date inputDate = inputDateFormat.parse(date);
            String formattedDate = outputDateFormat.format(inputDate);
            birthDate = formattedDate;
            System.out.println("Formatted Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return birthDate;
    }
}
