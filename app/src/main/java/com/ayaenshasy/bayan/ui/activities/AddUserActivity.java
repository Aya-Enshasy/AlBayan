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

public class AddUserActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    ActivityAddUserBinding binding;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    String image1;
    ActivityResultLauncher<String> al1;
    boolean img = false;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Role user_role;
    private Calendar calendar;


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
        Map<String, Object> map = new HashMap<>();
        map.put("name", binding.etName.getText().toString());
        map.put("image", image1);
        map.put("id", binding.etId.getText().toString());
        map.put("phone", binding.etPhone.getText().toString());
        map.put("birthDate", binding.etBrithDate.getText().toString());
        //parent id only when we add student
        map.put("parentId", binding.etParentId.getText().toString());
        if (binding.radioFemale.isChecked())
            map.put("gender", binding.radioFemale.getText().toString());
        else map.put("gender", binding.radioMale.getText().toString());

        switch (user_role) {
            case TEACHER:
                addNewUser("students", Role.STUDENT);
                break;
            case supervisor:
                addNewUser("users", Role.ADMIN);
                break;
            case ADMIN:
                addNewUser("users", Role.TEACHER);
                break;
        }
    }

    private void addNewUser(String db_name, Role role) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(db_name);

        // Create a new User or Student object based on the role
        Student newUser;
        String name = binding.etName.getText().toString();
        String id = binding.etId.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String image = image1;
        String birthDate = binding.etBrithDate.getText().toString();
        String teacherId = user.getIdNumber();  // Assuming the teacher ID is retrieved from the "user" object

        if (role == Role.STUDENT) {
            String parentId = binding.etParentId.getText().toString();
            newUser = new Student(name, id, phone, image, role, birthDate, parentId, teacherId);
        } else {
            
            newUser = new Student(name, id, phone, image, role, birthDate, "", teacherId);
        }

        // Check if the ID already exists in the database
        Query query = usersRef.child(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // ID already exists, show an error message or handle the case accordingly
                    Toast.makeText(getApplicationContext(), "ID already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // ID is unique, proceed with adding the user to the database
                    // Set the user object at the specified key (ID)
                    usersRef.child(id).setValue(newUser)
                            .addOnCompleteListener(task -> {
                                // Check if the user addition was successful
                                if (task.isSuccessful()) {
                                    // User added successfully
                                    // Perform any additional actions or show success message
                                    Toast.makeText(getApplicationContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                                    loader_dialog.dismiss();
                                } else {
                                    // User addition failed
                                    // Handle the error or show an error message
                                    Toast.makeText(getApplicationContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                                    loader_dialog.dismiss();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
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

                        Glide.with(getBaseContext()).load(result.toString()).transform(new RoundedCorners(8)).
                                error(R.drawable.ic_user_circle_svgrepo_com).into(binding.imgUser);

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
                                                    Glide.with(getBaseContext()).load(image1).transform(new RoundedCorners(8))
                                                            .error(R.drawable.ic_user_circle_svgrepo_com).into(binding.imgUser);
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