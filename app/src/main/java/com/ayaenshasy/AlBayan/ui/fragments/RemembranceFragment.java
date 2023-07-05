package com.ayaenshasy.AlBayan.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayaenshasy.AlBayan.NotificationsActivity;
import com.ayaenshasy.AlBayan.adapter.RemembranceAdapter;
import com.ayaenshasy.AlBayan.databinding.FragmentRemembranceBinding;
import com.ayaenshasy.AlBayan.model.RemembranceModel;

import java.util.ArrayList;
import java.util.List;


public class RemembranceFragment extends Fragment {

    List<RemembranceModel> list = new ArrayList<>();

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


        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));


        RemembranceAdapter();

        binding.notification.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        });
        return view;
    }



    private void RemembranceAdapter() {

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new RemembranceAdapter(getActivity(), list);
        list .add( new RemembranceModel(1,"اذكار الصباح"));
        list .add( new RemembranceModel(2,"أذكار المساء"));
        list .add( new RemembranceModel(3,"أذكار بعد الصلاة"));
        list .add( new RemembranceModel(4,"أذكار الصلاة"));
        list .add( new RemembranceModel(5,"أذكار النوم"));
        list .add( new RemembranceModel(6,"أذكار الاستيقاظ"));
        binding.recyclerview.setAdapter(adapter);


    }

}