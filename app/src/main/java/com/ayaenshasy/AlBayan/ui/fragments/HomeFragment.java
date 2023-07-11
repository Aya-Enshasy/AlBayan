package com.ayaenshasy.AlBayan.ui.fragments;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.NotificationsActivity;
import com.ayaenshasy.AlBayan.PrayerService;
import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.listeners.DeleteListener;
import com.ayaenshasy.AlBayan.utils.Constant;
import com.ayaenshasy.AlBayan.utils.TimeUpdater;
import com.ayaenshasy.AlBayan.adapter.StudentAdapter;
import com.ayaenshasy.AlBayan.adapter.UserAdapter;
import com.ayaenshasy.AlBayan.databinding.AddNewAttendanceLayoutBinding;
import com.ayaenshasy.AlBayan.databinding.FragmentHomeBinding;
import com.ayaenshasy.AlBayan.listeners.DataListener;
import com.ayaenshasy.AlBayan.model.Attendance;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.user.Student;
import com.ayaenshasy.AlBayan.model.user.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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


public class HomeFragment extends BaseFragment {
    List<Student> students = new ArrayList<>();
    List<User> users = new ArrayList<>();
    FragmentHomeBinding binding;
    private StudentAdapter adapter;
    private DatabaseReference studentsRef;
    private View progressView;
    UserAdapter userAdapter;
    Attendance attendance;
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
//                searchByName(newText);
                return false;
            }
        });
        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(context, "close", Toast.LENGTH_SHORT).show();
                setData();
                return false;
            }
        });
        binding.searchView.setVisibility(View.GONE);
        getDate();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.VIBRATE}, REQUEST_VIBRATE_PERMISSION);
        }


        Intent serviceIntent = new Intent(context, PrayerService.class);
        context.startService(serviceIntent);

        binding.notification.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        });

        return view;
    }


    @SuppressLint("SetTextI18n")
    private void getDate() {
        TimeUpdater timeUpdater = new TimeUpdater(binding.time, getActivity());
        timeUpdater.startUpdatingTime();
    }

    private void setData() {
        setMainUserData();
        showWaitingImage();
        setRvData();
    }

    private void setRvData() {

        if (role == Role.TEACHER && !isParent) {
            queryStudentsByTeacherId();
        }

        else if (isParent) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            userAdapter = new UserAdapter(users, context, new DeleteListener() {
                @Override
                public void onClick(String name, int pos) {
//                    showDeleteConfirmationDialog(name,pos);
                }
            });
            binding.rvUser.setAdapter(userAdapter);
            queryStudentsByParentId(currentUser.getId());
        }

        else {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            userAdapter = new UserAdapter(users, context, new DeleteListener() {
                @Override
                public void onClick(String name, int pos) {
                    showDeleteConfirmationDialog(name,pos,"users");
                }
            });
            binding.rvUser.setAdapter(userAdapter);

            CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

            com.google.firebase.firestore.Query query = usersRef.whereNotEqualTo("role", "ADMIN");

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            users.add(user);

                        }
