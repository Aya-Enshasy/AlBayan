package com.ayaenshasy.AlBayan.ui.activities;

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

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.base.BaseActivity;
import com.ayaenshasy.AlBayan.databinding.ActivityAddUserBinding;
import com.ayaenshasy.AlBayan.model.Role;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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


public class AddUserActivity extends BaseActivity {
    ActivityAddUserBinding binding;
    FirebaseStorage firebaseStorage;
    Uri imageUri;
    String imageUrl;
    ActivityResultLauncher<String> al1;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Role user_role;
    private Calendar calendar;
    String birthDate;
    String parentId;


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
        user_role = preferences.getUserProfile() != null ? preferences.getUserRole() : Role.BIG_BOSS;
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
            showDatePickerDialog();

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
            default:
                addNewUser("users", Role.BIG_BOSS);
                break;
        }
    }

    private void addNewUser(String collectionName, Role role) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String id = binding.etId.getText().toString();
        String responsibleId = user.getId(); // Assuming the teacher ID is retrieved from the "user" object

        String name = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String gender = binding.radioFemale.isChecked() ? binding.radioFemale.getText().toString() : binding.radioMale.getText().toString();

        // Validate input fields before proceeding
        if (TextUtils.isEmpty(id)) {
            showErrorMessage("اضف رقم الهويه للمستخد الجديد ");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            showErrorMessage("اضف الاسم");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showErrorMessage("اضف رقم الهاتف");
            return;
        }

        String date = binding.lazyDatePicker.getText().toString();
        if (TextUtils.isEmpty(date)) {
            showErrorMessage("اضف تاريخ الميلاد");
            return;
        }

        // Create a map to store user data
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id", id);
        map.put("phone", phone);
        map.put("birthDate", date);
        map.put("responsible_id", "123");
        map.put("gender", gender);
        map.put("role", role.toString());
        map.put("image", imageUrl);

        if (role == Role.STUDENT) {
            parentId = binding.etParentId.getText().toString();

            // Validate the parent ID field for students
            if (TextUtils.isEmpty(parentId)) {
                showErrorMessage("اضف رقم هوية الاب");
                return;
            }
            map.put("parentId", parentId);
        }

        // Check if the ID already exists in the database
        db.collection(collectionName).document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // ID already exists, show an error message or handle the case accordingly
                    showErrorMessage("رقم الهوية موجود بالفعل");
                } else {
                    // ID is unique, proceed with adding the student to the database
                    db.collection(collectionName).document(id).set(map)
                            .addOnSuccessListener(aVoid -> {

                                // Create the parent if the user is a student
                                if (role == Role.STUDENT) {

                                    createParent("parent", parentId);


                                } else {
                                    loader_dialog.dismiss();
                                }
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                            });
                }
            } else {
                Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();

                // Handle failure
            }
        });
    }

    private void createParent(String collectionName, String parentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        parentMap.put("role", Role.PARENT);
        parentMap.put("id", parentId);
        parentMap.put("image", imageUrl);
        parentMap.put("responsible_id", "123");


        // Check if the parent ID already exists in the database
        db.collection(collectionName).document(parentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // ID already exists, show an error message or handle the case accordingly
                    showErrorMessage("تم إنشاء المستخدم بنجاح");
                    finish();

                } else {

                    // ID is unique, proceed with adding the parent to the database
                    db.collection(collectionName).document(parentId).set(parentMap)
                            .addOnSuccessListener(aVoid -> {
                                showSuccessMessage("تم إنشاء المستخدم بنجاح");
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                            });
                }
            } else {
                // Handle failure
            }
        });
    }

    private void uploadUserImage() {
        // Generate a unique filename for the image
        String filename = UUID.randomUUID().toString();

        // Get a reference to the Firebase Storage location
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + filename);

        // Upload the image file to Firebase Storage
        UploadTask uploadTask = storageReference.putFile(imageUri);

        // Monitor the upload process
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the uploaded image
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                imageUrl = uri.toString();

//                    // Add the image URL to the user data
//                    userData.put("image", imageUrl);
//
                Log.e("image", imageUrl);
//                    // Get the Firebase Firestore reference to the document
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    DocumentReference userRef = db.collection(collectionName).document(documentId);
//
//                    // Add the user to the database with the complete user data
//                    addUserWithUserData(userRef, userData);
            }).addOnFailureListener(e -> {
                showErrorMessage("خطأ في تحميل الصورة، يرجى المحاولة مرة أخرى");
            });
        }).addOnFailureListener(e -> {
            showErrorMessage("خطأ في تحميل الصورة، يرجى المحاولة مرة أخرى");
        });

    }

    private void addUserWithUserData(DocumentReference userRef, Map<String, Object> userData) {
        userRef.set(userData)
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
                        uploadUserImage();
                    }
                });
    }

    private void showErrorMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        loader_dialog.dismiss();
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//        loader_dialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

//    private String formatDate(String date) {
//        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
//        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
//
//        try {
//            Date inputDate = inputDateFormat.parse(date);
//            String formattedDate = outputDateFormat.format(inputDate);
//            System.out.println("Formatted Date: " + formattedDate);
//            return formattedDate;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return null; // Return null if the date couldn't be parsed
//    }


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