package com.ayaenshasy.AlBayan.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.StudentAdapter;
import com.ayaenshasy.AlBayan.databinding.FragmentCommittedStudentsBinding;
import com.ayaenshasy.AlBayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.AlBayan.listeners.DataListener;
import com.ayaenshasy.AlBayan.model.Attendance;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.user.Student;
import com.ayaenshasy.AlBayan.model.user.User;
import com.ayaenshasy.AlBayan.utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
 * Use the {@link CommittedStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommittedStudentsFragment extends BaseFragment {
    FragmentCommittedStudentsBinding binding;
    private StudentAdapter adapter;
    List<Student> students = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommittedStudentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommittedStudentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommittedStudentsFragment newInstance(String param1, String param2) {
        CommittedStudentsFragment fragment = new CommittedStudentsFragment();
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
        binding = FragmentCommittedStudentsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setRvData();
        return view;
    }

    private void setRvData() {

        queryStudentsByTeacherId(currentUser.getId());

    }

    private void queryStudentsByTeacherId(String teacherId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentsCollection = db.collection("students");
        Query query = studentsCollection.whereEqualTo("responsible_id", teacherId);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle any errors
                    return;
                }

                students.clear(); // Clear the list before adding new data
                String currentDate = getCurrentDate(); // Replace with the method to get the current date

                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    Student student = documentSnapshot.toObject(Student.class);
                    String studentId = student.getId();

                    DocumentReference attendanceRef = db.collection("attendance")
                            .document(studentId)
                            .collection("records")
                            .document(currentDate);

                    attendanceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            boolean isAttendanceMarkedToday = documentSnapshot.exists();
                            Attendance attendance = documentSnapshot.toObject(Attendance.class);
                            binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

                            adapter = new StudentAdapter(students, context, new DataListener<Student>() {
                                @Override
                                public void sendData(Student student) {
                                    Constant.showBottomSheet(student,adapter,currentUser,getContext());
                                }
                            },null);
                            binding.rvUser.setAdapter(adapter);

                            if (isAttendanceMarkedToday && attendance != null && attendance.getPlanToday() != null && !attendance.getPlanToday().isEmpty()) {
                                student.setChecked(isAttendanceMarkedToday);
                                students.add(student);
                            }

                            // Notify the adapter when all students are processed
//                            if (students.size() == querySnapshot.size()) {
                            binding.progressBar4.setVisibility(View.GONE);
                            adapter.setStudents(students);
//                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                        }
                    });
                }
            }
        });
    }


}