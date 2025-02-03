package com.smritiraksha;

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

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Patients> patientList;
    private ArrayList<Patients> patientListFull;  // Copy of original list for filtering
    private PatientClickListener listener;

    public interface PatientClickListener {
        void onPatientClick(String patientId);
    }

    public PatientAdapter(Context context, ArrayList<Patients> patientList, PatientClickListener listener) {
        this.context = context;
        this.patientList = patientList;
        this.listener = listener;
        this.patientListFull = new ArrayList<>(patientList);  // Initialize the full patient list for filtering
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
        holder.itemView.setOnClickListener(v -> listener.onPatientClick(patient.getId()));  // Handle item click
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    @Override
    public Filter getFilter() {
        return patientFilter;
    }

    private Filter patientFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Patients> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(patientListFull);  // No filtering, return the full list
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Patients patient : patientListFull) {
                    if (patient.getName().toLowerCase().contains(filterPattern) || patient.getId().contains(filterPattern)) {
                        filteredList.add(patient);  // Add matching patients to the filtered list
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
                patientList.addAll((List) results.values);  // Update patient list with filtered data
            }
            notifyDataSetChanged();  // Notify the adapter to update the RecyclerView
        }
    };

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;

        public PatientViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);  // Assuming patient name is displayed
        }
    }
}
