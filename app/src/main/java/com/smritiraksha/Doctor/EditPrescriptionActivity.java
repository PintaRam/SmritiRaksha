package com.smritiraksha.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.smritiraksha.R;

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

        // Get the prescription object passed from the adapter
        Intent intent = getIntent();
        Prescription prescription = (Prescription) intent.getSerializableExtra("prescription");

        // Prefill the data into the EditText fields
        if (prescription != null) {
            titleEdit.setText(prescription.getTitle());
            descriptionEdit.setText(prescription.getDescription());
            hourEdit.setText(prescription.getHour());
            minuteEdit.setText(prescription.getMinute());
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
}
