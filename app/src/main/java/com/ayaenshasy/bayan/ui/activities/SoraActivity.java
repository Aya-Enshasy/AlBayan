package com.ayaenshasy.bayan.ui.activities;

 import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.ayaenshasy.bayan.adapter.SuraAdapter;
import com.ayaenshasy.bayan.databinding.ActivitySoraBinding;
import com.ayaenshasy.bayan.model.quran.Verse;
import com.ayaenshasy.bayan.model.VerseChapter;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SoraActivity extends AppCompatActivity {
    ActivitySoraBinding binding;
    SuraAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        String chapterId = intent.getStringExtra(Constant.AYA_ID);


        OkHttpClient client = new OkHttpClient();
        String CHAPTER_URL_PREFIX = "https://cdn.jsdelivr.net/npm/quran-json@3.1.2/dist/chapters/";
        String CHAPTER_URL_SUFFIX = ".json";

        Request request = new Request.Builder()
                .url(CHAPTER_URL_PREFIX + chapterId + CHAPTER_URL_SUFFIX)
                .build();

        // Make an asynchronous GET request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Retrieve the JSON data
                    String jsonData = response.body().string();

                    // Parse the JSON and retrieve the text
                    Gson gson = new Gson();
                    VerseChapter chapter = gson.fromJson(jsonData, VerseChapter.class);
                    List<Verse> verses = chapter.getVerses();


                     runOnUiThread(() -> {
                         binding.recyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
                         adapter = new SuraAdapter(verses,getBaseContext());
                         binding.recyclerview.setAdapter(adapter);

                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
