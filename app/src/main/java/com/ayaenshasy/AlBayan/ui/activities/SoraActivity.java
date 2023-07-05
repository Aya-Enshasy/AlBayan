package com.ayaenshasy.AlBayan.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.SuraAdapter;
import com.ayaenshasy.AlBayan.database.AppDatabase;
import com.ayaenshasy.AlBayan.database.DatabaseClient;
import com.ayaenshasy.AlBayan.databinding.ActivitySoraBinding;
import com.ayaenshasy.AlBayan.listeners.SoraListener;
import com.ayaenshasy.AlBayan.model.quran.Verse;
import com.ayaenshasy.AlBayan.model.VerseChapter;
import com.ayaenshasy.AlBayan.utils.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private float fontSize = 18;
    boolean more = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String chapterId = intent.getStringExtra(Constant.AYA_ID);
        String chapterName = intent.getStringExtra(Constant.AYA_NAME);

        binding.soraName.setText("سورة" + " " + chapterName);
        new Thread(() -> {
            fetchChapterFromAssets(chapterId);

        }).start();


        fontSize();
    }

    private void fontSize(){
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fontSize = progress + 12;
                adapter.updateFontSize(fontSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }
        });

        binding.soraName.setOnClickListener(View -> {
            if (more==false) {
                binding.constraintLayout3.setVisibility(android.view.View.VISIBLE);
                more = true;
            } else {
                binding.constraintLayout3.setVisibility(android.view.View.GONE);
                more = false;
            }
        });
    }

    private void fetchChapterFromAssets(String chapterId) {
        try {
            // Read the JSON file from assets directory
            InputStream inputStream = getAssets().open("quran.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            bufferedReader.close();

            String json = jsonStringBuilder.toString();
            JSONArray chaptersArray = new JSONArray(json);

            // Find the chapter by its ID in the JSON array
            for (int i = 0; i < chaptersArray.length(); i++) {
                JSONObject chapterObject = chaptersArray.getJSONObject(i);
                int id = chapterObject.getInt("id");
                if (id == Integer.parseInt(chapterId)) {
                    String versesJson = chapterObject.getString("verses");
                    JSONArray versesArray = new JSONArray(versesJson);

                    // Parse the verses JSON array
                    List<Verse> verses = new ArrayList<>();
                    for (int j = 0; j < versesArray.length(); j++) {
                        JSONObject verseObject = versesArray.getJSONObject(j);
                        int verseId = verseObject.getInt("id");
                        String verseText = verseObject.getString("text");
                        String verseTransliteration = verseObject.getString("transliteration");

                        Verse verse = new Verse();
                        verse.setId(verseId);
                        verse.setText(verseText);
                        verse.setTransliteration(verseTransliteration);

                        verses.add(verse);
                    }

                    // Display the fetched chapter
                    runOnUiThread(() -> {
                        binding.progressBar2.setVisibility(View.VISIBLE);
                        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
                        adapter = new SuraAdapter(verses, getBaseContext(), new SoraListener() {
                            @Override
                            public void onClick(TextView name) {
                                name.setTextSize(fontSize);
                            }
                        });
                        binding.recyclerview.setAdapter(adapter);
                        binding.progressBar2.setVisibility(View.GONE);
                    });

                    break; // Stop searching after finding the chapter
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}
