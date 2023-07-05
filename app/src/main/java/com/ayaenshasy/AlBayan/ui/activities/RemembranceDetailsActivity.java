package com.ayaenshasy.AlBayan.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import com.ayaenshasy.AlBayan.adapter.RemembranceDetailsAdapter;
import com.ayaenshasy.AlBayan.databinding.ActivityRemembranceDetailsBinding;
import com.ayaenshasy.AlBayan.jsons.AzkarAfterPray;
import com.ayaenshasy.AlBayan.jsons.AzkarAlMasaa;
import com.ayaenshasy.AlBayan.jsons.AzkarLoader;
import com.ayaenshasy.AlBayan.model.RemembranceDetailsModel;
import com.ayaenshasy.AlBayan.utils.AppPreferences;
import com.ayaenshasy.AlBayan.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RemembranceDetailsActivity extends AppCompatActivity {

    ActivityRemembranceDetailsBinding binding;
    RemembranceDetailsAdapter adapter;
    List<RemembranceDetailsModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemembranceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        RemembranceDetailsAdapter();

        binding.backArrow.setOnClickListener(View->{finish();});



    }

    public void displayChapters(List<RemembranceDetailsModel> chaptersList) {
        adapter = new RemembranceDetailsAdapter(this, chaptersList, null);
        binding.recyclerview.setAdapter(adapter);
    }

    private void RemembranceDetailsAdapter() {

        Intent intent = getIntent();
        String id = intent.getStringExtra(Constant.Remembrance_Id);
        String name = intent.getStringExtra(Constant.Remembrance_Name);
        binding.text.setText(name);

        if (id.equals("1")) {
            String savedData = AppPreferences.getInstance(getBaseContext()).getStringPreference("azkar_sabah");
            AzkarLoader quranLoader = new AzkarLoader(this, binding.progressBar);
            quranLoader.execute();
            if (savedData != null) {
                List<RemembranceDetailsModel> savedChapters = convertJsonToChaptersList(savedData);
                displayChapters(savedChapters);
            } else {
                quranLoader.execute();
            }
        }
        else if (id.equals("2")) {
            AzkarAlMasaa azkarAlMasaa = new AzkarAlMasaa(this, binding.progressBar);
            azkarAlMasaa.execute();

            String savedData = AppPreferences.getInstance(getBaseContext()).getStringPreference("azkar_massa");

            if (savedData != null) {
                List<RemembranceDetailsModel> savedChapters = convertJsonToChaptersList(savedData);
                displayChapters(savedChapters);
            } else {
                azkarAlMasaa.execute();
            }
        }
        else if (id.equals("3")) {
            AzkarAfterPray azkarAfterPray = new AzkarAfterPray(this, binding.progressBar);
            azkarAfterPray.execute();
            String savedData = AppPreferences.getInstance(getBaseContext()).getStringPreference("PostPrayer_azkar");

            if (savedData != null) {
                List<RemembranceDetailsModel> savedChapters = convertJsonToChaptersList(savedData);
                displayChapters(savedChapters);
            } else {
                azkarAfterPray.execute();
            }

        }
        else if (id.equals("4")) {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            adapter = new RemembranceDetailsAdapter(this, list, null);
            list.add(new RemembranceDetailsModel(1, "اللَّهُمَّ بَاعِدْ بَيْنِيْ وَبَيْنَ خَطَايَايَ كَمَا بَاعَدْتَ بَيْنَ الْمَشْرِقِ وَالْمَغْرِبِ، اللهم نَقِّنِيْ مِنْ خَطَايَايَ كَمَا يُنَقَّى الثَّوْبُ الْأبْيَضُ مِنَ الدَّنَسِ، اللهم اغْسِلْنِيْ مِنْ خَطَايَايَ، بِالثَّلْجِ وَالْمَاءِ والْبَرَدِ."));
            list.add(new RemembranceDetailsModel(1, "سُبْحَانَكَ اللهم وَبِحَمْدِكَ، وَتَبَارَكَ اسْمُكَ، وَتَعَالَى جَدُّكَ، وَلَا إِلَهَ غَيْرُكَ."));
            list.add(new RemembranceDetailsModel(1, "الْحَمْدُ للّهِ حَمْداً كَثِيراً طَيِّباً مُبَارَكاً فِيهِ."));
            list.add(new RemembranceDetailsModel(1, "اللهُ أكْبَرُ كَبِيْرًا، وَالْحَمْدُ لِلهِ كَثِيْرًا، وَسُبْحَانَ اللهِ بُكْرَةً وَّاصِيْلًا. أعُوْذُ بِاللهِ مِنَ الشَّيْطَانِ: مِنْ نَفْخِهِ، وَنَفْثِهِ، وَهَمْزِهِ."));
            list.add(new RemembranceDetailsModel(1, "اللَّهُمَّ رَبَّ جَبْرَائِيلَ، وَمِيْكَائِيلَ، وَإِسْرَافِيْلَ، فَاطِرَ السَّمَوَاتِ وَالْأرْضِ، عَالِمَ الغَيْبِ وَالشَّهَادَةِ أنْتَ تَحْكُمُ بَيْنَ عِبَادِكَ فِيْمَا كَانُوا فِيْهِ يَخْتَلِفُونَ، اِهْدِنِيْ لِمَا اخْتُلِفَ فِيْهِ مِنَ الْحَقِّ بِإِذْنِكَ إِنَّكَ تَهْدِيْ مَنْ تَشَاءُ إِلَى صِرَاطٍ مُّسْتَقِيْمٍ."));
            list.add(new RemembranceDetailsModel(1, "وَجَّهْتُ وَجْهِيَ لِلَّذِيْ فَطَرَ السَّمَوَاتِ وَالْأرْضَ حَنِيْفًا وَّمَا أنَا مِنَ الْمُشْرِكِيْنَ، إِنَّ صَلَاتِيْ، وَنُسُكِيْ، وَمَحْيَايَ، وَمَمَاتِيْ لِلهِ رَبِّ الْعَالَمِيْنَ، لَا شَرِيْكَ لَهُ وَبِذَلِكَ أُمِرْتُ وَانَا مِنَ الْمُسْلِمِيْنَ، اللهم أنْتَ الْمَلِكُ لَا إِلَهَ إِلَّا أنْتَ، أنْتَ رَبِّيْ وَأنَا عَبْدُكَ، ظَلَمْتُ نَفْسِيْ وَاعْتَرَفْتُ بِذَنْبِيْ فَاغْفِرْ لِيْ ذُنُوْبِيْ جَمِيْعًا إِنَّهُ لَا يَغْفِرُ الذُّنوبَ إِلَّا أنْتَ. وَاهْدِنِيْ لِأحْسَنِ الْأخْلَاقِ لَا يَهْدِيْ لِأحْسَنِهَا إِلَّا أنْتَ، وَاصْرِفْ عَنِّيْ سَيِّئَهَا، لَا يَصْرِفُ عَنِّيْ سَيِّئَهَا إِلَّا أنْتَ، لَبَّيْكَ وَسَعْدَيْكَ، وَالْخَيْرُ كُلُّهُ بِيَدَيْكَ، وَالشَّرُّ لَيْسَ إِلَيْكَ، أنَا بِكَ وَإِلَيْكَ، تَبارَكْتَ وَتَعَالَيْتَ، أسْتَغْفِرُكَ وَأتُوْبُ إِلَيْكَ."));
            binding.recyclerview.setAdapter(adapter);

        }
        else if (id.equals("5")) {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            adapter = new RemembranceDetailsAdapter(this, list, null);
            list.add(new RemembranceDetailsModel(1, "بِاسْمِكَ رَبِّـي وَضَعْـتُ جَنْـبي ، وَبِكَ أَرْفَعُـه، فَإِن أَمْسَـكْتَ نَفْسـي فارْحَـمْها ، وَإِنْ أَرْسَلْتَـها فاحْفَظْـها بِمـا تَحْفَـظُ بِه عِبـادَكَ الصّـالِحـين."));
            list.add(new RemembranceDetailsModel(1, "اللّهُـمَّ إِنَّـكَ خَلَـقْتَ نَفْسـي وَأَنْـتَ تَوَفّـاهـا لَكَ ممَـاتـها وَمَحْـياها ، إِنْ أَحْيَيْـتَها فاحْفَظْـها ، وَإِنْ أَمَتَّـها فَاغْفِـرْ لَـها . اللّهُـمَّ إِنَّـي أَسْـأَلُـكَ العـافِـيَة. "));
            list.add(new RemembranceDetailsModel(3, "اللّهُـمَّ قِنـي عَذابَـكَ يَـوْمَ تَبْـعَثُ عِبـادَك. "));
            list.add(new RemembranceDetailsModel(1, "بِاسْـمِكَ اللّهُـمَّ أَمـوتُ وَأَحْـيا. "));
            list.add(new RemembranceDetailsModel(1, "الـحَمْدُ للهِ الَّذي أَطْـعَمَنا وَسَقـانا، وَكَفـانا، وَآوانا، فَكَـمْ مِمَّـنْ لا كـافِيَ لَـهُ وَلا مُـؤْوي."));
            list.add(new RemembranceDetailsModel(1, "اللّهُـمَّ عالِـمَ الغَـيبِ وَالشّـهادةِ فاطِـرَ السّماواتِ وَالأرْضِ رَبَّ كُـلِّ شَـيءٍ وَمَليـكَه، أَشْهـدُ أَنْ لا إِلـهَ إِلاّ أَنْت، أَعـوذُ بِكَ مِن شَـرِّ نَفْسـي، وَمِن شَـرِّ الشَّيْـطانِ وَشِـرْكِه، وَأَنْ أَقْتَـرِفَ عَلـى نَفْسـي سوءاً أَوْ أَجُـرَّهُ إِلـى مُسْـلِم ."));
            list.add(new RemembranceDetailsModel(1, "اللّهُـمَّ أَسْـلَمْتُ نَفْـسي إِلَـيْكَ، وَفَوَّضْـتُ أَمْـري إِلَـيْكَ، وَوَجَّـهْتُ وَجْـهي إِلَـيْكَ، وَأَلْـجَـاْتُ ظَهـري إِلَـيْكَ، رَغْبَـةً وَرَهْـبَةً إِلَـيْكَ، لا مَلْجَـأَ وَلا مَنْـجـا مِنْـكَ إِلاّ إِلَـيْكَ، آمَنْـتُ بِكِتـابِكَ الّـذي أَنْزَلْـتَ وَبِنَبِـيِّـكَ الّـذي أَرْسَلْـت. "));
            list.add(new RemembranceDetailsModel(33, "سُبْحَانَ اللَّهِ. "));
            list.add(new RemembranceDetailsModel(33, "الْحَمْدُ لِلَّهِ. "));
            list.add(new RemembranceDetailsModel(34, "اللَّهُ أَكْبَرُ. "));
            list.add(new RemembranceDetailsModel(3, "يجمع كفيه ثم ينفث فيهما والقراءة فيهما\u200F:\u200F \u200F{\u200Fقل هو الله أحد\u200F}\u200F و\u200F{\u200Fقل أعوذ برب الفلق\u200F}\u200F و\u200F{\u200Fقل أعوذ برب الناس\u200F}\u200F ومسح ما استطاع من الجسد يبدأ بهما على رأسه ووجه وما أقبل من جسده. "));
            list.add(new RemembranceDetailsModel(1, "سورة البقرة: أَعُوذُ بِاللهِ مِنْ الشَّيْطَانِ الرَّجِيمِ\n" + "آمَنَ الرَّسُولُ بِمَا أُنْزِلَ إِلَيْهِ مِنْ رَبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِنْ رُسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ. لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ رَبَّنَا لَا تُؤَاخِذْنَا إِنْ نَسِينَا أَوْ أَخْطَأْنَا رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِنْ قَبْلِنَا رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا أَنْتَ مَوْلَانَا فَانْصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ. [البقرة 285 - 286] "));
            list.add(new RemembranceDetailsModel(1, "آية الكرسى: أَعُوذُ بِاللهِ مِنْ الشَّيْطَانِ الرَّجِيمِ\n" + "اللّهُ لاَ إِلَـهَ إِلاَّ هُوَ الْحَيُّ الْقَيُّومُ لاَ تَأْخُذُهُ سِنَةٌ وَلاَ نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الأَرْضِ مَن ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلاَّ بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلاَ يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلاَّ بِمَا شَاء وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالأَرْضَ وَلاَ يَؤُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ. [البقرة 255] "));
            binding.recyclerview.setAdapter(adapter);

        }
        else if (id.equals("6")) {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            adapter = new RemembranceDetailsAdapter(this, list, null);
            list.add(new RemembranceDetailsModel(1, "الحَمْـدُ لِلّهِ الّذي أَحْـيانا بَعْـدَ ما أَماتَـنا وَإليه النُّـشور."));
            list.add(new RemembranceDetailsModel(1, "الحمدُ للهِ الذي عافاني في جَسَدي وَرَدّ عَليّ روحي وَأَذِنَ لي بِذِكْرِه. "));
            list.add(new RemembranceDetailsModel(1, "لا إلهَ إلاّ اللّهُ وَحْـدَهُ لا شَـريكَ له، لهُ المُلـكُ ولهُ الحَمـد، وهوَ على كلّ شيءٍ قدير، سُـبْحانَ اللهِ، والحمْـدُ لله ، ولا إلهَ إلاّ اللهُ واللهُ أكبَر، وَلا حَولَ وَلا قوّة إلاّ باللّهِ العليّ العظيم. رَبِّ اغْفرْ لي. "));
            binding.recyclerview.setAdapter(adapter);

        }


    }

    private List<RemembranceDetailsModel> convertJsonToChaptersList(String json) {
        List<RemembranceDetailsModel> chaptersList = new ArrayList<>();
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chaptersList;
    }


}