//                        binding.progressBar3.setVisibility(View.GONE);
                        dismissWaitingImage();

                        userAdapter.notifyDataSetChanged();
                    } else {
                        // Handle any errors
                        System.out.println("Error: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void queryStudentsByParentId(String teacherId) {
        CollectionReference studentsRef = FirebaseFirestore.getInstance().collection("students");
        com.google.firebase.firestore.Query query = studentsRef.whereEqualTo("parentId", currentUser.getId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        users.add(user);

                    }
//                    binding.progressBar3.setVisibility(View.GONE);
                    dismissWaitingImage();
                    userAdapter.notifyDataSetChanged();
                } else {
                    // Handle any errors
                    System.out.println("Error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void queryStudentsByTeacherId() {
        CollectionReference studentsRef = FirebaseFirestore.getInstance().collection("students");
        com.google.firebase.firestore.Query query = studentsRef.whereEqualTo("responsible_id", currentUser.getId());
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
                                .document(studentId)
                                .collection("records")
                                .document(currentDate);

                        attendanceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean isAttendanceMarkedToday = task.getResult().exists();
                                    Attendance attendance = task.getResult().toObject(Attendance.class);
                                    binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
                                    adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                                        @Override
                                        public void sendData(Student student) {
                                            Constant.showBottomSheet(student, adapter, currentUser, getContext());
                                        }
                                    }, new DeleteListener() {
                                        @Override
                                        public void onClick(String name, int pos) {
                                            showDeleteConfirmationDialog(name,pos,"students");
                                        }
                                    });
                                    binding.rvUser.setAdapter(adapter);

                                    if (isAttendanceMarkedToday && attendance != null && attendance.getPlanToday() != null && !attendance.getPlanToday().isEmpty())
                                        student.setChecked(true);
                                    else
                                        student.setChecked(false);

                                    students.add(student);

                                    // Notify the adapter when all students are processed
                                    if (students.size() == numStudents) {
//                                        binding.progressBar3.setVisibility(View.GONE);
                                        adapter.setStudents(students);
                                        dismissWaitingImage();
                                    }else
                                        showNoDataImage();
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

    private void getLastAttendance(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendanceCollection = db.collection("attendance");

        attendanceCollection.document(userId)
                .collection("records")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            attendance = document.toObject(Attendance.class);
                        } else {
                            Log.e("TAG", "No attendance records found");
                        }
                    } else {
                        Log.e("TAG", "Failed to retrieve attendance records: " + task.getException());
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setMainUserData() {
        if (role == Role.ADMIN) {
            binding.textView9.setText("جميع المحفظين");
        } else if (role == Role.SUPERVISOR) {
            binding.textView9.setText("جميع المشرفين");
        } else if (role == Role.TEACHER) {
            binding.textView9.setText("جميع طلابك ");
        } else if (role == Role.BIG_BOSS) {
            binding.textView9.setText("جميع المشرفين");
        } else binding.textView9.setText("جميع ابناءك  ");

        binding.userName.setText(currentUser.getName());
        binding.userRole.setText(role_name);
        binding.identifier.setText(currentUser.getId() + "");
        Glide.with(context).load(currentUser.getImage()).placeholder(R.drawable.ic_user_circle_svgrepo_com).diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true).into(binding.userImage);
    }


    private void searchByName(String query) {
        if (role == Role.TEACHER) {
            students.clear();
            CollectionReference studentsCollection = FirebaseFirestore.getInstance().collection("students");
            Query studentsQuery = studentsCollection.whereEqualTo("responsible_id", currentUser.getId())
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
//                    binding.progressBar3.setVisibility(View.GONE);
                    dismissWaitingImage();

                }
            });
        }
    }

     @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // Handle VIBRATE permission request result
        }
    }

    private void showWaitingImage() {
        binding.lottieImg.setAnimation(R.raw.waiting_sand);
        binding.lottieImg.loop(true);
        binding.lottieImg.playAnimation();
        binding.lottieImg.setVisibility(View.VISIBLE);
        binding.rvUser.setVisibility(View.GONE);
    }

    private void showNoDataImage() {
        binding.lottieImg.setAnimation(R.raw.waiting_sand);
        binding.lottieImg.loop(true);
        binding.lottieImg.playAnimation();
        binding.lottieImg.setVisibility(View.VISIBLE);
        binding.rvUser.setVisibility(View.GONE);
    }

    private void dismissWaitingImage() {
        binding.lottieImg.setVisibility(View.GONE);
        binding.rvUser.setVisibility(View.VISIBLE);
    }

    private void showDeleteConfirmationDialog(String userId, int pos,String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("حذف المستخدم");
        builder.setMessage("هل متاكد من عملية الحذف ؟");
        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                CollectionReference usersRef = FirebaseFirestore.getInstance().collection(path);
                DocumentReference userDocRef = usersRef.document(userId);
                userDocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            queryStudentsByTeacherId();
                            setData();
                            Toast.makeText(context, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle any errors
                            System.out.println("Error: " + task.getException().getMessage());
                        }
                    }
                });

//                deleteItem(position);
            }
        });
        builder.setNegativeButton("لا", null);
        builder.show();
    }

}


