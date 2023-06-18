package com.ayaenshasy.bayan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.ExamItemBinding;
import com.ayaenshasy.bayan.databinding.MonthelyItemBinding;
import com.ayaenshasy.bayan.model.Attendance;
import com.ayaenshasy.bayan.model.Exam;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class MonthHistoryAdapter extends RecyclerView.Adapter<MonthHistoryAdapter.ViewHolder> {

    private List<Attendance> list;
    Context context;

    public MonthHistoryAdapter(List<Attendance> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public void setExam(List<Attendance> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MonthelyItemBinding binding = MonthelyItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.tvDate.setText("  "+list.get(position).getDate());


    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        MonthelyItemBinding binding;

        public ViewHolder(MonthelyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


