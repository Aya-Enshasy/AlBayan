package com.ayaenshasy.bayan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.ExamItemBinding;
import com.ayaenshasy.bayan.databinding.SoraItemBinding;
import com.ayaenshasy.bayan.model.Exam;
import com.ayaenshasy.bayan.model.quran.Verse;
import com.ayaenshasy.bayan.model.user.Student;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    private List<Exam> list;
    Context context;

    public ExamAdapter(List<Exam> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public void setExam(List<Exam> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExamItemBinding binding = ExamItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.tvName.setText("الاسم : "+list.get(position).getName());
        holder.binding.tvMosque.setText("اسم المسجد : "+list.get(position).getMosque());
        holder.binding.tvDegree.setText("الدرجة : "+list.get(position).getDegree());

        holder.binding.imgExam.setVisibility(View.GONE);
//         Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.ic_user_circle_svgrepo_com)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .skipMemoryCache(true)
//                .into(holder.binding.imgExam);



    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ExamItemBinding binding;

        public ViewHolder(ExamItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


