package com.ayaenshasy.AlBayan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.databinding.ExamItemBinding;
import com.ayaenshasy.AlBayan.listeners.DataListener;
import com.ayaenshasy.AlBayan.model.Exam;
import com.ayaenshasy.AlBayan.listeners.DataListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    private List<Exam> list;
    Context context;
    DataListener<Exam> listener;

    public ExamAdapter(List<Exam> list, Context context, DataListener<Exam> listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public void setExams(List<Exam> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void updateExam(int examId, Exam updatedExam) {
        // Find the position of the item with the given exam ID in the dataset
        int position = findExamPositionById(examId);

        if (position != -1) {
            // Update the item at the specified position in your dataset
            list.set(position, updatedExam);
            notifyItemChanged(position);
        }
    }
    private int findExamPositionById(int examId) {
        for (int i = 0; i < list.size(); i++) {
            Exam exam = list.get(i);
            if (exam.getId()==(examId)) {
                return i; // Return the position if the exam ID matches
            }
        }
        return -1; // Exam ID not found in the list
    }


    public void addExam(Exam newExam) {
        // Add the new item to your dataset
        list.add(newExam);
        notifyItemInserted(list.size() - 1);
    }
    public void setExam(Exam list) {
        this.list.add(list);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.tvName.setText("الاسم : " + list.get(position).getName());
        holder.binding.tvMosque.setText("اسم المسجد : " + list.get(position).getMosque());
        holder.binding.tvDegree.setText("الدرجة : " + list.get(position).getDegree());

        Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.ic_user_circle_svgrepo_com)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.binding.imgExam);

        holder.binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sendData(list.get(position));
            }
        });

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


