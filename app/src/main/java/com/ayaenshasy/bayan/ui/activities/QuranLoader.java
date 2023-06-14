package com.ayaenshasy.bayan.ui.activities;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ayaenshasy.bayan.database.AppDatabase;
import com.ayaenshasy.bayan.database.DatabaseClient;
import com.ayaenshasy.bayan.database.QuranChapterDao;
import com.ayaenshasy.bayan.ui.fragments.QuranFragment;
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
    private ProgressBar progressBar;

    private QuranFragment fragment;

    public QuranLoader(QuranFragment fragment, ProgressBar progressBar) {
        this.fragment = fragment;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected List<QuranChapter> doInBackground(Void... voids) {
        List<QuranChapter> chaptersList = new ArrayList<>();
        if (!isDataExistsInDatabase()) {
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
                        quranChapter.setNum(chapterNumber);
                        quranChapter.setType(type);
                        chaptersList.add(quranChapter);
                    }
                    // Save the data to the Room database
                    DatabaseClient databaseClient = DatabaseClient.getInstance(fragment.getActivity());
                    AppDatabase appDatabase = databaseClient.getAppDatabase();
                    appDatabase.quranChapterDao().insertAll(chaptersList);

                } else {
                    Log.e(TAG, "HTTP response code: " + responseCode);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Data exists in the Room database, so retrieve it from there
            DatabaseClient databaseClient = DatabaseClient.getInstance(fragment.getActivity());
            AppDatabase appDatabase = databaseClient.getAppDatabase();
            chaptersList = appDatabase.quranChapterDao().getAllChapters();
        }


        return chaptersList;
    }

    @Override
    protected void onPostExecute(List<QuranChapter> quranChapters) {
        super.onPostExecute(quranChapters);
        // Pass the loaded chapters to the RecyclerView adapter for display
        fragment.displayChapters(quranChapters);
        progressBar.setVisibility(View.GONE);
    }

    private boolean isDataExistsInDatabase() {
        DatabaseClient databaseClient = DatabaseClient.getInstance(fragment.getActivity());
        AppDatabase appDatabase = databaseClient.getAppDatabase();
        List<QuranChapter> chaptersList = appDatabase.quranChapterDao().getAllChapters();
        return chaptersList != null && chaptersList.size() > 0;
    }


}

