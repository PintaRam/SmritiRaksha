package com.smritiraksha.Patient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.Adapters.PatientAdapter;
import com.smritiraksha.CareTaker.guidesMainActivity;
import com.smritiraksha.Constants;
import com.smritiraksha.Models.Patients;
import com.smritiraksha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class select_patients extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private PatientAdapter adapter;
    private ArrayList<Patients> patientList;
    private TextView emptyText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_patients);

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_view);
        searchBar = findViewById(R.id.search_bar);
        emptyText = findViewById(R.id.empty_text);

        // Initialize the list and adapter
        patientList = new ArrayList<>();
        adapter = new PatientAdapter(this, patientList, patientId -> {
            // On item click, navigate to MainActivity and pass the selected patient ID
            Intent intent = new Intent(select_patients.this, guidesMainActivity.class);
            intent.putExtra("PATIENT_ID", patientId);  // Passing the patient ID
            startActivity(intent);
        });

        // Fetch patient data
        fetchPatients();

        // Set the layout manager and adapter to the recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Implement search bar filtering
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);  // Trigger filter as text changes
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Automatically trigger the filter when the screen loads
        new Handler().postDelayed(() -> {
            adapter.getFilter().filter(searchBar.getText().toString());
        }, 100); // small delay to ensure data is fetched before applying filter
    }

    private void fetchPatients() {
        String url = Constants.getPatients;

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("request_key", "your_request_value");
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                response -> {
                    try {
                        boolean error = response.getBoolean("error");
                        if (!error) {
                            JSONArray patientsArray = response.getJSONArray("patients");
                            for (int i = 0; i < patientsArray.length(); i++) {
                                JSONObject patient = patientsArray.getJSONObject(i);
                                String id = patient.getString("patient_id");
                                String name = patient.getString("patient_name");

                                patientList.add(new Patients(id, name));  // Add patient to the list
                            }

                            // Notify the adapter to update RecyclerView
                            adapter.notifyDataSetChanged();
                            emptyText.setVisibility(View.GONE);  // Hide empty state if patients are found
                        } else {
                            emptyText.setVisibility(View.VISIBLE);  // Show empty state if no patients
                            Toast.makeText(select_patients.this, "No patients found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Error parsing response: " + e.getMessage());
                        emptyText.setVisibility(View.VISIBLE);  // Show empty state in case of error
                        Toast.makeText(select_patients.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VOLLEY_ERROR", "Error: " + error.toString());
                    emptyText.setVisibility(View.VISIBLE);  // Show empty state on error
                    Toast.makeText(select_patients.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
