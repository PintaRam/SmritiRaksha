package com.smritiraksha.Patient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smritiraksha.Constants;
import com.smritiraksha.Doctor.PrescriptionAdapter;
import com.smritiraksha.R;
import com.smritiraksha.Doctor.Prescription;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MedicationActivity extends AppCompatActivity {
    private Button backToReminderBtn;

    private RecyclerView recyclerView;
    private PrescriptionAdapter adapter;
    private ArrayList<Prescription> prescriptionList = new ArrayList<>();
    private String patientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backToReminderBtn = findViewById(R.id.btnBackToReminder);

        backToReminderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MedicationActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "MedicalReminderFragment");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewPrescriptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        patientEmail = getIntent().getStringExtra("patientEmail");
        if (patientEmail != null) {
            fetchPrescriptions(patientEmail);
        } else {
            Toast.makeText(this, "No patient selected.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Same behavior when physical back button is pressed
        super.onBackPressed();
        backToReminderBtn.performClick();
    }
    private void fetchPrescriptions(String email) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(Constants.GET_Patinet_Prescription + "?email=" + email);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                    in.close();
                    return result.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            JSONArray jsonArray = jsonObject.getJSONArray("prescriptions");
                            Log.d("API Response", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                prescriptionList.add(new Prescription(
                                        String.valueOf(obj.getInt("id")),
                                        obj.getString("patient_email"),
                                        obj.getString("title"),
                                        obj.getString("description"),
                                        String.valueOf(obj.getInt("hour")),
                                        String.valueOf(obj.getInt("minute")),
                                        obj.getString("created_at")
                                ));
                            }

                            adapter = new PrescriptionAdapter(MedicationActivity.this, prescriptionList, prescription -> {
                                // Handle edit if needed
                            });
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(MedicationActivity.this, "Error from server.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MedicationActivity.this, "Failed to parse JSON.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MedicationActivity.this, "No data received.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}