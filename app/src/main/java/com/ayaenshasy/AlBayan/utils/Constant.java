package com.ayaenshasy.AlBayan.utils;

import static com.ayaenshasy.AlBayan.utils.MyApplication.getContext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.StudentAdapter;
import com.ayaenshasy.AlBayan.database.AppDatabase;
import com.ayaenshasy.AlBayan.database.AttendanceDao;
import com.ayaenshasy.AlBayan.database.DatabaseClient;
import com.ayaenshasy.AlBayan.databinding.AddNewAttendanceLayoutBinding;
import com.ayaenshasy.AlBayan.model.Attendance;
import com.ayaenshasy.AlBayan.model.user.Student;
import com.ayaenshasy.AlBayan.model.user.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Constant {
    public static String LOGIN = "LOGIN";
    public static String Remembrance_Id = "Remembrance_Id";
    public static String Remembrance_Name = "Remembrance_Name";
    public static String Remembrance_List = "Remembrance_List";
    public static String AYA_ID = "AYA_ID";
    public static String AYA_NAME = "AYA_NAME";
    public static String USER_ID = "USER_ID";
    public static String USER_ROLE = "USER_ROLE";
    public static String USER_NAME = "USER_NAME";
    public static String USER_IMAGE = "USER_IMAGE";
    public static String password = "password";
    public static int REQUEST_IMAGE_GALLERY = 1;
    public static int REQUEST_IMAGE_PERMISSION = 2;

    public static void showBottomSheet(Student student, StudentAdapter adapter, User currentUser, Context context) {
        if (context != null) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            AddNewAttendanceLayoutBinding bottomSheetBinding = AddNewAttendanceLayoutBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(bottomSheetBinding.getRoot());


            // Get references to the views
            TextView cancel = bottomSheetBinding.cancel;
            EditText etPlanToday = bottomSheetBinding.etPlanToday;
            EditText etTodayPercentage = bottomSheetBinding.etTodayPercentage;
            EditText etRepeated = bottomSheetBinding.etRepeated;
            EditText etPlanYesterday = bottomSheetBinding.etPlanYesterday;
            EditText etYesterdayPercentage = bottomSheetBinding.etYesterdayPercentage;
            EditText etRepeatedYesterday = bottomSheetBinding.etRepeatedYesterday;
            EditText etPlanTomorrow = bottomSheetBinding.etPlanTomorrow;
            EditText etNote = bottomSheetBinding.notes;
            Button btnSave = bottomSheetBinding.btnSave;
            ProgressBar progressView = bottomSheetBinding.progressBar;

            cancel.setOnClickListener(v -> {
                dialog.dismiss();
            });
            btnSave.setOnClickListener(v -> {
                // Get the values entered by the user
                // today
                String planToday = etPlanToday.getText().toString().trim();
                String todayPercentage = etTodayPercentage.getText().toString().trim();
                String repeated = etRepeated.getText().toString().trim();
                // yesterday
                String planYesterday = etPlanYesterday.getText().toString().trim();
                String yesterdayPercentage = etYesterdayPercentage.getText().toString().trim();
                String repeatedYesterday = etRepeatedYesterday.getText().toString().trim();
                // Tomorrow
                String planTomorrow = etPlanTomorrow.getText().toString().trim();
                String note = etNote.getText().toString().trim();

                // Show the progress view
                // Validate the input
                boolean isValid = validateInput(planToday, todayPercentage, repeated,
                        planYesterday, yesterdayPercentage, repeatedYesterday,
                        planTomorrow, note, bottomSheetBinding);

                if (isValid) {
                    showProgress(true, progressView, getContext());

                    // Save the data to Firestore
                    String currentDate = getCurrentDate();

                    AttendanceDao attendanceDao = DatabaseClient.getInstance(context).getAppDatabase().attendanceDao();
                    Attendance attendance = new Attendance(student.getId(), currentDate, planToday, todayPercentage,
                            repeated, planYesterday, yesterdayPercentage,
                            repeatedYesterday, planTomorrow, note);

                    new AsyncTask<Void, Void, Long>() {
                        @Override
                        protected Long doInBackground(Void... voids) {
                            return attendanceDao.insert(attendance);
                        }

                        @Override
                        protected void onPostExecute(Long result) {
                            super.onPostExecute(result);
                            if (result > -1) {
                                Toast.makeText(context, "تم الحفظ :)", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }.execute();
                }
            });
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
             dialog.show();


            animateBottomSheet(dialog);
        }
    }

    public static void animateBottomSheet(Dialog bottomSheetDialog) {
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

    public static boolean validateInput(String planToday, String todayPercentage, String repeated,
                                        String planYesterday, String yesterdayPercentage, String repeatedYesterday,
                                        String planTomorrow, String note, AddNewAttendanceLayoutBinding bottomSheetBinding) {
        // Perform your validation logic here
        if (planToday.isEmpty()) {
            bottomSheetBinding.etPlanToday.setError("أدخل الخطة لليوم");
            bottomSheetBinding.etPlanToday.requestFocus();
            return false;
        }

        if (todayPercentage.isEmpty()) {
            bottomSheetBinding.etTodayPercentage.setError("أدخل النسبة المئوية لليوم");
            bottomSheetBinding.etTodayPercentage.requestFocus();
            return false;
        } else {
            try {
                double percentage = Double.parseDouble(todayPercentage);
                if (percentage < 0 || percentage > 100) {
                    bottomSheetBinding.etTodayPercentage.setError("الرجاء إدخال نسبة صحيحة بين 0 و 100");
                    bottomSheetBinding.etTodayPercentage.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                bottomSheetBinding.etTodayPercentage.setError("الرجاء إدخال رقم صحيح للنسبة");
                bottomSheetBinding.etTodayPercentage.requestFocus();
                return false;
            }
        }

        if (repeated.isEmpty()) {
            bottomSheetBinding.etRepeated.setError("أدخل عدد المرات المتكررة");
            bottomSheetBinding.etRepeated.requestFocus();
            return false;
        }

        if (planYesterday.isEmpty()) {
            bottomSheetBinding.etPlanYesterday.setError("أدخل الخطة لليوم السابق");
            bottomSheetBinding.etPlanYesterday.requestFocus();
            return false;
        }

        if (yesterdayPercentage.isEmpty()) {
            bottomSheetBinding.etYesterdayPercentage.setError("أدخل النسبة المئوية لليوم السابق");
            bottomSheetBinding.etYesterdayPercentage.requestFocus();
            return false;
        } else {
            try {
                double percentage = Double.parseDouble(yesterdayPercentage);
                if (percentage < 0 || percentage > 100) {
                    bottomSheetBinding.etYesterdayPercentage.setError("الرجاء إدخال نسبة صحيحة بين 0 و 100");
                    bottomSheetBinding.etYesterdayPercentage.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                bottomSheetBinding.etYesterdayPercentage.setError("الرجاء إدخال رقم صحيح للنسبة");
                bottomSheetBinding.etYesterdayPercentage.requestFocus();
                return false;
            }
        }

        if (repeatedYesterday.isEmpty()) {
            bottomSheetBinding.etRepeatedYesterday.setError("أدخل عدد المرات المتكررة لليوم السابق");
            bottomSheetBinding.etRepeatedYesterday.requestFocus();
            return false;
        }

        if (planTomorrow.isEmpty()) {
            bottomSheetBinding.etPlanTomorrow.setError("أدخل الخطة لليوم القادم");
            bottomSheetBinding.etPlanTomorrow.requestFocus();
            return false;
        }


        // Add more validation rules for all fields

        return true;
    }


    public static void showProgress(boolean show, ProgressBar progressView, Context context) {
        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }


    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }


}
