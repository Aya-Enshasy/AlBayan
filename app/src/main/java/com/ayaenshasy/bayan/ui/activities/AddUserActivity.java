package com.ayaenshasy.bayan.ui.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivityAddUserBinding;
import com.ayaenshasy.bayan.model.Role;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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

public class AddUserActivity extends BaseActivity {
    ActivityAddUserBinding binding;
    FirebaseStorage firebaseStorage;
    Uri imageUri;
    ActivityResultLauncher<String> al1;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Role user_role;
    private Calendar calendar;
    String birthDate;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        user_role = preferences.getUserRole();
        calendar = Calendar.getInstance();
        showET();
        clickListener();
        userImage();
        requestStoragePermission();
    }

    private void showET() {
        if (user_role == Role.TEACHER) {
            binding.etParentId.setVisibility(View.VISIBLE);
            binding.tvParentId.setVisibility(View.VISIBLE);
            binding.etParentName.setVisibility(View.VISIBLE);
            binding.tvParentName.setVisibility(View.VISIBLE);
            binding.tvId.setText("اضف هوية الطالب ");
        }
    }

    private void clickListener() {
        binding.imgUser.setOnClickListener(View -> {
            al1.launch("image/*");
        });
        binding.btnSave.setOnClickListener(View -> {
            addUser();
            closeKeyboard();
        });
        binding.lazyDatePicker.setOnClickListener(View -> {
            try {
                showDatePickerDialog();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        binding.backArrow.setOnClickListener(View -> {
            finish();
        });
    }

    private void addUser() {
        loaderDialog();

        switch (user_role) {
            case TEACHER:
                addNewUser("students", Role.STUDENT);
                break;
            case SUPERVISOR:
                addNewUser("users", Role.ADMIN);
                break;
            case ADMIN:
                addNewUser("users", Role.TEACHER);
                break;
            case BIG_BOSS:
                addNewUser("users", Role.SUPERVISOR);
                break;
        }
    }

    private void addNewUser(String db_name, Role role) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(db_name);

        String id = binding.etId.getText().toString();
        String responsibleId = user.getId(); // Assuming the teacher ID is retrieved from the "user" object

        String name = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        formatDate(binding.lazyDatePicker.getDate().toString());
        String gender = binding.radioFemale.isChecked() ? binding.radioFemale.getText().toString() : binding.radioMale.getText().toString();

        // Validate input fields before proceeding
        if (TextUtils.isEmpty(name)) {
            showErrorMessage("اضف الاسم");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showErrorMessage("اضف رقم الهاتف");
            return;
        }

        if (TextUtils.isEmpty(birthDate)) {
            showErrorMessage("اضف تاريخ الميلاد");
            return;
        }

        // Create a map to store user data
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id", id);
        map.put("phone", phone);
        map.put("birthDate", birthDate);
        map.put("responsible_id", responsibleId);
        map.put("gender", gender);
        map.put("role", role.toString());

        if (role == Role.STUDENT) {
            String parentId = binding.etParentId.getText().toString();

            // Validate the parent ID field for students
            if (TextUtils.isEmpty(parentId)) {
                showErrorMessage("اضف رقم هوية الاب");
                return;
            }

            map.put("parentId", parentId);

            // Check if the ID already exists in the database
            usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // ID already exists, show an error message or handle the case accordingly
                        showErrorMessage("رقم الهوية موجود بالفعل");
                    } else {
                        // ID is unique, proceed with adding the student to the database
                        uploadUserImage(usersRef.child(id), map);

                        // Create the parent
                        createParent("parent", parentId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }
    }

    private void createParent(String db_name, String parentId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(db_name);

        // Retrieve parent details from the UI
        String parentName = binding.etParentName.getText().toString();
        String parentPhone = binding.etPhone.getText().toString();

        // Validate parent details
        if (TextUtils.isEmpty(parentName)) {
            showErrorMessage("اضف اسم الاب");
            return;
        }

        if (TextUtils.isEmpty(parentPhone)) {
            showErrorMessage("اضف رقم هاتف الاب");
            return;
        }

        // Create a map to store parent data
        Map<String, Object> parentMap = new HashMap<>();
        parentMap.put("name", parentName);
        parentMap.put("phone", parentPhone);

        // Check if the parent ID already exists in the database
        usersRef.child(parentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // ID already exists, show an error message or handle the case accordingly
                    showErrorMessage("رقم الهوية للأب موجود بالفعل");
                } else {
                    // ID is unique, proceed with adding the parent to the database
                    usersRef.child(parentId).setValue(parentMap);
                    showSuccessMessage("تم إنشاء المستخدم والأب بنجاح");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void uploadUserImage(DatabaseReference userRef, Map<String, Object> userData) {
        if (imageUri != null) {
            // Generate a unique filename for the image
            String filename = UUID.randomUUID().toString();

            // Get a reference to the Firebase Storage location
            StorageReference storageReference = firebaseStorage.getReference().child("images/" + filename);

            // Upload the image file to Firebase Storage
            UploadTask uploadTask = storageReference.putFile(Uri.parse(String.valueOf(imageUri)));

            // Monitor the upload process
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL of the uploaded image
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Add the image URL to the user data
                    userData.put("image", imageUrl);

                    // Add the user to the database with the complete user data
                    addUserWithUserData(userRef, userData);
                }).addOnFailureListener(e -> {
                    showErrorMessage("خطا بتحميل الصورة حاول مرة اخرى");
                });
            }).addOnFailureListener(e -> {
                showErrorMessage("خطا بتحميل الصورة حاول مرة اخرى");
            });
        } else {
            addUserWithUserData(userRef, userData);
        }
    }

    private void addUserWithUserData(DatabaseReference userRef, Map<String, Object> userData) {
        userRef.setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessMessage("تم الاضافة بنجاح");
                        finish();
                    } else {
                        showErrorMessage("خطا بتسجيل المستخدم حاول لاحقا");
                    }
                });
    }

    private void userImage() {
        al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        imageUri = result;
                        Glide.with(getBaseContext()).load(imageUri).transform(new RoundedCorners(8))
                                .error(R.drawable.ic_user_circle_svgrepo_com).into(binding.imgUser);
                    }
                });
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

    private void showDatePickerDialog() throws ParseException {
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

    private void formatDate(String date) {
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
    }

}