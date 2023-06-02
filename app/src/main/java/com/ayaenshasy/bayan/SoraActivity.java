package com.ayaenshasy.bayan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaenshasy.bayan.model.Verse;
import com.ayaenshasy.bayan.model.VerseChapter;
import com.ayaenshasy.bayan.utils.Constant;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SoraActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sora);

        textView = findViewById(R.id.textView4);
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

                    // Process the verses and update the UI on the main thread
                    runOnUiThread(() -> {
                        StringBuilder verseText = new StringBuilder();
                        for (Verse verse : verses) {
                            verseText.append(verse.getText()).append(" ");
                        }

                        textView.setText(verseText.toString());
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
