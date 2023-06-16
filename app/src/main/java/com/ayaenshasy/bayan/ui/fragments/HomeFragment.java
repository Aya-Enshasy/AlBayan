package com.ayaenshasy.bayan.ui.fragments;

import static android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

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
import com.ayaenshasy.bayan.model.PrayerTimingsClass;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.TimeUpdater;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.adapter.UserAdapter;
import com.ayaenshasy.bayan.databinding.AddNewAttendanceLayoutBinding;
import com.ayaenshasy.bayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Attendance;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    List<User> users = new ArrayList<>();
    FragmentHomeBinding binding;
    private StudentAdapter adapter;
    private DatabaseReference studentsRef;
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
                    Constant.showBottomSheet(student,context);
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
                            queryStudentsByTeacherId(teacherId);
                            Log.e("m,f teacherId", teacherId);
                            break; // Break after finding the teacher ID
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors
                }
            });
        } else {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            UserAdapter userAdapter = new UserAdapter(users, context);
            binding.rvUser.setAdapter(userAdapter);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");

            Query query = usersRef.orderByChild("responsible_id").equalTo(currentUser.getId());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        users.add(user);

                        System.out.println(user.getName());
                    }
                    userAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that occur
                    System.out.println("Error: " + databaseError.getMessage());
                }
            });

        }
    }

    private void queryStudentsByTeacherId(String teacherId) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        Query query = studentsRef.orderByChild("responsible_id").equalTo(currentUser.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                students.clear(); // Clear the list before adding new data
                String currentDate = Constant.getCurrentDate(); // Replace with the method to get the current date

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    String studentId = student.getId();

                    DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance")
                            .child(currentDate)
                            .child(studentId);

                    attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                            boolean isAttendanceMarkedToday = attendanceSnapshot.exists();
                            student.setChecked(isAttendanceMarkedToday);
                            students.add(student);

                            // Notify the adapter when all students are processed
                            if (students.size() == dataSnapshot.getChildrenCount()) {
                                binding.progressBar3.setVisibility(View.GONE);
                                adapter.setStudents(students);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
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



    private void searchByName(String query) {
        if (role == Role.TEACHER) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                @Override
                public void sendData(Student student) {
                    Constant.showBottomSheet(student,context);
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

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // Handle VIBRATE permission request result
        }
    }

    // Handle activity result for battery optimization permission request
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
//            // Handle IGNORE_BATTERY_OPTIMIZATIONS permission request result
//        }
//    }
}

