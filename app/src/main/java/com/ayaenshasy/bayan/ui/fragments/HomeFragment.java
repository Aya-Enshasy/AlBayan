package com.ayaenshasy.bayan.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance( ) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        getUserData();

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void getUserData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String user_id = AppPreferences.getInstance(getActivity()).getStringPreferences(Constant.USER_ID);

        DatabaseReference usersRef = database.getReference("users").child(user_id);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String image = dataSnapshot.child("image").getValue(String.class);
                    AppPreferences.getInstance(getActivity()).setStringPreferences(Constant.USER_NAME,name);
                    AppPreferences.getInstance(getActivity()).setStringPreferences(Constant.USER_IMAGE,image);

                    // Do something with the retrieved user data
                    Log.e("UserData", "Name: " + name);
                    Log.e("UserData", "img  : " + image);
                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Handle any errors or cancellations
            }
        });


    }
}