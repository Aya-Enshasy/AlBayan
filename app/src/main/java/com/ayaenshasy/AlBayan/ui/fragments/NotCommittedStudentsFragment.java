package com.ayaenshasy.AlBayan.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.StudentAdapter;
import com.ayaenshasy.AlBayan.databinding.FragmentCommittedStudentsBinding;
import com.ayaenshasy.AlBayan.databinding.FragmentNotCommittedStudentsBinding;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotCommittedStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotCommittedStudentsFragment extends BaseFragment {
    FragmentNotCommittedStudentsBinding binding;
    private StudentAdapter adapter;
    List<Student> students = new ArrayList<>();

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
        setRvData();
        sendNotification();
        return view;
    }

    private void sendNotification() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSubmitDialog();
            }
        });


    }

//    private void sendNotificationToParent(String fcmToken, String message) {
//        try {
//            URL url = new URL("https://fcm.googleapis.com/fcm/send");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestProperty("Authorization", "Bearer " + YOUR_SERVER_KEY);
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//
//            JSONObject notification = new JSONObject();
//            notification.put("title", "Notification Title");
//            notification.put("body", message);
//
//            JSONObject data = new JSONObject();
//            data.put("key1", "value1");
//            data.put("key2", "value2");
//
//            JSONObject jsonPayload = new JSONObject();
//            jsonPayload.put("to", fcmToken);
//            jsonPayload.put("priority", "high");
//            jsonPayload.put("notification", notification);
//            jsonPayload.put("data", data);
//
//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(jsonPayload.toString().getBytes("UTF-8"));
//            outputStream.close();
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                // Notification sent successfully
//                Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show();
//
//            } else {
//                Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show();
//
//                // Failed to send notification
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private void saveNotificationDataToFirestore( String parentId, String title,String message,String studentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationCollection = db.collection("notification");

        // Create a new document in the "notification" collection
        DocumentReference notificationDocRef = notificationCollection.document();

        // Set the necessary fields in the notification document
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("parentId", parentId);
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("student_id", studentId);
        notificationData.put("timestamp", FieldValue.serverTimestamp());

        notificationDocRef.set(notificationData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Notification data saved successfully
                        Toast.makeText(context, "Notification data saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save notification data
                        Toast.makeText(context, " Failed to save notification data", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void showSubmitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_submit_operation, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Apply animation to the dialog
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // Handle submit button click
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!students.isEmpty()) {
                    for (int i = 0; i < students.size(); i++) {
                        Student student=students.get(i);
                        String parentId = student.getParentId();
                        // Retrieve the parent's FCM token from Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference parentsCollection = db.collection("parent");
                        DocumentReference parentDocRef = parentsCollection.document(parentId);

                        parentDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Retrieve the parent's FCM token
                                    String fcmToken = documentSnapshot.getString("fcmToken");
                                    String msg = "عزيزي الوالد، نود إبلاغكم أن طفلكم " + student.getName() + " تم تسجيل غيابه اليوم.";
                                    String title = "إشعار غياب ";
                                    if (fcmToken != null) {
//                                        String studentName = "جون دو";

//                                        Notification notification = Notification.builder()
//                                                .setTitle("إشعار الحضور")
//                                                .setBody("عزيزي الوالد، نود إبلاغكم أن طفلكم " + studentName + " تم تسجيل غيابه اليوم.")
//                                                .build();

                                        // Build and send the notification using the FCM token
//                                        sendNotificationToParent(fcmToken, "Your notification message");

                                        // Save the notification data in the "notification" table


                                    }
                                    saveNotificationDataToFirestore(parentId, title, msg,student.getId());
                                    dialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                                dialog.dismiss();
                            }
                        });
                    }
                }

            }
        });

        dialog.show();
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
                            });
                            binding.rvUser.setAdapter(adapter);

                            if (!isAttendanceMarkedToday
                                    || attendance == null
                                    || attendance.getPlanToday() == null
                                    || attendance.getPlanToday().isEmpty()) {

                                student.setChecked(false);
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