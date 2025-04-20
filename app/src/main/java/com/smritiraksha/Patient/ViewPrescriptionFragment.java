package com.smritiraksha.Patient;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewPrescriptionFragment extends Fragment {

    private LinearLayout layoutPrescriptions;

    // Replace this with your actual API endpoint
    private static final String PRESCRIPTION_URL = "http://YOUR_IP/smritiraksha/get_prescriptions.php?patient_id=PT04sri";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_prescription, container, false);
        layoutPrescriptions = view.findViewById(R.id.layout_prescriptions);
        fetchPrescriptionsFromServer();

        return view;
    }

    private void fetchPrescriptionsFromServer() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, PRESCRIPTION_URL, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject prescription = response.getJSONObject(i);

                            String time = prescription.getString("time");
                            String medicine = prescription.getString("medicine");
                            String diet = prescription.getString("diet");

                            // Dynamically create a TextView for each prescription
                            TextView textView = new TextView(requireContext());
                            textView.setText("⏰ " + time + "\n💊 " + medicine + "\n🥗 " + diet);
                            textView.setPadding(20, 30, 20, 30);
                            textView.setTextSize(18f);
                            textView.setBackgroundResource(R.drawable.prescription_card_bg); // Optional, create drawable
                            textView.setTextColor(getResources().getColor(android.R.color.white));

                            layoutPrescriptions.addView(textView);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Failed to parse data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to fetch prescriptions", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
