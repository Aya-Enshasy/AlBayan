package com.ayaenshasy.AlBayan.jsons;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ayaenshasy.AlBayan.model.RemembranceDetailsModel;
import com.ayaenshasy.AlBayan.ui.activities.RemembranceDetailsActivity;
import com.ayaenshasy.AlBayan.ui.activities.RemembranceDetailsActivity;
import com.ayaenshasy.AlBayan.utils.AppPreferences;

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

public class AzkarAlMasaa extends AsyncTask<Void, Void, List<RemembranceDetailsModel>> {

    private static final String TAG = AzkarAlMasaa.class.getSimpleName();
    private static final String JSON_URL = "https://ahegazy.github.io/muslimKit/json/azkar_massa.json";
    private ProgressBar progressBar;

    private RemembranceDetailsActivity fragment;

    public AzkarAlMasaa(RemembranceDetailsActivity fragment, ProgressBar progressBar) {
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
    protected List<RemembranceDetailsModel> doInBackground(Void... voids) {
        List<RemembranceDetailsModel> chaptersList = new ArrayList<>();
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
                      JSONObject chaptersObject = new JSONObject(json);


                      JSONArray chaptersArray = chaptersObject.getJSONArray("content");

                     for (int i = 0; i < chaptersArray.length(); i++) {
                         JSONObject chapterObject = chaptersArray.getJSONObject(i);

                         String chapterName = chapterObject.getString("zekr");
                          int chapterNumber = chapterObject.getInt("repeat");

                         RemembranceDetailsModel quranChapter = new RemembranceDetailsModel();
                         quranChapter.setText(chapterName);
                         quranChapter.setRepeat(chapterNumber);
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
    protected void onPostExecute(List<RemembranceDetailsModel> quranChapters) {
        super.onPostExecute(quranChapters);
         fragment.displayChapters(quranChapters);
        progressBar.setVisibility(View.GONE);
         String json = convertChaptersListToJson(quranChapters);
        AppPreferences.getInstance(fragment).setStringPreference("azkar_massa",json);

    }


    private String convertChaptersListToJson(List<RemembranceDetailsModel> chaptersList) {
        try {
            JSONArray chaptersArray = new JSONArray();

            for (RemembranceDetailsModel chapter : chaptersList) {
                JSONObject chapterObject = new JSONObject();
                chapterObject.put("zekr", chapter.getText());
                chapterObject.put("repeat", chapter.getRepeat());
                chaptersArray.put(chapterObject);
            }

            JSONObject chaptersObject = new JSONObject();
            chaptersObject.put("content", chaptersArray);

            return chaptersObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}

