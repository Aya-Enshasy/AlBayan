package com.ayaenshasy.bayan.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.RemembranceItemBinding;
import com.ayaenshasy.bayan.ui.activities.RemembranceDetailsActivity;
import com.ayaenshasy.bayan.model.RemembranceModel;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;


public class RemembranceAdapter extends RecyclerView.Adapter<RemembranceAdapter.ViewHolder> {
    private List<RemembranceModel> list;
    Context context;

    public RemembranceAdapter(Context context, List<RemembranceModel> list) {
        this.context = context;
        this.list = list;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RemembranceItemBinding binding = RemembranceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.text.setText(list.get(position).getName());

        if (position == 0) {
            Glide.with(context).load(R.drawable.sun).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(holder.binding.imageView);
            holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.orange);
        } else if (position == 1) {
             holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.parbel);
        }
        else if (position == 2) {
            Glide.with(context).load(R.drawable.pngimg_com___mosque_png25).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(holder.binding.imageView);
            holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.red);
        }
        else if (position == 3) {
            Glide.with(context).load(R.drawable.mosque).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(holder.binding.imageView);
            holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.teel);
        }
        else if (position == 4) {
            Glide.with(context).load(R.drawable.sleep).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(holder.binding.imageView);
            holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.blue);
        }
        else if (position == 5) {
           Glide.with(context).load(R.drawable.clock).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(holder.binding.imageView);
            holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.green);
        }


        holder.itemView.setOnClickListener(View -> {
            if (position == 0) {
                context.startActivity(new Intent(context, RemembranceDetailsActivity.class)
                        .putParcelableArrayListExtra(Constant.Remembrance_List, list.get(position).getList())
                        .putExtra(Constant.Remembrance_Name, list.get(position).getName() + "")
                        .putExtra(Constant.Remembrance_Id, 1 + "")
                );

            }
            else if (position == 1) {
                context.startActivity(new Intent(context, RemembranceDetailsActivity.class)
                        .putParcelableArrayListExtra(Constant.Remembrance_List, list.get(position).getList())
                        .putExtra(Constant.Remembrance_Name, list.get(position).getName() + "")
                        .putExtra(Constant.Remembrance_Id, 2+ "")
                );

            }
            else if (position == 2) {
                context.startActivity(new Intent(context, RemembranceDetailsActivity.class)
                        .putParcelableArrayListExtra(Constant.Remembrance_List, list.get(position).getList())
                        .putExtra(Constant.Remembrance_Name, list.get(position).getName() + "")
                        .putExtra(Constant.Remembrance_Id, 3+ "")
                );
            }
            else if (position == 3) {
                context.startActivity(new Intent(context, RemembranceDetailsActivity.class)
                        .putParcelableArrayListExtra(Constant.Remembrance_List, list.get(position).getList())
                        .putExtra(Constant.Remembrance_Name, list.get(position).getName() + "")
                        .putExtra(Constant.Remembrance_Id, 4+ "")
                );
            }
            else if (position == 4) {
                context.startActivity(new Intent(context, RemembranceDetailsActivity.class)
                        .putParcelableArrayListExtra(Constant.Remembrance_List, list.get(position).getList())
                        .putExtra(Constant.Remembrance_Name, list.get(position).getName() + "")
                        .putExtra(Constant.Remembrance_Id, 5+ "")
                );
            }
            else if (position == 5) {
                context.startActivity(new Intent(context, RemembranceDetailsActivity.class)
                        .putParcelableArrayListExtra(Constant.Remembrance_List, list.get(position).getList())
                        .putExtra(Constant.Remembrance_Name, list.get(position).getName() + "")
                        .putExtra(Constant.Remembrance_Id, 6+ "")
                );
            }



         });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        RemembranceItemBinding binding;

        public ViewHolder(RemembranceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }


    }

}
