package com.smritiraksha;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
public class patient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient);

        // Input Fields
        TextInputEditText etPatientName = findViewById(R.id.et_patient_name);
        TextInputEditText etPatientID = findViewById(R.id.et_patient_id);
        TextInputEditText etContact = findViewById(R.id.et_contact);
        AutoCompleteTextView actSex = findViewById(R.id.act_sex);
        AutoCompleteTextView actGuideID = findViewById(R.id.act_guide_id);
        AutoCompleteTextView actGuideName = findViewById(R.id.act_guide_name);

        // Submit Button
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);

        // Dropdown Data
        String[] sexOptions = {"Male", "Female", "Other"};
        String[] guideIDs = {"G101", "G102", "G103"};
        String[] guideNames = {"John Doe", "Jane Smith", "Michael Brown"};

        // Adapters for Dropdowns
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sexOptions);
        ArrayAdapter<String> guideIDAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, guideIDs);
        ArrayAdapter<String> guideNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, guideNames);

        actSex.setAdapter(sexAdapter);
        actGuideID.setAdapter(guideIDAdapter);
        actGuideName.setAdapter(guideNameAdapter);

        // Submit Button Click Listener
        btnSubmit.setOnClickListener(v -> {
            String patientName = etPatientName.getText().toString();
            String patientID = etPatientID.getText().toString();
            String contact = etContact.getText().toString();
            String sex = actSex.getText().toString();
            String guideID = actGuideID.getText().toString();
            String guideName = actGuideName.getText().toString();

            if (patientName.isEmpty() || patientID.isEmpty() || contact.isEmpty() || sex.isEmpty() || guideID.isEmpty() || guideName.isEmpty()) {
                Toast.makeText(patient.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Handle form submission
                Toast.makeText(patient.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
