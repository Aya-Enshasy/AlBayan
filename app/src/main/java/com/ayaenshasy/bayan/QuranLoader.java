package com.ayaenshasy.bayan;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ayaenshasy.bayan.fragments.QuranFragment;
import com.ayaenshasy.bayan.model.QuranChapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuranLoader extends AsyncTask<Void, Void, List<QuranChapter>> {

    private static final String TAG = QuranLoader.class.getSimpleName();
    private static final String JSON_URL = "https://cdn.jsdelivr.net/npm/quran-json@3.1.2/dist/chapters/index.json";

    private QuranFragment fragment;

    public QuranLoader(QuranFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected List<QuranChapter> doInBackground(Void... voids) {
        List<QuranChapter> chaptersList = new ArrayList<>();

        try {
            URL url = new URL(JSON_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonStringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }
                bufferedReader.close();

                String json = jsonStringBuilder.toString();
                JSONArray chaptersArray = new JSONArray(json);

                for (int i = 0; i < chaptersArray.length(); i++) {
                    JSONObject chapterObject = chaptersArray.getJSONObject(i);

                    String chapterName = chapterObject.getString("name");
                    String type = chapterObject.getString("type");
                    int chapterNumber = chapterObject.getInt("id");

                    QuranChapter quranChapter = new QuranChapter();
                    quranChapter.setName(chapterName);
                    quranChapter.setId(chapterNumber);
                    quranChapter.setType(type);

                    chaptersList.add(quranChapter);
                }
            } else {
                Log.e(TAG, "HTTP response code: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return chaptersList;
    }

    @Override
    protected void onPostExecute(List<QuranChapter> quranChapters) {
        super.onPostExecute(quranChapters);
        // Pass the loaded chapters to the RecyclerView adapter for display
        fragment.displayChapters(quranChapters);
    }



}

