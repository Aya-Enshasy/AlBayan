package com.ayaenshasy.AlBayan.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.datasource.DataSource;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.databinding.ItemUserMainBinding;
import com.ayaenshasy.AlBayan.databinding.VideoItemBinding;
import com.ayaenshasy.AlBayan.model.Video;
import com.ayaenshasy.AlBayan.listeners.DataListener;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<Video> list;
    Context context;


    public VideoAdapter(List<Video> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setVideoList(List<Video> users) {
        this.list = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VideoItemBinding binding = VideoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.link.setText(list.get(position).getTitle());
        holder.binding.link.setOnClickListener(View->{
            String url = list.get(position).getUrl();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        VideoItemBinding binding;

        public ViewHolder(VideoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
          }
    }


}


