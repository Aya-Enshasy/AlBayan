package com.ayaenshasy.bayan.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.RemembranceAdapter;
import com.ayaenshasy.bayan.adapter.RemembranceDetailsAdapter;
import com.ayaenshasy.bayan.databinding.ActivityRemembranceDetailsBinding;
import com.ayaenshasy.bayan.databinding.ActivitySplashBinding;
import com.ayaenshasy.bayan.databinding.FragmentRemembranceBinding;
import com.ayaenshasy.bayan.listeners.RemembranceListener;
import com.ayaenshasy.bayan.model.RemembranceDetailsModel;
import com.ayaenshasy.bayan.model.RemembranceModel;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RemembranceDetailsActivity extends AppCompatActivity {

    ActivityRemembranceDetailsBinding binding;
     RemembranceDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemembranceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RemembranceDetailsAdapter();


    }


    private void RemembranceDetailsAdapter() {

        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra(Constant.Remembrance_Id));
        String name = intent.getStringExtra(Constant.Remembrance_Name);
        binding.text.setText(name);



        ArrayList<RemembranceDetailsModel> receivedList = intent.getParcelableArrayListExtra(Constant.Remembrance_List);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
        adapter = new RemembranceDetailsAdapter(getBaseContext(), receivedList, new RemembranceListener() {
            @Override
            public void onClick(String name) {
                Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                txtIntent.setType("text/plain");
                txtIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "الاذكار");
                txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, name);
                txtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(txtIntent, "مشاركة"));
            }
        });
        binding.recyclerview.setAdapter(adapter);


    }

}