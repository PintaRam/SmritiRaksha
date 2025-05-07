package com.smritiraksha.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.Adapters.PatientAdapter;
import com.smritiraksha.Constants;
import com.smritiraksha.Models.Patients;
import com.smritiraksha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatientSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Patients> patientList = new ArrayList<>();
    private PatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewPatients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PatientAdapter(this, patientList, patientId -> {
            // Find the selected patient object by ID
            for (Patients patient : patientList) {
                if (patient.getId().equals(patientId)) {
                    Intent intent = new Intent(PatientSelectionActivity.this, DoctorDashboard.class);
                    intent.putExtra("patient_id", patient.getId());
                    intent.putExtra("patient_name", patient.getName());
                    intent.putExtra("doemail",getIntent().getStringExtra("DocEmail"));
                    Log.d("IntentDebug", "Selected Patient ID: " + patient.getId());
                    Log.d("IntentDebug", "Selected Patient Name: " + patient.getName());
                    Log.d("IntentDebug", "Doctor Email from Intent: " + getIntent().getStringExtra("DocEmail"));
                    startActivity(intent);
                    finish(); // Close this activity
                    break;
                }
            }
        });

        recyclerView.setAdapter(adapter);

        fetchPatients();
    }

    private void fetchPatients() {
        String url = Constants.All_Patinets;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        boolean error = root.getBoolean("error");

                        if (!error) {
                            JSONArray array = root.getJSONArray("patients");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String id = String.valueOf(obj.getInt("patient_id")); // Convert int to String
                                String name = obj.getString("patient_name");

                                patientList.add(new Patients(id, name));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Error in response", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch patients,No Network...", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

}
