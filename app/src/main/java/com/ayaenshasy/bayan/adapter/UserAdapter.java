package com.ayaenshasy.bayan.adapter;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;
import static com.ayaenshasy.bayan.utils.Constant.USER_NAME;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.ui.activities.StudentDetailsActivity;
import com.ayaenshasy.bayan.databinding.ItemUserMainBinding;
import com.ayaenshasy.bayan.listeners.DataListener;
import com.ayaenshasy.bayan.model.Role;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.ui.activities.UserDetailsActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> list;
    Context context;
    DataListener<User> listener;


    public UserAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setStudents(List<User> users) {
        this.list = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserMainBinding binding = ItemUserMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvName.setText(list.get(position).getName());
        holder.binding.tvId.setText(list.get(position).getId()+"");


        holder.itemView.setOnClickListener(View -> {
            if (list.get(position).getRole()== Role.STUDENT){
                context.startActivity(new Intent(context, StudentDetailsActivity.class)
                        .putExtra(USER_NAME, list.get(position).getName())
                        .putExtra(USER_ID, list.get(position).getId())
                );
            }else{
                context.startActivity(new Intent(context, UserDetailsActivity.class)
                        .putExtra(USER_NAME, list.get(position).getName())
                        .putExtra(USER_ID, list.get(position).getId())
                );
            }

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserMainBinding binding;

        public ViewHolder(ItemUserMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


