package com.ayaenshasy.AlBayan.adapter;

import static com.ayaenshasy.AlBayan.utils.Constant.USER_ID;
import static com.ayaenshasy.AlBayan.utils.Constant.USER_NAME;
import static com.ayaenshasy.AlBayan.utils.Constant.USER_ROLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.databinding.ItemUserMainBinding;
import com.ayaenshasy.AlBayan.listeners.DataListener;
import com.ayaenshasy.AlBayan.listeners.DeleteListener;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.user.User;
import com.ayaenshasy.AlBayan.ui.activities.StudentDetailsActivity;
import com.ayaenshasy.AlBayan.ui.activities.UserDetailsActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> list;
    Context context;
    DataListener<User> listener;
    DeleteListener delete;


    public UserAdapter(List<User> list, Context context,DeleteListener delete) {
        this.list = list;
        this.context = context;
        this.delete = delete;
    }

    public void setUsers(List<User> users) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.tvName.setText(list.get(position).getName());
        holder.binding.tvId.setText(list.get(position).getId()+"");
        Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.ic_user_circle_svgrepo_com)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.binding.imgUser);

        Log.e("dnc,kdsc,n",list.get(position).getImage()+"");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getRole() == Role.STUDENT) {
                    context.startActivity(new Intent(context, StudentDetailsActivity.class)
                            .putExtra(USER_NAME, list.get(position).getName())
                            .putExtra(USER_ID, list.get(position).getId())
                            .putExtra(USER_ROLE, list.get(position).getRole().name())
                    );
                } else {
                    context.startActivity(new Intent(context, UserDetailsActivity.class)
                            .putExtra(USER_NAME, list.get(position).getName())
                            .putExtra(USER_ID, list.get(position).getId())
                            .putExtra(USER_ROLE, list.get(position).getRole().name())
                    );
                }
            }
        });


        holder.binding.imgDelete.setOnClickListener(View->{
            delete.onClick(list.get(position).getId(),position);

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


