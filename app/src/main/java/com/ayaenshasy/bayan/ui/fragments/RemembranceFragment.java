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

import com.ayaenshasy.bayan.adapter.RemembranceAdapter;
import com.ayaenshasy.bayan.databinding.FragmentRemembranceBinding;
import com.ayaenshasy.bayan.model.RemembranceDetailsModel;
import com.ayaenshasy.bayan.model.RemembranceModel;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class RemembranceFragment extends Fragment {

    ArrayList<RemembranceModel> list = new ArrayList<>();

    FragmentRemembranceBinding binding;
    RemembranceAdapter adapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RemembranceFragment() {
        // Required empty public constructor
    }

    public static RemembranceFragment newInstance() {
        RemembranceFragment fragment = new RemembranceFragment();
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
        binding = FragmentRemembranceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RemembranceAdapter();
        getData();
        getDataFromShared();

        return view;
    }

    private void getData() {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference remembranceRef = database.getReference("Remembrance");

            remembranceRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RemembranceModel remembranceModel = new RemembranceModel();
                        remembranceModel.setId(dataSnapshot.child("id").getValue(Integer.class));
                        remembranceModel.setName(dataSnapshot.child("name").getValue(String.class));

                        ArrayList<RemembranceDetailsModel> itemList = new ArrayList<>();
                        for (DataSnapshot itemSnapshot : dataSnapshot.child("list").getChildren()) {
                            RemembranceDetailsModel remembranceItem = itemSnapshot.getValue(RemembranceDetailsModel.class);

                            itemList.add(remembranceItem);
                        }
                        remembranceModel.setList(itemList);

                        list.add(remembranceModel);
                        Gson gson = new Gson();
                        String remembranceItemListJson = gson.toJson(list);
                        AppPreferences.getInstance(getActivity()).setStringPreference(Constant.Remembrance_List,remembranceItemListJson);

                        Log.e("remembranceModel", dataSnapshot.toString());
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(),"no data",Toast.LENGTH_LONG).show();
                    Log.e("error","error");
                }
            });

    }

    private void getDataFromShared() {

        String jsonString = AppPreferences.getInstance(getActivity()).getStringPreference(Constant.Remembrance_List);

        if (!jsonString.equals("")) {
            Type type = new TypeToken<ArrayList<RemembranceModel>>() {
            }.getType();
            ArrayList<RemembranceModel> dataList = new Gson().fromJson(jsonString, type);

            RemembranceAdapter adapter = new RemembranceAdapter(getActivity(), dataList);
            binding.recyclerview.setAdapter(adapter);

        }
    }

    private void RemembranceAdapter() {

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new RemembranceAdapter(getActivity(), list);
        binding.recyclerview.setAdapter(adapter);


    }

}