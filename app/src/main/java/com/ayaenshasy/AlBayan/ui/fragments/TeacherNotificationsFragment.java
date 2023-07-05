package com.ayaenshasy.AlBayan.ui.fragments;

import static com.ayaenshasy.AlBayan.utils.Constant.USER_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.MonthHistoryAdapter;
import com.ayaenshasy.AlBayan.adapter.NotificationAdapter;
import com.ayaenshasy.AlBayan.databinding.FragmentNotCommittedStudentsBinding;
import com.ayaenshasy.AlBayan.databinding.FragmentTeacherNotificationsBinding;
import com.ayaenshasy.AlBayan.model.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherNotificationsFragment extends BaseFragment {
    FragmentTeacherNotificationsBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TeacherNotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherNotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherNotificationsFragment newInstance(String param1, String param2) {
        TeacherNotificationsFragment fragment = new TeacherNotificationsFragment();
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

    ArrayList<Notification> notificationArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTeacherNotificationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getData();
        return view;
    }

    private void getData() {
        NotificationAdapter adapter = new NotificationAdapter(notificationArrayList, this.requireContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        binding.recyclerView.setAdapter(adapter);
// Assuming you have the `parentId` and `studentId` values
        String parentId = currentUser.getId();
        String studentId = getActivity().getIntent().getStringExtra(USER_ID);

// Get the Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

// Get the "notification" collection reference
        CollectionReference notificationCollection = firestore.collection("notification");

// Create a query to filter notifications for the parent and student
        Query query = notificationCollection
                .whereEqualTo("parentId", parentId)
                .whereEqualTo("student_id", studentId);

// Execute the query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Query execution successful
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        // Iterate through the query results
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Notification notification = document.toObject(Notification.class);
                            notificationArrayList.add(notification);

                            // Do something with the notification data
                            System.out.println("Title: " + notification.getTitle() + ", Message: " + notification.getMessage());
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // An error occurred while executing the query
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Handle the error
                        exception.printStackTrace();
                    }
                }
            }
        });

    }

}