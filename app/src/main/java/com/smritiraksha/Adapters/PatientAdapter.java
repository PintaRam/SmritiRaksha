package com.smritiraksha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.smritiraksha.Models.Patients;
import com.smritiraksha.R;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Patients> patientList;
    private ArrayList<Patients> patientListFull;
    private PatientClickListener listener;

    public interface PatientClickListener {
        void onPatientClick(String patientId);  // or change to onPatientClick(Patients patient) if needed
    }

    public PatientAdapter(Context context, ArrayList<Patients> patientList, PatientClickListener listener) {
        this.context = context;
        this.patientList = patientList;
        this.listener = listener;
        this.patientListFull = new ArrayList<>(patientList);
    }

    public PatientAdapter(Context context, ArrayList<Patients> patientList) {
        this.context = context;
        this.patientList = patientList;
        this.patientListFull = new ArrayList<>(patientList);
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patients patient = patientList.get(position);
        holder.patientName.setText(patient.getName());
        holder.patientId.setText("ID: " + patient.getId());  // 🔁 ADDED: show patient ID
        holder.itemView.setOnClickListener(v -> listener.onPatientClick(patient.getId())); // Optionally pass whole object
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    @Override
    public Filter getFilter() {
        return patientFilter;
    }

    private final Filter patientFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Patients> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(patientListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Patients patient : patientListFull) {
                    if (patient.getName().toLowerCase().contains(filterPattern) ||
                            patient.getId().toLowerCase().contains(filterPattern)) {
                        filteredList.add(patient);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            patientList.clear();
            if (results.values != null) {
                patientList.addAll((List<Patients>) results.values);
            }
            notifyDataSetChanged();
        }
    };

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientId;  // 🔁 ADDED: patient ID reference

        public PatientViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            patientId = itemView.findViewById(R.id.patient_id);  // 🔁 ADDED: make sure this ID exists in layout
        }
    }
}
