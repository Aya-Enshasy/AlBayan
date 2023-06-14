package com.ayaenshasy.bayan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.databinding.SoraItemBinding;
import com.ayaenshasy.bayan.model.quran.Verse;

import java.util.List;

public class SuraAdapter extends RecyclerView.Adapter<SuraAdapter.ViewHolder> {

    private List<Verse> list;
    Context context;
    boolean mark = false;

    public SuraAdapter(List<Verse> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SoraItemBinding binding = SoraItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.text.setText(list.get(position).getText());
        holder.binding.id.setText(list.get(position).getId()+"");
        if (position == 0){
            holder.binding.textView4.setVisibility(View.VISIBLE);
        }else {
            holder.binding.textView4.setVisibility(View.GONE);

        }

        holder.binding.mark.setOnClickListener(View->{
            if(!mark){
                mark=true;
                holder.binding.mark.setImageResource(R.drawable.ic_baseline_bookmark_24);
            }else {
                mark=false;
                holder.binding.mark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SoraItemBinding binding;

        public ViewHolder(SoraItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


