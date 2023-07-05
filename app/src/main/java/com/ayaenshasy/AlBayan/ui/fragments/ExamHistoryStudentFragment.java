package com.ayaenshasy.AlBayan.ui.fragments;

import static com.ayaenshasy.AlBayan.utils.Constant.USER_ID;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.ExamAdapter;
import com.ayaenshasy.AlBayan.database.DatabaseClient;
import com.ayaenshasy.AlBayan.database.ExamDao;
import com.ayaenshasy.AlBayan.databinding.FragmentExamHistoryStudentBinding;
import com.ayaenshasy.AlBayan.listeners.DataListener;
import com.ayaenshasy.AlBayan.model.Exam;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExamHistoryStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExamHistoryStudentFragment extends BaseFragment {
    FragmentExamHistoryStudentBinding binding;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAPTURE_IMAGE = 3;
    ExamAdapter adapter;
    private List<Exam> list = new ArrayList<>();
    //    private ShapeableImageView imgUser; // Declare imgUser as a class member
    String date;
    String user_id;
    private byte[] imageData;
    private Calendar calendar;
    ActivityResultLauncher<String> al1;
    Uri imageUri;
    ShapeableImageView imgExam;
    String imageUrl;
    private Uri selectedImageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExamHistoryStudentFragment() {
        // Required empty public constructor
    }

    public static ExamHistoryStudentFragment newInstance(String param1, String param2) {
        ExamHistoryStudentFragment fragment = new ExamHistoryStudentFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExamHistoryStudentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        user_id = getActivity().getIntent().getStringExtra(USER_ID);
        calendar = Calendar.getInstance();

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet(null);
            }
        });
        RemembranceAdapter(); // Call the adapter setup method here
        requestStoragePermission();

        getDataFromFirestore();
        getDataFromRoom();
        userImage();
        return view;
    }


    void getDataFromFirestore() {
//        Toast.makeText(context, user_id+"", Toast.LENGTH_SHORT).show();
        FirebaseFirestore.getInstance().collection("exams")
                .document(user_id)
                .collection("records")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Exam> list = new ArrayList<>(); // Create a new list to store the retrieved exams

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String examId = documentSnapshot.getId();
                        String degree = documentSnapshot.getString("degree");
                        String image = documentSnapshot.getString("image");
                        String mosque = documentSnapshot.getString("mosque");
                        String name = documentSnapshot.getString("name");
                        String date = documentSnapshot.getString("date");
                        Exam exam = new Exam(degree, image, mosque, name, date); // Include examId in the constructor
                        exam.setId(documentSnapshot.getLong("examId").intValue());
                        exam.setExamId(examId);
                        exam.setStudentId(documentSnapshot.getString("studentId"));
                        list.add(exam);
                    }

                    // TODO: Add your code here to handle the retrieved list of exams
                    // For example, you can pass the list to another method or update the UI with the data.
//                    handleRetrievedExams(list);
                    adapter.setExams(list);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur while retrieving the data
