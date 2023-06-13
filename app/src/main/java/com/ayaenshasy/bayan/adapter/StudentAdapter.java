package com.ayaenshasy.bayan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.databinding.ItemStudentMainBinding;
import com.ayaenshasy.bayan.databinding.SoraItemBinding;
import com.ayaenshasy.bayan.model.user.Student;
import com.ayaenshasy.bayan.model.user.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> list;
    Context context;
    boolean mark = false;

    public StudentAdapter(List<Student> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public void setStudents(List<Student> students) {
        this.list = students;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentMainBinding binding = ItemStudentMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true).into(holder.binding.imgUser);
        holder.binding.tvName.setText(list.get(position).getName());
        holder.binding.tvId.setText(list.get(position).getId());
        Toast.makeText(context, list.get(position).getId()+"", Toast.LENGTH_SHORT).show();
        holder.binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                } else {
//                    Intent in=new Intent(context,)
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemStudentMainBinding binding;

        public ViewHolder(ItemStudentMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


