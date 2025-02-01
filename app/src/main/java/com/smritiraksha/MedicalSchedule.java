package com.smritiraksha;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import com.smritiraksha.Constants;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class MedicalSchedule extends AppCompatActivity {
    private TextInputLayout medicineNameLayout, dosageLayout;
    private TextInputEditText medicineName, dosage;
    private TimePicker timePicker;
    private MaterialButton setReminderButton;
    private Button medbckbtn;
    private final String API_URL= Constants.Remainder_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medical_schedule);
        // Initialize UI elements
        medicineNameLayout = findViewById(R.id.medicine_name_layout);
        dosageLayout = findViewById(R.id.dosage_layout);
        medicineName = findViewById(R.id.medicine_name);
        dosage = findViewById(R.id.dosage);
        timePicker = findViewById(R.id.time_picker);
        setReminderButton = findViewById(R.id.set_reminder_button);

        setReminderButton.setOnClickListener(v -> submitMedicationReminder(v));

        medbckbtn = findViewById(R.id.button);
        medbckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlebckbutton();
            }
        });

    }

    private  void handlebckbutton()
    {
        // Handle back navigation to `guideRem` fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        remainder_guide fragment = new remainder_guide();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Optional
        fragmentTransaction.commit();
    }

    private void submitMedicationReminder(View view) {
        String medicine = medicineName.getText().toString().trim();
        String dose = dosage.getText().toString().trim();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Validation
        boolean isValid = true;

        if (medicine.isEmpty()) {
            medicineNameLayout.setError("Medicine name is required");
            isValid = false;
        } else {
            medicineNameLayout.setError(null);
        }

        if (dose.isEmpty()) {
            dosageLayout.setError("Dosage is required");
            isValid = false;
        } else {
            dosageLayout.setError(null);
        }

        if (!isValid) return; // Stop if validation fails

        // Prepare data for API
        Map<String, String> params = new HashMap<>();
        params.put("medicine", medicine);
        params.put("dosage", dose);
        params.put("hour", String.valueOf(hour));
        params.put("minute", String.valueOf(minute));
        params.put("patient_id", "123"); // Replace with actual patient ID
        params.put("guide_id","test");

        sendToServer(params, view);
    }

    private void sendToServer(Map<String, String> params, View view) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, API_URL,
                response -> Snackbar.make(view, "Reminder Set Successfully!", Snackbar.LENGTH_LONG).show(),
                error -> Snackbar.make(view, "Failed to set reminder", Snackbar.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(request);
    }
}