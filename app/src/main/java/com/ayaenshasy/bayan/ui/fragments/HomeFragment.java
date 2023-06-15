package com.ayaenshasy.bayan.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.databinding.AddNewAttendanceLayoutBinding;
import com.ayaenshasy.bayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.bayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Attendance;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {
    List<Student> students = new ArrayList<>();
    FragmentHomeBinding binding;
    private StudentAdapter adapter;
    private DatabaseReference studentsRef;
    private View progressView;


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
        return view;
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

            // Retrieve teacher ID from Firebase
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("users");
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String teacherId = childSnapshot.getKey();  // Assuming teacher ID is the key of each child node
                        if (teacherId != null) {
                            // Query students based on teacher ID
                            DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
                            Query query = studentsRef.orderByChild("responsible_id").equalTo(teacherId);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                                        Student student = studentSnapshot.getValue(Student.class);
                                        students.add(student);
                                    }
                                    binding.progressBar3.setVisibility(View.GONE);
                                    adapter.setStudents(students);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle any errors
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors
                }
            });
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");

            Query query = usersRef.orderByChild("responsible_id").equalTo(currentUser.getId());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the user data
                        User user = userSnapshot.getValue(User.class);

                        // Do something with the user data
                        // For example, print the user's name
                        System.out.println(user.getName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that occur
                    System.out.println("Error: " + databaseError.getMessage());
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void setMainUserData() {
        binding.userName.setText(currentUser.getName());
        binding.userRole.setText(role_name);
        binding.identifier.setText(currentUser.getName() + "");
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

                // Show the progress view
                showProgress(true, progressView);

                // Save the data to Firebase Realtime Database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("attendance");
                String attendanceId = databaseReference.push().getKey();
                String currentDate = getCurrentDate();
//                List<String> islamicPrayers = Arrays.asList("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha");
                Map<String, Boolean> islamicPrayers = new HashMap<>();
                islamicPrayers.put("Fajr", false);
                islamicPrayers.put("Dhuhr", false);
                islamicPrayers.put("Asr", false);
                islamicPrayers.put("Maghrib", false);
                islamicPrayers.put("Isha", false);
                // Create an attendance object
                Attendance attendance = new Attendance(attendanceId, currentDate, planToday, todayPercentage, repeated, planYesterday,
                        yesterdayPercentage, repeatedYesterday, islamicPrayers, planTomorrow);

                // Save the attendance object to the database
                databaseReference.child(currentDate).child(student.getId()).setValue(attendance)
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
            });

            bottomSheetDialog.show();

            // Animate the bottom sheet dialog
            animateBottomSheet(bottomSheetDialog);
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

            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("users");
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    students.clear();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String teacherId = childSnapshot.getKey();
                        if (teacherId != null) {
                            DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
                            Query studentsQuery = studentsRef.orderByChild("responsible_id").equalTo(teacherId);
                            studentsQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                                        Student student = studentSnapshot.getValue(Student.class);
                                        if (student != null && student.getName().contains(query)) {
                                            students.add(student);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    binding.progressBar3.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


}

