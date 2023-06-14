package com.ayaenshasy.bayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.SuraAdapter;
import com.ayaenshasy.bayan.database.AppDatabase;
import com.ayaenshasy.bayan.database.DatabaseClient;
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
//    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String chapterId = intent.getStringExtra(Constant.AYA_ID);
//        progressBar = findViewById(R.id.progressBar);

        // Check if data exists in the Room database
        DatabaseClient databaseClient = DatabaseClient.getInstance(this);
        AppDatabase appDatabase = databaseClient.getAppDatabase();

        new Thread(() -> {
            List<Verse> verses = appDatabase.verseDao().getAllVerses();
            if (verses != null && verses.size() > 0) {
                // Data exists in the database, retrieve it
                displayChapterFromDatabase(verses);
            } else {
                // Data doesn't exist in the database, fetch it from the server
                fetchChapterFromServer(chapterId);
            }
        }).start();
    }

    private void fetchChapterFromServer(String chapterId) {
        OkHttpClient client = new OkHttpClient();
        String CHAPTER_URL_PREFIX = "https://cdn.jsdelivr.net/npm/quran-json@3.1.2/dist/chapters/";
        String CHAPTER_URL_SUFFIX = ".json";

        Request request = new Request.Builder()
                .url(CHAPTER_URL_PREFIX + chapterId + CHAPTER_URL_SUFFIX)
                .build();

        // Make an asynchronous GET request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Retrieve the JSON data
                    String jsonData = response.body().string();

                    // Parse the JSON and retrieve the text
                    Gson gson = new Gson();
                    VerseChapter chapter = gson.fromJson(jsonData, VerseChapter.class);
                    List<Verse> verses = chapter.getVerses();

                    // Save the fetched chapter to Room database in a background thread
                    new Thread(() -> {
                        DatabaseClient databaseClient = DatabaseClient.getInstance(SoraActivity.this);
                        AppDatabase appDatabase = databaseClient.getAppDatabase();
                        appDatabase.verseDao().insertAll(verses);
                    }).start();

                    runOnUiThread(() -> {
                        // Show the progress bar
                        binding.progressBar2.setVisibility(View.VISIBLE);

                        // Display the fetched chapter
                        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
                        adapter = new SuraAdapter(verses, getBaseContext());
                        binding.recyclerview.setAdapter(adapter);

                        // Hide the progress bar
                        binding.progressBar2.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void displayChapterFromDatabase(List<Verse> verses) {
        runOnUiThread(() -> {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
            adapter = new SuraAdapter(verses, getBaseContext());
            binding.recyclerview.setAdapter(adapter);
            binding.progressBar2.setVisibility(View.GONE);
        });
    }

}
