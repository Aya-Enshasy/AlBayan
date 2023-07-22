package com.ayaenshasy.AlBayan.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.ayaenshasy.AlBayan.AdsActivity;
import com.ayaenshasy.AlBayan.NotificationsActivity;
import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.database.DatabaseClient;
import com.ayaenshasy.AlBayan.database.ExamDao;
import com.ayaenshasy.AlBayan.dialogs.LogoutDialog;
import com.ayaenshasy.AlBayan.model.Attendance;
import com.ayaenshasy.AlBayan.model.Exam;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.ui.activities.DailyHistoryActivity;
import com.ayaenshasy.AlBayan.ui.activities.EditUserProfileActivity;
import com.ayaenshasy.AlBayan.ui.activities.SupportActivity;
import com.ayaenshasy.AlBayan.databinding.FragmentSettingsBinding;
import com.ayaenshasy.AlBayan.ui.activities.AddUserActivity;
import com.ayaenshasy.AlBayan.ui.activities.LoginActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class SettingsFragment extends BaseFragment implements LogoutDialog.LogoutDialogListener {
    FragmentSettingsBinding binding;
    public Dialog add_video_dialog;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        openActivities();
        getData();
        dataCount();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        binding.userName.setText(currentUser.getName());
        if (Objects.equals(role_name, Role.TEACHER.toString()))
            binding.userRole.setText("محفظ");
        if (Objects.equals(role_name, Role.SUPERVISOR.toString()))
            binding.userRole.setText("مشرف عام ");
        if (Objects.equals(role_name, Role.ADMIN.toString()))
            binding.userRole.setText("مشرف ");
        else binding.userRole.setText(" اهلا وسهلا عزيزنا ");

        binding.identifier.setText(currentUser.getName() + "");
        Glide.with(context).load(currentUser.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true).into(binding.userImage);


        if (currentUser.getRole().name().equals("TEACHER")) {
            binding.day.setVisibility(View.VISIBLE);
        } else {
            binding.day.setVisibility(View.GONE);
        }

    }

    private void openActivities() {
        binding.addUser.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), AddUserActivity.class));
        });

        binding.notification.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        });
        binding.ads.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), AdsActivity.class));
        });

        binding.editProfile.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), EditUserProfileActivity.class));
        });

        binding.support.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), SupportActivity.class));
        });

        binding.day.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), DailyHistoryActivity.class));
        });

        binding.addVideo.setOnClickListener(View -> {
            VideoDialog();
        });
        binding.uploadData.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                if (Integer.parseInt(binding.number.getText().toString()) > 0) {
                    showSubmitDialog();
                } else {
                    Toast.makeText(context, "لا يوجد هنا بيانات لرفعها", Toast.LENGTH_SHORT).show();
                }
            } else {
                launchInternetLottieDialog(getActivity());
            }
        });
        binding.logOut.setOnClickListener(View -> {
            showLogoutDialog();

        });
    }

    AlertDialog dialog;
    private ProgressBar progressBar;

    public void launchInternetLottieDialog(Activity activity) {
        if (activity != null) {
            Context context = activity.getApplicationContext();

            Button button = new Button(context);
            button.setText("Retry");
            button.setTextColor(Color.WHITE);
            button.setAllCaps(false);
            int purpleColor = ContextCompat.getColor(context, R.color.orange);
            button.setBackgroundTintList(ColorStateList.valueOf(purpleColor));

            LottieDialog dialog = new LottieDialog(activity)
                    .setAnimation(R.raw.no_internet)
                    .setAutoPlayAnimation(true)
                    .setAnimationRepeatCount(LottieDialog.INFINITE)
                    .setMessage("You have no internet connection")
                    .addActionButton(button);

            button.setOnClickListener(view -> {
                dialog.dismiss();
                if (!isNetworkAvailable()) {
                    launchInternetLottieDialog(activity);
                }
            });

            dialog.show();
        }
    }

    private void showSubmitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_submit_operation, null);
        builder.setView(dialogView);
        dialog = builder.create();

        // Get reference to ProgressBar
        progressBar = dialogView.findViewById(R.id.progressBar);
        TextView txt = dialogView.findViewById(R.id.txt);

        // Apply animation to the dialog
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        // Handle submit button click
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                txt.setText("العملية قيد التنفيذ ...");
                uploadAttendance();
                uploadExams();
            }
        });
    }

    private void uploadAttendance() {
        progressBar.setVisibility(View.VISIBLE);

        AsyncTask.execute(() -> {
            List<Attendance> attendanceList = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .attendanceDao()
                    .readAll();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference attendanceCollection = db.collection("attendance");

            for (int i = 0; i < attendanceList.size(); i++) {
                Attendance attendance = attendanceList.get(i);

                DocumentReference attendanceRef = attendanceCollection
                        .document(attendance.getId())
                        .collection("records")
                        .document(attendance.getDate());

                Map<String, Object> attendanceData = new HashMap<>();
                attendanceData.put("attendanceId", currentUser.getId());
                attendanceData.put("currentDate", attendance.getDate());
                attendanceData.put("planToday", attendance.getPlanToday());
                attendanceData.put("todayPercentage", attendance.getTodayPercentage());
                attendanceData.put("repeated", attendance.getRepeated());
                attendanceData.put("planYesterday", attendance.getPlanYesterday());
                attendanceData.put("yesterdayPercentage", attendance.getYesterdayPercentage());
                attendanceData.put("repeatedYesterday", attendance.getRepeatedYesterday());
                attendanceData.put("planTomorrow", attendance.getPlanTomorrow());
                attendanceData.put("notes", attendance.getNotes());

                Map<String, Boolean> islamicPrayers = attendance.getIslamicPrayers();
                if (islamicPrayers != null) {
                    attendanceData.put("islamicPrayers", islamicPrayers);
                }

                if (isParent)
                    attendanceData.put("rateParent", attendance.getRateParent());
                else
                    attendanceData.put("rateTeacher", attendance.getRateTeacher());

                attendanceRef.set(attendanceData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            // Data saved successfully
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "بيانات الحضور تم حفظها بنجاح", Toast.LENGTH_SHORT).show();
                                // Delete the uploaded record from the local database
                                deleteAttendanceFromDatabase(attendance);
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Failed to save data
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnCompleteListener(task -> {
                            // Hide the progress bar
                            progressBar.setVisibility(View.GONE);
                            // Dismiss the dialog
                            dialog.dismiss();
                        });
            }
        });
    }

    private void uploadExams() {
        AsyncTask.execute(() -> {
            List<Exam> examList = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .examDao()
                    .getAllExams();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference attendanceCollection = db.collection("exams");

            for (int i = 0; i < examList.size(); i++) {
                Exam exam = examList.get(i);
                DocumentReference document = attendanceCollection
                        .document(exam.getStudentId())
                        .collection("records").document();
//                        .document(exam.getDate());
                Map<String, Object> examData = new HashMap<>();
                examData.put("examId", exam.getId());
                examData.put("studentId", exam.getStudentId());
                examData.put("name", exam.getName());
                examData.put("degree", exam.getDegree());
                examData.put("mosque", exam.getMosque());
                examData.put("date", exam.getDate());
                examData.put("shackName", exam.getShack_name());
                examData.put("examType", exam.getExam_type());
                examData.put("image", exam.getImage());
                document.set(examData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            // Data saved successfully
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "بيانات الاختبارات تم حفظها بنجاح", Toast.LENGTH_SHORT).show();
                                // Delete the uploaded record from the local database
                                deleteExamFromDatabase(exam);
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Failed to save data
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnCompleteListener(task -> {
                            // Hide the progress bar
                            progressBar.setVisibility(View.GONE);
                            // Dismiss the dialog
                            dialog.dismiss();
                        });
            }
        });
    }


    private void deleteExamFromDatabase(Exam exam) {
        AsyncTask.execute(() -> {
            DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .examDao()
                    .delete(exam);
        });
        dataCount();
    }

    private void deleteAttendanceFromDatabase(Attendance attendance) {
        AsyncTask.execute(() -> {
            DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .attendanceDao()
                    .delete(attendance);
        });
        dataCount();
    }

    private void dataCount() {
        AsyncTask.execute(() -> {
            int testItemList = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .attendanceDao()
                    .getCount();
            int testItemList2 = DatabaseClient.getInstance(requireContext())
                    .getAppDatabase()
                    .examDao()
                    .getCount();

            // Update the UI on the main thread
            requireActivity().runOnUiThread(() -> binding.number.setText(String.valueOf(testItemList + testItemList2)));
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void showLogoutDialog() {
        LogoutDialog dialog = new LogoutDialog();
        dialog.setListener(this);
        dialog.show(getActivity().getSupportFragmentManager(), "logout_dialog");
    }

    @Override
    public void onLogoutConfirmed() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
        preferences.clearPreferences();
    }

    @Override
    public void onLogoutCancelled() {
    }

    public void VideoDialog() {
        add_video_dialog = new Dialog(getActivity());

        add_video_dialog.setContentView(R.layout.add_video_dialog);
        add_video_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        add_video_dialog.getWindow().setBackgroundDrawableResource(R.drawable.input_background);
        EditText et_url = add_video_dialog.findViewById(R.id.url);
        Button add = add_video_dialog.findViewById(R.id.add);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference videoRef = database.getReference("videos");

        add.setOnClickListener(View -> {
            String url = et_url.getText().toString();
            String videoKey = videoRef.push().getKey();
            videoRef.child(videoKey).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "تم اضافة بنجاح", Toast.LENGTH_SHORT).show();
                    add_video_dialog.dismiss();
                }
            });
        });

        add_video_dialog.show();


    }
}