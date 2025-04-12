package com.smritiraksha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smritiraksha.Models.MonitorItem;
import com.smritiraksha.R;

import java.util.List;
public class MonitorAdapter extends RecyclerView.Adapter<MonitorAdapter.ViewHolder> {
    private List<MonitorItem> monitorList;
    private OnItemClickListener listener;

    public MonitorAdapter(List<MonitorItem> monitorList, OnItemClickListener listener) {
        this.monitorList = monitorList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MonitorItem item);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monitor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonitorItem item = monitorList.get(position);
        holder.title.setText("Patient ID: " + item.getId());
        holder.description.setText("Email: " + item.getEmail());
        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return monitorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.monitor_title);
            description = itemView.findViewById(R.id.monitor_description);
        }
    }
}
