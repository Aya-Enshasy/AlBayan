package com.ayaenshasy.AlBayan.ui.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.base.BaseActivity;
import com.ayaenshasy.AlBayan.databinding.ActivityEditUserProfileBinding;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.user.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

        try {
            String birthDateStr = user.getBirthDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date birthDate = dateFormat.parse(birthDateStr);

            if (birthDate != null) {
                binding.lazyDatePicker.setText(birthDate+"");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                    Glide.with(getBaseContext()).load(imageUri).into(binding.imgUser);
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

    private void updateUserData(String collectionName, Role role) {
        String id = binding.etId.getText().toString();
        String name = binding.etName.getText().toString();
        String birthDate = binding.lazyDatePicker.getText().toString();

        // Validate input fields before proceeding
        if (TextUtils.isEmpty(name)) {
            showErrorMessage("اضف الاسم");
            return;
        }

         FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection(collectionName);
        DocumentReference userRef = usersRef.document(id);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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

                        if (imageUri != null) {
                            deletePreviousImage(userRef, updatedData);
                        } else {
                            updateUserWithNewData(userRef, updatedData);
                        }
                    } else {
                        showErrorMessage( " غير موجود هذا المستحدم");
                    }
                } else {
                 }
            }
        });
    }

    private void deletePreviousImage(DocumentReference userRef, Map<String, Object> updatedData) {
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains("image")) {
                    String imageUrl = documentSnapshot.getString("image");
                    deleteImageAndUploadNew(userRef, updatedData, imageUrl);
                } else {
                    uploadNewUserImage(userRef, updatedData);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             }
        });
    }

    private void deleteImageAndUploadNew(DocumentReference userRef, Map<String, Object> updatedData, String imageUrl) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadNewUserImage(userRef, updatedData);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorMessage("Failed to delete previous image");
            }
        });
    }

    private void uploadNewUserImage(DocumentReference userRef, Map<String, Object> updatedData) {
        String filename = UUID.randomUUID().toString();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + filename);
        UploadTask uploadTask = storageReference.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String newImageUrl = downloadUri.toString();
                    updatedData.put("image", newImageUrl);
                }

                updateUserWithNewData(userRef, updatedData);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorMessage("Failed to upload the new image. Please try again.");
            }
        });
    }

    private void updateUserWithNewData(DocumentReference userRef, Map<String, Object> updatedData) {
        userRef.update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSuccessMessage("تم تحديث بيانات المستخدم بنجاح");
                        updateSharedPreferences(updatedData);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorMessage("فشل في تحديث بيانات المستخدم. يرجى المحاولة لاحقًا.");
                    }
                });
    }

    private void updateSharedPreferences(Map<String, Object> updatedData) {
        User user = preferences.getUserProfile();
        String name = (String) updatedData.get("name");
        String birthDate = (String)  updatedData.get("birthDate");
//        Uri downloadUr.i = imageUri.getResult();
        if (imageUri!=null){
            String imageUrl = imageUri.toString();
            user.setImage(imageUrl);
        }

        user.setName(name);
        user.setBirthDate(birthDate+"");
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }


    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String selectedDate = dateFormat.format(calendar.getTime());

            binding.lazyDatePicker.setText(selectedDate);
        }
    };
}
