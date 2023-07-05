package com.ayaenshasy.AlBayan.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ayaenshasy.AlBayan.NotificationsActivity;
import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.databinding.FragmentQuranBinding;
import com.ayaenshasy.AlBayan.jsons.Quran;
 import com.ayaenshasy.AlBayan.adapter.QuranChapterAdapter;
import com.ayaenshasy.AlBayan.model.QuranChapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuranFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuranFragment extends Fragment {
    private QuranChapterAdapter adapter;
    FragmentQuranBinding binding;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QuranFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static QuranFragment newInstance( ) {
        QuranFragment fragment = new QuranFragment();
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
        binding = FragmentQuranBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        Quran quranLoader = new Quran(this, progressBar,getActivity());
        quranLoader.execute();

        binding.notification.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        });
        return view;
    }

    public void displayChapters(List<QuranChapter> chaptersList) {
        adapter = new QuranChapterAdapter(chaptersList,getActivity());
        binding.recyclerView.setAdapter(adapter);
    }

}