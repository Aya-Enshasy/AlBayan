package com.ayaenshasy.AlBayan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.databinding.SoraItemBinding;
import com.ayaenshasy.AlBayan.listeners.SoraListener;
import com.ayaenshasy.AlBayan.model.quran.Verse;

import java.util.List;

public class SuraAdapter extends RecyclerView.Adapter<SuraAdapter.ViewHolder> {

    private List<Verse> list;
    Context context;
    SoraListener listener;
    float fontSize;

    public SuraAdapter(List<Verse> list, Context context, SoraListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        this.fontSize = 22;
    }

    public void updateFontSize(float fontSize) {
        this.fontSize = fontSize;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SoraItemBinding binding = SoraItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Verse verse = list.get(position);
        holder.binding.text.setText(verse.getText());
        holder.binding.id.setText(String.valueOf(verse.getId()));
        listener.onClick(holder.binding.text);

        holder.binding.text.setTextSize(fontSize);

        if (position == 0) {
            holder.binding.textView4.setVisibility(View.VISIBLE);
        } else {
            holder.binding.textView4.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SoraItemBinding binding;

        public ViewHolder(SoraItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


