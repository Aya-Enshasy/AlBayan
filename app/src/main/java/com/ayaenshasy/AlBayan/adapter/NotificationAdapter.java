package com.ayaenshasy.AlBayan.adapter;


 import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.AlBayan.databinding.ItemNotificationBinding;
 import com.ayaenshasy.AlBayan.utils.Methods;
import com.ayaenshasy.AlBayan.listeners.DataListener;
import com.ayaenshasy.AlBayan.model.Notification;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.user.User;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> list;
    Context context;
    DataListener<User> listener;


    public NotificationAdapter(List<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setNotification(List<Notification> users) {
        this.list = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvMsg.setText(list.get(position).getMessage());
        holder.binding.tvTitle.setText(list.get(position).getTitle());
        Timestamp timestamp = list.get(position).getTimestamp();
        Date date = timestamp.toDate();
        String formattedDate = Methods.formatJsonDate(date);
        holder.binding.tvDate.setText(formattedDate);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        public ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


