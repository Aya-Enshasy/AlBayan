package com.ayaenshasy.bayan.ui.fragments;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.ExamAdapter;
import com.ayaenshasy.bayan.adapter.RemembranceAdapter;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.databinding.AddExamLayoutBinding;
import com.ayaenshasy.bayan.databinding.AddNewAttendanceLayoutBinding;
import com.ayaenshasy.bayan.databinding.FragmentExamHistoryStudentBinding;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Attendance;
import com.ayaenshasy.bayan.model.Exam;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExamHistoryStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExamHistoryStudentFragment extends BaseFragment {
    FragmentExamHistoryStudentBinding binding;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAPTURE_IMAGE = 3;
    ExamAdapter adapter;
    private List<Exam> list=new ArrayList<>();
    private ShapeableImageView imgUser; // Declare imgUser as a class member
    String date;
    String user_id;


    private Uri selectedImageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExamHistoryStudentFragment() {
        // Required empty public constructor
    }

    public static ExamHistoryStudentFragment newInstance(String param1, String param2) {
        ExamHistoryStudentFragment fragment = new ExamHistoryStudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExamHistoryStudentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        user_id = getActivity().getIntent().getStringExtra(USER_ID);

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });


        getData();

        return view;
    }


    void getData() {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("exams");
        examsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot studentSnapshot = dataSnapshot.child(user_id); // Get the snapshot for student with ID "12"

                    for (DataSnapshot examSnapshot : studentSnapshot.getChildren()) {
                        String examId = examSnapshot.getKey();
                        String degree = examSnapshot.child("degree").getValue(String.class);
                        String image = examSnapshot.child("image").getValue(String.class);
                        String mosque = examSnapshot.child("mosque").getValue(String.class);
                        String name = examSnapshot.child("name").getValue(String.class);
                        Exam exam = new Exam(degree, image, mosque, name);

                        list.add(exam);
                    }

                    RemembranceAdapter(); // Call the adapter setup method here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void RemembranceAdapter() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new ExamAdapter(list, getActivity());
        binding.recyclerView.setAdapter(adapter);
    }


    private void showBottomSheet() {
         // Check if the fragment is attached to the activity and the context is not null
        if (isAdded() && getContext() != null) {
            // Create and show the bottom sheet
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.add_exam_layout, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            // Get references to the views
            imgUser = bottomSheetView.findViewById(R.id.img_user);
            TextView tvName = bottomSheetView.findViewById(R.id.tv_name);
            AppCompatEditText etName = bottomSheetView.findViewById(R.id.et_name);
            TextView tvDegree = bottomSheetView.findViewById(R.id.tv_degree);
            AppCompatEditText etDegree = bottomSheetView.findViewById(R.id.et_degree);
            TextView tvMosque = bottomSheetView.findViewById(R.id.tv_mosque);
            AppCompatEditText etMosque = bottomSheetView.findViewById(R.id.et_mosque);
            TextView tvDate = bottomSheetView.findViewById(R.id.tv_date);
            LazyDatePicker etDate = bottomSheetView.findViewById(R.id.et_date);
            ProgressBar progressBar = bottomSheetView.findViewById(R.id.progressBar);
            AppCompatButton btnSave = bottomSheetView.findViewById(R.id.btn_save);
            imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageOptions();
                }
            });
            // Set click listener for the save button
            btnSave.setOnClickListener(v -> {
                // Get the values entered by the user
                String name = etName.getText().toString().trim();
                String degree = etDegree.getText().toString().trim();
                String mosque = etMosque.getText().toString().trim();

                // Validate fields
                if (TextUtils.isEmpty(name)) {
                    etName.setError("الرجاء إدخال الاسم");
                    return;
                }

                if (TextUtils.isEmpty(degree)) {
                    etDegree.setError("الرجاء إدخال الدرجة");
                    return;
                }

                if (TextUtils.isEmpty(mosque)) {
                    etMosque.setError("الرجاء إدخال اسم المسجد");
                    return;
                }
                if (etDate.getDate().toString().equals("")) {
                    Toast.makeText(context, "تاكد من ادخال التاريخ", Toast.LENGTH_LONG).show();
                     return;
                }

                formatDate(etDate.getDate().toString());

                // Show the progress view
                progressBar.setVisibility(View.VISIBLE);

                // Upload image to Firebase Storage
                if (!name.equals("")||!degree.equals("")||!mosque.equals("")){
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                // Image upload success
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Get the download URL of the uploaded image
                                    String imageUrl = uri.toString();

                                    // Perform your desired operations with the entered data and image URL here
                                    // ...

                                    // Save the data to Firebase Realtime Database
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("exams");

                                    DatabaseReference userRef = reference.child(user_id); // Reference to the user node

                                    DatabaseReference newEntryRef = userRef.push(); // Generate a new unique key

                                    newEntryRef.child("name").setValue(name);
                                    newEntryRef.child("degree").setValue(degree);
                                    newEntryRef.child("mosque").setValue(mosque);
                                    newEntryRef.child("date").setValue(date);
                                    newEntryRef.child("image").setValue(imageUrl);


                                    // Data saved successfully
                                    Toast.makeText(getContext(), "تم حفظ البيانات بنجاح", Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.dismiss();
                                }).addOnFailureListener(e -> {
                                    // Failed to get download URL of the uploaded image
                                    Toast.makeText(getContext(), "فشل في الحصول على رابط الصورة: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                });
                            }).addOnFailureListener(e -> {
                                // Failed to upload image
                                Toast.makeText(getContext(), "فشل في تحميل الصورة: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            });
                }else {
                    Toast.makeText(context, "تاكد من ادخال البيانات", Toast.LENGTH_SHORT).show();
                }

            });

            bottomSheetDialog.show();

            // Animate the bottom sheet dialog
            animateBottomSheet(bottomSheetDialog);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void showImageOptions() {
        // Create an intent to pick an image from the gallery or capture an image using the camera
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setType("image/*");

        // Check if the device has a camera
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create a chooser dialog to select between gallery and camera
            Intent chooser = Intent.createChooser(intent, "اختر صورة");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{getCameraIntent()});

            // Start the activity to pick an image
            startActivityForResult(chooser, REQUEST_PICK_IMAGE);
        } else {
            // No camera available, so only gallery option is available
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private Intent getCameraIntent() {
        // Create an intent to capture an image using the camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create a unique filename for the captured image
            String fileName = UUID.randomUUID().toString() + ".jpg";
            Uri photoUri = getOutputUri(fileName);

            // Set the output file for the captured image
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            selectedImageUri = photoUri;

            // Return the camera intent
            return cameraIntent;
        } else {
            Toast.makeText(context, "الكاميرا غير متوفرة", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private Uri getOutputUri(String fileName) {
        // TODO: Implement your logic to create a file URI for storing the captured image
        // Example: return Uri.fromFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName));
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE) {
                // Image picked from gallery
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    // Set the selected image to the image view
                    imgUser.setImageURI(selectedImageUri);
                }
            } else if (requestCode == REQUEST_CAPTURE_IMAGE) {
                // Image captured using camera
                if (selectedImageUri != null) {
                    // Set the captured image to the image view
                    imgUser.setImageURI(selectedImageUri);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, show the image options
                showImageOptions();
            } else {
                Toast.makeText(context, "تم رفض إذن الكاميرا", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgress(boolean show, ProgressBar progressView) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

    private void animateBottomSheet(BottomSheetDialog bottomSheetDialog) {
        View bottomSheetView = bottomSheetDialog.findViewById(R.id.bottom_sheet_layout);
        bottomSheetView.setBackgroundResource(R.drawable.buttombar);

        if (bottomSheetView != null && bottomSheetView.isAttachedToWindow()) {
            int centerX = (bottomSheetView.getLeft() + bottomSheetView.getRight()) / 2;
            int centerY = (bottomSheetView.getTop() + bottomSheetView.getBottom()) / 2;

            int startRadius = 0;
            int endRadius = Math.max(bottomSheetView.getWidth(), bottomSheetView.getHeight());

            Animator circularReveal = ViewAnimationUtils.createCircularReveal(bottomSheetView, centerX, centerY, startRadius, endRadius);
            circularReveal.setDuration(500);
            bottomSheetView.setVisibility(View.VISIBLE);
            circularReveal.start();
        }
    }

    private void formatDate(String date) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        try {
            Date inputDate = inputDateFormat.parse(date);
            String formattedDate = outputDateFormat.format(inputDate);
            date = formattedDate;
            System.out.println("Formatted Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}