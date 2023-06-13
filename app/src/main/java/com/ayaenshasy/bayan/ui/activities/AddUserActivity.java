package com.ayaenshasy.bayan.ui.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddUserActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    ActivityAddUserBinding binding;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    Uri imageUri;
    ActivityResultLauncher<String> al1;
    boolean img = false;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Role user_role;
    private Calendar calendar;
    Map<String, Object> map;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void clickListener() {
        binding.imgUser.setOnClickListener(View -> {
            al1.launch("image/*");
        });
        binding.btnSave.setOnClickListener(View -> {
            if (binding.etName.getText().toString().equals(""))
                Toast.makeText(this, "اضف الاسم", Toast.LENGTH_SHORT).show();
            else if (binding.etId.getText().toString().equals(""))
                Toast.makeText(this, "اضف رقم الهوية", Toast.LENGTH_SHORT).show();
            else if (binding.etPhone.getText().toString().equals(""))
                Toast.makeText(this, "اضف رقم الهاتف", Toast.LENGTH_SHORT).show();
//            else if (img==false)
//                Toast.makeText(this, "اضف صورة لو سمحت", Toast.LENGTH_SHORT).show();
            else
                addUser();

            closeKeyboard();
        });
        binding.btnSelectDate.setOnClickListener(View -> {
            showDatePickerDialog();
        });

//        binding.backArrow.setOnClickListener(View -> {
//            finish();
//        });
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
        String birthDate = binding.etBrithDate.getText().toString();
        String gender = binding.radioFemale.isChecked() ? binding.radioFemale.getText().toString() : binding.radioMale.getText().toString();

        // Validate input fields before proceeding
        if (TextUtils.isEmpty(name)) {
            showErrorMessage("Name field is empty");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showErrorMessage("Phone field is empty");
            return;
        }

        if (TextUtils.isEmpty(birthDate)) {
            showErrorMessage("Birth date field is empty");
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
                showErrorMessage("Parent ID field is empty");
                return;
            }

            map.put("parentId", parentId);
        }

        // Check if the ID already exists in the database
        usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // ID already exists, show an error message or handle the case accordingly
                    showErrorMessage("ID already exists");
                } else {
                    // ID is unique, proceed with adding the user to the database

                    // Upload the image to Firebase Storage
                    uploadUserImage(usersRef.child(id), map);
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
                    // Handle any errors during URL retrieval
                    showErrorMessage("Failed to retrieve image URL");
                });
            }).addOnFailureListener(e -> {
                // Handle any errors during the upload process
                showErrorMessage("Image upload failed");
            });
        } else {
            // No image selected, proceed without uploading an image
            addUserWithUserData(userRef, userData);
        }
    }

    private void addUserWithUserData(DatabaseReference userRef, Map<String, Object> userData) {
        userRef.setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User added successfully
                        showSuccessMessage("User added successfully");
                    } else {
                        // User addition failed
                        showErrorMessage("Failed to add user");
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


    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String selectedDate = dateFormat.format(calendar.getTime());

        binding.etBrithDate.setText(selectedDate);
    }
}