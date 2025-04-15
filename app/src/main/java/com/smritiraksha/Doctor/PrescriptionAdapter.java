package com.smritiraksha.Doctor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smritiraksha.R;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private Context context;
    private List<Prescription> prescriptionList;

    public PrescriptionAdapter(Context context, List<Prescription> prescriptionList) {
        this.context = context;
        this.prescriptionList = prescriptionList;
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

        holder.title.setText("Title: " + prescription.getTitle());
        holder.description.setText("Description: " + prescription.getDescription());
        holder.time.setText("Time: " + prescription.getHour() + ":" + prescription.getMinute());

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), EditPrescriptionActivity.class);
            // Passing Prescription object to EditPrescriptionActivity
            intent.putExtra("prescription", prescription);
            context.startActivity(intent);
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
