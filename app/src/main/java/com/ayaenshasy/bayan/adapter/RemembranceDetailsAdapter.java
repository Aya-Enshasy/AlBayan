package com.ayaenshasy.bayan.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.RemembranceDetailsItemBinding;
import com.ayaenshasy.bayan.listeners.RemembranceListener;
import com.ayaenshasy.bayan.model.RemembranceDetailsModel;
import com.ayaenshasy.bayan.utils.AppPreferences;
import com.ayaenshasy.bayan.utils.Constant;

import java.util.ArrayList;


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
        holder.binding.text.setText(list.get(position) .getText());
        holder.binding.repeat.setText("التكرار : 0"+ list.get(position) .getRepeat());

        holder.binding.share.setOnClickListener(View->{
            listener.onClick( list.get(position).getText());

        });

 int id = AppPreferences.getInstance(context).getIntegerPreferences(Constant.Remembrance_Id);

//         if (id== 1) {
//             holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.orange);
//         } else if (id == 2) {
//             holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.parbel);
//         } else if (id == 3) {
//             holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.blue);
//         } else if (id == 4) {
//             holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.green);
//         } else if (id == 5) {
//             holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.teel);
//         } else if (id == 6) {
//             holder.binding.ConstraintLayout.setBackgroundResource(R.drawable.red);
//         }

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
