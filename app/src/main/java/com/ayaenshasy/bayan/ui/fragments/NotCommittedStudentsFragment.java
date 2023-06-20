package com.ayaenshasy.bayan.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.StudentAdapter;
import com.ayaenshasy.bayan.adapter.UserAdapter;
import com.ayaenshasy.bayan.databinding.FragmentCommittedStudentsBinding;
import com.ayaenshasy.bayan.databinding.FragmentNotCommittedStudentsBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.utils.Constant;
import com.ayaenshasy.bayan.utils.TimeUpdater;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotCommittedStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotCommittedStudentsFragment extends BaseFragment {
    FragmentNotCommittedStudentsBinding binding;
    private StudentAdapter adapter;
    List<Student> students = new ArrayList<>();
    List<User> users = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotCommittedStudentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotCommittedStudentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotCommittedStudentsFragment newInstance(String param1, String param2) {
        NotCommittedStudentsFragment fragment = new NotCommittedStudentsFragment();
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
        binding = FragmentNotCommittedStudentsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        setRvData();
        getData();
        return view;
    }

    private void getData() {
        if (role == Role.TEACHER) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                @Override
                public void sendData(Student student) {
//                    showBottomSheet(student);
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

    private void setRvData() {
        if (role == Role.TEACHER) {
            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                @Override
                public void sendData(Student student) {
                    Constant.showBottomSheet(student, requireContext());
                }
            });
            binding.rvUser.setAdapter(adapter);

            // Retrieve teacher ID from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersCollection = db.collection("users");

            usersCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String teacherId = document.getId(); // Assuming teacher ID is the document ID
                            if (teacherId != null) {
                                queryStudentsByTeacherId(teacherId);
                                break; // Break after finding the teacher ID
                            }
                        }
                    } else {
                        // Handle any errors
                    }
                }
            });
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersCollection = db.collection("users");

            Query query = usersCollection.whereEqualTo("responsible_id", currentUser.getId());

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve the user data
                            User user = document.toObject(User.class);

                            // Do something with the user data
                            // For example, print the user's name
                            System.out.println(user.getName());
                        }
                    } else {
                        // Handle any errors that occur
                        System.out.println("Error: " + task.getException().getMessage());
                    }
                }
            });

        }
    }

    private void queryStudentsByTeacherId(String teacherId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentsCollection = db.collection("students");

        Query query = studentsCollection.whereEqualTo("responsible_id", currentUser.getId());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    students.clear(); // Clear the list before adding new data
                    String currentDate = getCurrentDate(); // Replace with the method to get the current date

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Student student = document.toObject(Student.class);
                        String studentId = student.getId();

                        DocumentReference attendanceRef = db.collection("attendance")
                                .document(currentDate)
                                .collection(studentId)
                                .document(studentId);

                        attendanceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> attendanceTask) {
                                if (attendanceTask.isSuccessful()) {
                                    DocumentSnapshot attendanceSnapshot = attendanceTask.getResult();
                                    boolean isAttendanceMarkedToday = attendanceSnapshot.exists();
                                    if (!isAttendanceMarkedToday) {
                                        student.setChecked(isAttendanceMarkedToday);
                                        students.add(student);
                                    }

                                    // Notify the adapter when all students are processed
                                    if (students.size() == task.getResult().size()) {
                                        binding.progressBar4.setVisibility(View.GONE);
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



    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}