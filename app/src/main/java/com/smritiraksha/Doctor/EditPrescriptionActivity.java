package com.smritiraksha.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPrescriptionActivity extends AppCompatActivity {

    private EditText titleEdit, descriptionEdit, hourEdit, minuteEdit;
    private Button backButton,updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_prescription);

        titleEdit = findViewById(R.id.editTitle);
        descriptionEdit = findViewById(R.id.editDescription);
        hourEdit = findViewById(R.id.editHour);
        minuteEdit = findViewById(R.id.editMinute);
        updateBtn = findViewById(R.id.updateButton);
        backButton=findViewById(R.id.medicinebck);

        Intent intent = getIntent();
        Prescription prescription = (Prescription) intent.getSerializableExtra("prescription");

        if (prescription != null) {
            titleEdit.setText(prescription.getTitle());
            descriptionEdit.setText(prescription.getDescription());
            hourEdit.setText(prescription.getHour());
            minuteEdit.setText(prescription.getMinute());

            updateBtn.setOnClickListener(view -> {
                // Send update request
                updatePrescriptionInServer(
                        prescription.getId(),
                        prescription.getPatientId(), // should be email
                        titleEdit.getText().toString(),
                        descriptionEdit.getText().toString(),
                        hourEdit.getText().toString(),
                        minuteEdit.getText().toString()
                );
            });
        }



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("return_to_fragment", "MedicationFragment");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void updatePrescriptionInServer(String id, String patientEmail, String title, String description, String hour, String minute) {
        String url = Constants.UPD_PATIENT_PRescription;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);  // This will trigger the fragment to refresh
                            finish();
                        }
                    else {
                            Toast.makeText(this, "Update failed: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("patient_email", patientEmail);
                params.put("title", title);
                params.put("description", description);
                params.put("hour", hour);
                params.put("minute", minute);
                return params;
            }
        };

        queue.add(request);
    }

}
