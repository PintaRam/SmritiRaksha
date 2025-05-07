package com.smritiraksha.Doctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smritiraksha.R;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    public interface OnEditClickListener {
        void onEditClick(Prescription prescription);
    }

    private Context context;
    private List<Prescription> prescriptionList;
    private OnEditClickListener editClickListener;

    public PrescriptionAdapter(Context context, List<Prescription> prescriptionList, OnEditClickListener listener) {
        this.context = context;
        this.prescriptionList = prescriptionList;
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);

        holder.title.setText(prescription.getTitle());
        holder.description.setText("Description: " + prescription.getDescription());
        holder.time.setText("Time: " + prescription.getHour() + ":" + prescription.getMinute());

        holder.editBtn.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(prescription);
            }
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, time;
        ImageView editBtn;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            description = itemView.findViewById(R.id.descriptionText);
            time = itemView.findViewById(R.id.timeText);
            editBtn = itemView.findViewById(R.id.editButton);
        }
    }
}
