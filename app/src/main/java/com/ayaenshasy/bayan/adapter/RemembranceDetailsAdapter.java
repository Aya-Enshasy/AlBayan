package com.ayaenshasy.bayan.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.RemembranceDetailsItemBinding;
import com.ayaenshasy.bayan.listeners.RemembranceListener;
import com.ayaenshasy.bayan.model.RemembranceDetailsModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class RemembranceDetailsAdapter extends RecyclerView.Adapter<RemembranceDetailsAdapter.ViewHolder> {
    private ArrayList<RemembranceDetailsModel> list  ;
    Context context;
    RemembranceListener listener;

     public RemembranceDetailsAdapter(Context context, ArrayList<RemembranceDetailsModel> list , RemembranceListener listener ){
        this.context= context;
         this.list = list;
         this.listener = listener;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RemembranceDetailsItemBinding binding = RemembranceDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

     @SuppressLint("SetTextI18n")
     @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
     public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
         RemembranceDetailsModel data = list.get(position);

         holder.binding.text.setText(data.getText());
         holder.binding.repeat.setText(String.valueOf(data.getRepeat()));

         holder.binding.share.setOnClickListener(v -> {
             listener.onClick(data.getText());
         });

         AtomicInteger count = new AtomicInteger(data.getRepeat());

         holder.binding.constraintlayout.setOnClickListener(v -> {
             if (count.get() == 0) {
                 holder.binding.repeat.setEnabled(false);
                 holder.binding.repeat.setText(String.valueOf(0));
                 holder.binding.constraintlayout.setBackgroundColor(context.getColor(com.google.android.material.R.color.design_default_color_error));
             } else {
                 count.getAndDecrement();
                 holder.binding.constraintlayout.setBackgroundColor(context.getColor(R.color.orange));
                 holder.binding.repeat.setText(String.valueOf(count.get()));
             }
         });
     }



    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder  {
        RemembranceDetailsItemBinding binding;
        public ViewHolder(RemembranceDetailsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }


    }

}
