package com.ayaenshasy.AlBayan.jsons;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.ayaenshasy.AlBayan.ui.fragments.QuranFragment;
import com.ayaenshasy.AlBayan.model.QuranChapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Quran extends AsyncTask<Void, Void, List<QuranChapter>> {

     private ProgressBar progressBar;
    Context context;
    private QuranFragment fragment;

    public Quran(QuranFragment fragment, ProgressBar progressBar,Context context) {
        this.fragment = fragment;
        this.progressBar = progressBar;
        this.context = context;
    }

    @Override
    protected void onPostExecute(List<QuranChapter> quranChapters) {
        super.onPostExecute(quranChapters);
        // Pass the loaded chapters to the RecyclerView adapter for display
        fragment.displayChapters(quranChapters);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected List<QuranChapter> doInBackground(Void... voids) {
        return QuranUtils.loadQuranChapters(context);
    }
}

class QuranUtils {
    public static List<QuranChapter> loadQuranChapters(Context context) {
        try {
            // Read the JSON file from assets directory
            InputStream inputStream = context.getAssets().open("quran.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            bufferedReader.close();

            String json = jsonStringBuilder.toString();
            JSONArray chaptersArray = new JSONArray(json);

            List<QuranChapter> chaptersList = new ArrayList<>();
            for (int i = 0; i < chaptersArray.length(); i++) {
                JSONObject chapterObject = chaptersArray.getJSONObject(i);

                String chapterName = chapterObject.getString("name");
                String type = chapterObject.getString("type");
                int chapterNumber = chapterObject.getInt("id");

                QuranChapter quranChapter = new QuranChapter();
                quranChapter.setName(chapterName);
                quranChapter.setNum(chapterNumber);
                quranChapter.setType(type);
                chaptersList.add(quranChapter);
            }

            return chaptersList;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

