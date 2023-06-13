package com.ayaenshasy.bayan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ayaenshasy.bayan.databinding.FragmentAddNewTeacherBinding;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewTeacherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewTeacherFragment extends Fragment {
    FragmentAddNewTeacherBinding binding;
    private Uri selectedImageUri;
    DatabaseReference databaseReference;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNewTeacherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewTeacherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewTeacherFragment newInstance(String param1, String param2) {
        AddNewTeacherFragment fragment = new AddNewTeacherFragment();
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
        binding = FragmentAddNewTeacherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        databaseReference = FirebaseDatabase.getInstance().getReference("teachers");

        binding.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkImagePermissions();

            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                saveTeacherData();
            }
        });
        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.imgUser.setImageURI(selectedImageUri);
        }
    }

//    private void saveTeacherData() {
//        // Retrieve entered data
//        String name = binding.etName.getText().toString().trim();
//        String idNumber = binding.etId.getText().toString().trim();
//        String phoneNumber = binding.etPhone.getText().toString().trim();
//
//        // Perform data validation
//        if (name.isEmpty() || idNumber.isEmpty() || phoneNumber.isEmpty()) {
//            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a Teacher object
//        Student teacher = new Student(name, idNumber, phoneNumber, selectedImageUri);
//
//        String teacherKey = databaseReference.push().getKey();
//        databaseReference.child(teacherKey).setValue(teacher)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Data successfully saved
//                        Toast.makeText(getContext(), "Teacher data saved", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Failed to save data
//                        Toast.makeText(getContext(), "Failed to save teacher data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//    }

    private void checkImagePermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constant.REQUEST_IMAGE_PERMISSION);
        } else {
            // Permission already granted
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Constant.REQUEST_IMAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openGallery();
            } else {
                // Permission denied
                Toast.makeText(getActivity(), "Image permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}