//                    handleFailure(e);
                });
    }

    @SuppressLint("StaticFieldLeak")
    private void getDataFromRoom() {
        new AsyncTask<Void, Void, List<Exam>>() {
            @SuppressLint("StaticFieldLeak")

            @Override
            protected List<Exam> doInBackground(Void... voids) {
                ExamDao examDao = DatabaseClient.getInstance(context).getAppDatabase().examDao();
                List<Exam> exams = examDao.getExamByStudentId(user_id);
                return exams;
            }

            @Override
            protected void onPostExecute(List<Exam> exams) {
                  adapter.setExams(exams);
            }
        }.execute();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String selectedDate = dateFormat.format(calendar.getTime());

            etDate.setText(selectedDate);
        }
    };

    private void RemembranceAdapter() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new ExamAdapter(list, getActivity(), new DataListener<Exam>() {
            @Override
            public void sendData(Exam exam) {
                showBottomSheet(exam);
            }
        });
        binding.recyclerView.setAdapter(adapter);
    }

    TextView etDate;
    boolean ImageUpload = false, btnClicked = false, ImageFinished = false;

    @SuppressLint("StaticFieldLeak")
    private void showBottomSheet(Exam exam2) {
        // Check if the fragment is attached to the activity and the context is not null
        if (isAdded() && getContext() != null) {
            // Create and show the bottom sheet
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.add_exam_layout, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            // Get references to the views
            imgExam = bottomSheetView.findViewById(R.id.img_user);
            TextView tvName = bottomSheetView.findViewById(R.id.tv_name);
            AppCompatEditText etName = bottomSheetView.findViewById(R.id.et_name);
            TextView tvDegree = bottomSheetView.findViewById(R.id.tv_degree);
            AppCompatEditText etDegree = bottomSheetView.findViewById(R.id.et_degree);
            TextView tvMosque = bottomSheetView.findViewById(R.id.tv_mosque);
            AppCompatEditText etMosque = bottomSheetView.findViewById(R.id.et_mosque);
            TextView tvDate = bottomSheetView.findViewById(R.id.tv_date);
            etDate = bottomSheetView.findViewById(R.id.et_date);
            ProgressBar progressBar = bottomSheetView.findViewById(R.id.progressBar);
            AppCompatButton btnSave = bottomSheetView.findViewById(R.id.btn_save);
            etDate.setOnClickListener(view -> showDatePickerDialog());


            imgExam.setOnClickListener(view -> {
                if (isNetworkAvailable()) {
                    ImageUpload = true;
                    al1.launch("image/*");
                } else {
                    Toast.makeText(context, "انت تحتاج الى انترنت لرفع صورة, تستطيع رفع الصورة عند توفر الانترنت ;) ", Toast.LENGTH_SHORT).show();
                }
            });

            // Populate the fields if exam2 is not null
            if (exam2 != null) {
                etName.setText(exam2.getName());
                etDegree.setText(exam2.getDegree());
                etMosque.setText(exam2.getMosque());
                etDate.setText(exam2.getDate());
//                if (exam2.getImage() != null)
                Glide.with(context).load(exam2.getImage()).placeholder(R.drawable.ic_user_circle_svgrepo_com)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
                        .into(imgExam);

            }

            // Set click listener for the save button
            btnSave.setOnClickListener(v -> {
                // Get the values entered by the user
                String name = etName.getText().toString().trim();
                String degree = etDegree.getText().toString().trim();
                String mosque = etMosque.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                btnClicked = true;
                if (ImageFinished)
                    uploadExamImage();

                // Validate fields
                if (TextUtils.isEmpty(name)) {
                    etName.setError("الرجاء إدخال الاسم");
                    return;
                }

                if (TextUtils.isEmpty(degree)) {
                    etDegree.setError("الرجاء إدخال الدرجة");
                    return;
                }

                if (TextUtils.isEmpty(mosque)) {
                    etMosque.setError("الرجاء إدخال اسم المسجد");
                    return;
                }

                if (TextUtils.isEmpty(date)) {
                    etDate.setError("الرجاء إدخال التاريخ");
                    return;
                }

                // Show the progress view
                progressBar.setVisibility(View.VISIBLE);

                // Save the data to Room database
                Exam exam = new Exam();
                exam.setStudentId(user_id);
                exam.setName(name);
                exam.setDegree(degree);
                exam.setMosque(mosque);
                exam.setDate(date);
                exam.setImage(imageUrl);
                 if (exam2 != null)
                    exam.setId(exam2.getId());
                // Get an instance of the Room database
                ExamDao examDao = DatabaseClient.getInstance(context).getAppDatabase().examDao();

                // Check if the exam already exists in the database
                if (ImageUpload && imageUrl == null) {
                    Toast.makeText(getContext(), "انتظر قلبلا ثم حاول مرة اخرى", Toast.LENGTH_SHORT).show();
                } else {
                    AsyncTask<Void, Void, Exam> checkExamTask = new AsyncTask<Void, Void, Exam>() {
                        @Override
                        protected Exam doInBackground(Void... voids) {
                            return examDao.getExamById(exam.getId());
                        }

                        @Override
                        protected void onPostExecute(Exam existingExam) {
                            if (exam2 != null) {
                                if (exam2.getExamId() == null || exam2.getExamId().isEmpty()) {
                                    if (existingExam != null) {
                                        // Exam already exists in the database, perform the update
                                        AsyncTask<Void, Void, Void> updateExamTask = new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... voids) {
                                                examDao.update(exam);
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void aVoid) {
                                                adapter.updateExam(existingExam.getId(), exam);
                                                Toast.makeText(getContext(), "تم تحديث البيانات بنجاح", Toast.LENGTH_SHORT).show();
                                                bottomSheetDialog.dismiss();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        };
                                        updateExamTask.execute();
                                    }
                                } else {
                                    exam.setExamId(exam2.getExamId());
                                    if (isNetworkAvailable()) {
                                        updateFireStore(exam, progressBar, bottomSheetDialog);
                                    } else {
                                        Toast.makeText(getActivity(), "تأكد من اتصال جهازك بالانترنت ;)", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                // Handle the case when exam2 is null or its examId is null or empty
                                AsyncTask<Void, Void, Void> insertExamTask = new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        examDao.insert(exam);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        adapter.setExam(exam);
                                        Toast.makeText(getContext(), "تم حفظ البيانات بنجاح", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                };
                                insertExamTask.execute();
                            }
                        }
                    };
                    checkExamTask.execute();
                }

            });

            bottomSheetDialog.show();

            // Animate the bottom sheet dialog
            animateBottomSheet(bottomSheetDialog);
        }
    }

    private void updateFireStore(Exam exam, ProgressBar progressBar, BottomSheetDialog bottomSheetDialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendanceCollection = db.collection("exams");

        DocumentReference document = attendanceCollection
                .document(exam.getStudentId())
                .collection("records").document(exam.getExamId());
//                        .document(exam.getDate());
        Map<String, Object> examData = new HashMap<>();
        examData.put("examId", exam.getId());
        examData.put("studentId", exam.getStudentId());
        examData.put("name", exam.getName());
        examData.put("degree", exam.getDegree());
        examData.put("mosque", exam.getMosque());
        examData.put("date", exam.getDate());
        examData.put("image", exam.getImage());
        document.set(examData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "بيانات الاختبارات تم حفظها بنجاح", Toast.LENGTH_SHORT).show();
                        // Delete the uploaded record from the local database
//                        deleteExamFromDatabase(exam);
                        adapter.updateExam(exam.getId(), exam);
                    });
                })
                .addOnFailureListener(e -> {
                    // Failed to save data
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnCompleteListener(task -> {
                    // Hide the progress bar
//                    progressBar.setVisibility(View.GONE);
                    // Dismiss the dialog
                    bottomSheetDialog.dismiss();
                    progressBar.setVisibility(View.GONE);

//                    dialog.dismiss();
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

    private void formatDate(String date) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        try {
            Date inputDate = inputDateFormat.parse(date);
            String formattedDate = outputDateFormat.format(inputDate);
            date = formattedDate;
            System.out.println("Formatted Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //------------------------------upload exam image -----------------------------------
    private void userImage() {
        al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        imageUri = result;
                        Glide.with(getActivity()).load(imageUri)
                                .error(R.drawable.ic_user_circle_svgrepo_com).into(imgExam);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

//    protected void loaderDialog() {
//        loader_dialog = new Dialog(getContext());
//        loader_dialog.setContentView(R.layout.loader_dialog);
//        loader_dialog.setCancelable(false);
//        loader_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
//        loader_dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
//        loader_dialog.show();
//    }

    private void uploadExamImage() {
//        loaderDialog();
        if (imageUri != null && btnClicked) {
            // Generate a unique filename for the image
            String filename = UUID.randomUUID().toString();

            // Get a reference to the Firebase Storage location
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + filename);

            // Upload the image file to Firebase Storage
            UploadTask uploadTask = storageReference.putFile(imageUri);

            // Monitor the upload process
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL of the uploaded image
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    Toast.makeText(context, "نم تحميل الصورة", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, imageUrl+"نم  ", Toast.LENGTH_SHORT).show();
//                    loader_dialog.dismiss();
                    ImageFinished = true;
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "خطأ في تحميل الصورة، يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();

                });
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "خطأ في تحميل الصورة، يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();

            });
        }
    }

}