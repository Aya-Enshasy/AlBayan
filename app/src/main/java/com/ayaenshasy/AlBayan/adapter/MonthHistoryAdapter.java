package com.ayaenshasy.AlBayan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 import com.ayaenshasy.AlBayan.databinding.ExamItemBinding;
import com.ayaenshasy.AlBayan.databinding.MonthelyItemBinding;
import com.ayaenshasy.AlBayan.model.Attendance;

import java.util.List;
import java.util.Map;

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
        Attendance attendance = list.get(position);
        holder.binding.tvDate.setText("  " + list.get(position).getDate());
        holder.binding.tvRemmeber.setText("حفظ اليوم : " + attendance.getPlanToday() + " نسبة الحفظ " + attendance.getTodayPercentage());
        holder.binding.tvPrayers.setText("الصلوات لهذا اليوم: ");
        StringBuilder prayersText = new StringBuilder();

        Map<String, Boolean> islamicPrayers = attendance.getIslamicPrayers();
        if (islamicPrayers != null) {
            for (Map.Entry<String, Boolean> entry : islamicPrayers.entrySet()) {
                String prayerName = entry.getKey();
                boolean isPrayerMarked = entry.getValue();
                if (isPrayerMarked) {
                    prayersText.append(prayerName).append(" , ");
                }
            }
        }

        if (prayersText.length() > 0) {
            // Remove the trailing comma and space
            prayersText.setLength(prayersText.length() - 2);
            holder.binding.tvPrayers.append(prayersText);
        } else {
            holder.binding.tvPrayers.append("لم يتم التسجيل ");
        }

        holder.binding.tvParentRate.append(attendance.getRateParent() != null ? attendance.getRateParent() : "لم يتم التسجيل");
        holder.binding.tvTeacherRate.append(attendance.getRateTeacher() != null ? attendance.getRateTeacher() : "لم يتم التسجيل");


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


