package com.ayaenshasy.bayan.adapter;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;
import static com.ayaenshasy.bayan.utils.Constant.USER_NAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.ui.activities.StudentDetailsActivity;
import com.ayaenshasy.bayan.databinding.ItemStudentMainBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.user.Student;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> list;
    Context context;
    boolean mark = false;
    DataListener<Student> listener;

    public StudentAdapter(List<Student> list, Context context, DataListener<Student> listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.ic_user_circle_svgrepo_com)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.binding.imgUser);
        holder.binding.tvName.setText(list.get(position).getName());
        holder.binding.tvId.setText(list.get(position).getId());

        holder.binding.checkBox.setOnCheckedChangeListener(null); // Remove previous listener to avoid conflicts
        holder.binding.checkBox.setEnabled(!list.get(position).isChecked());
        holder.binding.checkBox.setChecked(list.get(position).isChecked());
        holder.binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    listener.sendData(list.get(position));
//                holder.binding.checkBox.setEnabled(false);
            }
        });


        holder.itemView.setOnClickListener(View -> {
            context.startActivity(new Intent(context, StudentDetailsActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(USER_NAME, list.get(position).getName())
                    .putExtra(USER_ID, list.get(position).getId())
            );
        });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemStudentMainBinding binding;

        public ViewHolder(ItemStudentMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


