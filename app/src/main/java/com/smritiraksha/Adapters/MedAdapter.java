package com.smritiraksha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smritiraksha.Models.MedItem;
import com.smritiraksha.R;

import java.util.List;
public class MedAdapter extends  RecyclerView.Adapter<MedAdapter.ViewHolder>{

    private List<MedItem> medList;
    private OnItemClickListener listener;

    public MedAdapter(List<MedItem> medList, OnItemClickListener listener) {
        this.medList = medList;
        this.listener = listener;
    }

    // Define correct OnItemClickListener interface
    public interface OnItemClickListener {
        void onItemClick(MedItem item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_med, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedItem item = medList.get(position);
        holder.name.setText("Patient ID: " + item.getId());
        holder.dosage.setText("Dosage: " + item.getEmail());
        // Handle click event
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return medList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dosage;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.med_name);
            dosage = itemView.findViewById(R.id.med_dosage);
        }
    }
}
