package com.ayaenshasy.AlBayan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.utils.Constant;
import com.ayaenshasy.AlBayan.ui.activities.SoraActivity;
import com.ayaenshasy.AlBayan.model.QuranChapter;

import java.util.List;

public class QuranChapterAdapter extends RecyclerView.Adapter<QuranChapterAdapter.ViewHolder> {

    private final List<QuranChapter> chaptersList;
    Context context;

    public QuranChapterAdapter(List<QuranChapter> chaptersList,Context context) {
        this.chaptersList = chaptersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View quranChapterView = inflater.inflate(R.layout.item_quran_chapter, parent, false);

        return new ViewHolder(quranChapterView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuranChapter quranChapter = chaptersList.get(position);

        TextView textViewChapterName = holder.textViewChapterName;
        TextView textViewChapterNumber = holder.textViewChapterNumber;
        TextView type = holder.type;
        ImageView imageView3 = holder.imageView3;

        textViewChapterName.setText(quranChapter.getName());
        textViewChapterNumber.setText(quranChapter.getNum()+"");
        if (quranChapter.getType().equals("meccan")){
            type.setText("سورة مكية");
            imageView3.setImageResource(R.drawable.ic_kaaba_svgrepo_com);
        }else {
            type.setText("سورة مدنية");
            imageView3.setImageResource(R.drawable.ic_mosque_svgrepo_com);
        }

        holder.itemView.setOnClickListener(View->{
            context.startActivity(new Intent(context, SoraActivity.class)
                    .putExtra(Constant.AYA_ID,quranChapter.getNum()+"")
                    .putExtra(Constant.AYA_NAME,quranChapter.getName()+"")
            );
        });
    }

    @Override
    public int getItemCount() {
        return chaptersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewChapterName;
        TextView textViewChapterNumber;
        TextView type;
        ImageView imageView3;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewChapterName = itemView.findViewById(R.id.textViewChapterName);
            type = itemView.findViewById(R.id.type);
            textViewChapterNumber = itemView.findViewById(R.id.textViewChapterNumber);
            imageView3 = itemView.findViewById(R.id.imageView3);
        }
    }
}


