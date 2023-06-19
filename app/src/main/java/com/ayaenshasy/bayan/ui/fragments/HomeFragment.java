package com.ayaenshasy.bayan.ui.fragments;

import static android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.ayaenshasy.bayan.PrayerService;
import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.adapter.UserAdapter;
import com.ayaenshasy.bayan.databinding.AddNewAttendanceLayoutBinding;
import com.ayaenshasy.bayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Attendance;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.TimeUpdater;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {
    List<Student> students = new ArrayList<>();
    List<User> users = new ArrayList<>();
    FragmentHomeBinding binding;
    private StudentAdapter adapter;
    private DatabaseReference studentsRef;
    private View progressView;
    private static final int REQUEST_VIBRATE_PERMISSION = 0;
    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Initialize Firebase database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        studentsRef = firebaseDatabase.getReference("students");

        setData();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByName(newText);
                return false;
            }
        });
        getDate();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.VIBRATE}, REQUEST_VIBRATE_PERMISSION);
        }


        Intent serviceIntent = new Intent(context, PrayerService.class);
        context.startService(serviceIntent);

        Intent serviceIntent1 = new Intent(context, PrayerService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(serviceIntent1);
//        }
        return view;
    }


    @SuppressLint("SetTextI18n")
    private void getDate() {

        TimeUpdater timeUpdater = new TimeUpdater(binding.time, getActivity());
        timeUpdater.startUpdatingTime();

    }

    private void setData() {
        setMainUserData();
        setRvData();
    }

    private void setRvData() {
        if (role == Role.TEACHER) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                @Override
                public void sendData(Student student) {
                    showBottomSheet(student);
                }
            });
            binding.rvUser.setAdapter(adapter);

            // Retrieve teacher ID from Firestore
            CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
            usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String teacherId = document.getId(); // Assuming teacher ID is the document ID
                            if (teacherId != null) {
                                queryStudentsByTeacherId(teacherId);
                                Log.e("m,f teacherId", teacherId);
                                break; // Break after finding the teacher ID
                            }
                        }
                    } else {
                        // Handle any errors
                    }
                }
            });
        } else {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            UserAdapter userAdapter = new UserAdapter(users, context);
            binding.rvUser.setAdapter(userAdapter);

            CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

            Query query = usersRef.whereEqualTo("responsible_id", currentUser.getId());

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            users.add(user);
                            System.out.println(user.getName());
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        // Handle any errors
                        System.out.println("Error: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void queryStudentsByTeacherId(String teacherId) {
        Toast.makeText(context, teacherId+"", Toast.LENGTH_SHORT).show();
        CollectionReference studentsRef = FirebaseFirestore.getInstance().collection("students");
        Query query = studentsRef.whereEqualTo("responsible_id", currentUser.getId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    students.clear(); // Clear the list before adding new data
                    String currentDate = getCurrentDate(); // Replace with the method to get the current date

                    QuerySnapshot querySnapshot = task.getResult(); // Get the QuerySnapshot
                    int numStudents = querySnapshot.size(); // Get the number of documents returned

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Student student = document.toObject(Student.class);
                        String studentId = student.getId();

                        DocumentReference attendanceRef = FirebaseFirestore.getInstance().collection("attendance")
                                .document(currentDate)
                                .collection(studentId)
                                .document("data");

                        attendanceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean isAttendanceMarkedToday = task.getResult().exists();
                                    student.setChecked(isAttendanceMarkedToday);
                                    students.add(student);

                                    // Notify the adapter when all students are processed
                                    if (students.size() == numStudents) {
                                        binding.progressBar3.setVisibility(View.GONE);
                                        adapter.setStudents(students);
                                    }
                                } else {
                                    // Handle any errors
                                }
                            }
                        });
                    }
                } else {
                    // Handle any errors
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setMainUserData() {
        if (role == Role.ADMIN) {
            binding.textView9.setText("جميع المحفظين");
        }else  if (role == Role.BIG_BOSS) {
            binding.textView9.setText("جميع المشرفين");
        }
        binding.userName.setText(currentUser.getName());
        binding.userRole.setText(role_name);
        binding.identifier.setText(currentUser.getId() + "");
        Glide.with(context).load(currentUser.getImage()).placeholder(R.drawable.ic_user_circle_svgrepo_com).diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true).into(binding.userImage);
    }

    private void showBottomSheet(Student student) {
        // Check if the fragment is attached to the activity and the context is not null
        if (isAdded() && context != null) {
            // Create and show the bottom sheet
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            AddNewAttendanceLayoutBinding bottomSheetBinding = AddNewAttendanceLayoutBinding.inflate(LayoutInflater.from(context));
            bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());

            // Get references to the views
            EditText etPlanToday = bottomSheetBinding.etPlanToday;
            EditText etTodayPercentage = bottomSheetBinding.etTodayPercentage;
            EditText etRepeated = bottomSheetBinding.etRepeated;
            EditText etPlanYesterday = bottomSheetBinding.etPlanYesterday;
            EditText etYesterdayPercentage = bottomSheetBinding.etYesterdayPercentage;
            EditText etRepeatedYesterday = bottomSheetBinding.etRepeatedYesterday;
            EditText etPlanTomorrow = bottomSheetBinding.etPlanTomorrow;
            Button btnSave = bottomSheetBinding.btnSave;
            ProgressBar progressView = bottomSheetBinding.progressBar;

            // Set click listener for the save button
            btnSave.setOnClickListener(v -> {
                // Get the values entered by the user
                //today
                String planToday = etPlanToday.getText().toString().trim();
                String todayPercentage = etTodayPercentage.getText().toString().trim();
                String repeated = etRepeated.getText().toString().trim();
                //yesterday
                String planYesterday = etPlanYesterday.getText().toString().trim();
                String yesterdayPercentage = etYesterdayPercentage.getText().toString().trim();
                String repeatedYesterday = etRepeatedYesterday.getText().toString().trim();
                //Tomorrow
                String planTomorrow = etPlanTomorrow.getText().toString().trim();

                // Validate the input
                if (validateInput(planToday, todayPercentage, repeated, planYesterday, yesterdayPercentage, repeatedYesterday, planTomorrow,bottomSheetBinding)) {
                    // Show the progress view
                    showProgress(true, progressView);

                    // Save the data to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String attendanceId = UUID.randomUUID().toString();
                    String currentDate = getCurrentDate();

                    // Create an attendance object
                    Attendance attendance = new Attendance(attendanceId, currentDate, planToday, todayPercentage, repeated, planYesterday,
                            yesterdayPercentage, repeatedYesterday, planTomorrow);

                    // Save the attendance object to Firestore
                    CollectionReference attendanceRef = db.collection("attendance");
                    attendanceRef.document(currentDate)
                            .collection(student.getId())
                            .document(attendanceId)
                            .set(attendance)
                            .addOnSuccessListener(aVoid -> {
                                // Data saved successfully
                                Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                // Failed to save data
                                Toast.makeText(context, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnCompleteListener(task -> {
                                // Hide the progress view
                                showProgress(false, progressView);
                            });
                }
            });

            bottomSheetDialog.show();

            // Animate the bottom sheet dialog
            animateBottomSheet(bottomSheetDialog);
        }
    }

    private boolean validateInput(String planToday, String todayPercentage, String repeated,
                                  String planYesterday, String yesterdayPercentage, String repeatedYesterday,
                                  String planTomorrow, AddNewAttendanceLayoutBinding bottomSheetBinding) {
        // Perform your validation logic here
        if (planToday.isEmpty()) {
            bottomSheetBinding.etPlanToday.setError("أدخل الخطة لليوم");
            bottomSheetBinding.etPlanToday.requestFocus();
            return false;
        }

        if (todayPercentage.isEmpty()) {
            bottomSheetBinding.etTodayPercentage.setError("أدخل النسبة المئوية لليوم");
            bottomSheetBinding.  etTodayPercentage.requestFocus();
            return false;
        } else {
            try {
                double percentage = Double.parseDouble(todayPercentage);
                if (percentage < 0 || percentage > 100) {
                    bottomSheetBinding.  etTodayPercentage.setError("الرجاء إدخال نسبة صحيحة بين 0 و 100");
                    bottomSheetBinding.   etTodayPercentage.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                bottomSheetBinding. etTodayPercentage.setError("الرجاء إدخال رقم صحيح للنسبة");
                bottomSheetBinding.  etTodayPercentage.requestFocus();
                return false;
            }
        }

        if (repeated.isEmpty()) {
            bottomSheetBinding. etRepeated.setError("أدخل عدد المرات المتكررة");
            bottomSheetBinding. etRepeated.requestFocus();
            return false;
        }

        if (planYesterday.isEmpty()) {
            bottomSheetBinding.  etPlanYesterday.setError("أدخل الخطة لليوم السابق");
            bottomSheetBinding. etPlanYesterday.requestFocus();
            return false;
        }

        if (yesterdayPercentage.isEmpty()) {
            bottomSheetBinding.  etYesterdayPercentage.setError("أدخل النسبة المئوية لليوم السابق");
            bottomSheetBinding.  etYesterdayPercentage.requestFocus();
            return false;
        } else {
            try {
                double percentage = Double.parseDouble(yesterdayPercentage);
                if (percentage < 0 || percentage > 100) {
                    bottomSheetBinding.  etYesterdayPercentage.setError("الرجاء إدخال نسبة صحيحة بين 0 و 100");
                    bottomSheetBinding.  etYesterdayPercentage.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                bottomSheetBinding. etYesterdayPercentage.setError("الرجاء إدخال رقم صحيح للنسبة");
                bottomSheetBinding. etYesterdayPercentage.requestFocus();
                return false;
            }
        }

        if (repeatedYesterday.isEmpty()) {
            bottomSheetBinding.  etRepeatedYesterday.setError("أدخل عدد المرات المتكررة لليوم السابق");
            bottomSheetBinding.   etRepeatedYesterday.requestFocus();
            return false;
        }

        if (planTomorrow.isEmpty()) {
            bottomSheetBinding. etPlanTomorrow.setError("أدخل الخطة لليوم القادم");
            bottomSheetBinding.   etPlanTomorrow.requestFocus();
            return false;
        }

        // Add more validation rules for all fields

        return true;
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

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private void searchByName(String query) {
        if (role == Role.TEACHER) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                @Override
                public void sendData(Student student) {
                    showBottomSheet(student);
                }
            });
            binding.rvUser.setAdapter(adapter);

            CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");
            Query teacherQuery = usersCollection.whereEqualTo("role", "teacher");
            teacherQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        students.clear();
                        for (QueryDocumentSnapshot teacherDocument : task.getResult()) {
                            String teacherId = teacherDocument.getId();
                            if (teacherId != null) {
                                CollectionReference studentsCollection = FirebaseFirestore.getInstance().collection("students");
                                Query studentsQuery = studentsCollection.whereEqualTo("responsible_id", teacherId)
                                        .whereGreaterThanOrEqualTo("name", query)
                                        .whereLessThanOrEqualTo("name", query + "\uf8ff");
                                studentsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot studentDocument : task.getResult()) {
                                                Student student = studentDocument.toObject(Student.class);
                                                students.add(student);
                                            }
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Log.d("TAG", "Error getting students: ", task.getException());
                                        }
                                        binding.progressBar3.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting teachers: ", task.getException());
                    }
                }
            });
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // Handle VIBRATE permission request result
        }
    }

}

