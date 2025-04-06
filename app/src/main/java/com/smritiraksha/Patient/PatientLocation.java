package com.smritiraksha.Patient;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.Adapters.LocationAdapter;
import com.smritiraksha.Models.LocationModel;
import com.smritiraksha.R;
import com.smritiraksha.CareTaker.remainder_guide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PatientLocation extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LocationAdapter locationAdapter;
    private List<LocationModel> locationList;
    private static final String URL = "https://yourwebsite.com/api/fetch_locations.php?patient_id=1"; // Change URL to your PHP endpoint
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_location);


        Button backButton = findViewById(R.id.button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back navigation to `guideRem` fragment
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                remainder_guide fragment = new remainder_guide();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null); // Optional
                fragmentTransaction.commit();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationList = new ArrayList<>();
        locationAdapter = new LocationAdapter(locationList);
        recyclerView.setAdapter(locationAdapter);
        addSampleLocations();

        //Need to update back part after that we need to work on fetchlocation()
        // fetchLocations();
    }

    private void addSampleLocations() {
        // Sample data (add locations to the list)
        locationList.add(new LocationModel(1, "Hospital XYZ", "2025-01-15 10:30 AM"));
        locationList.add(new LocationModel(2, "Clinic ABC", "2025-01-16 02:15 PM"));
        locationList.add(new LocationModel(3, "Pharmacy 123", "2025-01-17 09:00 AM"));
        locationList.add(new LocationModel(4, "Park Downtown", "2025-01-18 11:00 AM"));
        locationList.add(new LocationModel(5, "Supermarket 456", "2025-01-19 03:45 PM"));
        // Notify adapter that data has changed
        locationAdapter.notifyDataSetChanged();
    }

    private void fetchLocations() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Prepare the request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Parse JSON response
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject locationObject = response.getJSONObject(i);
                                int id = locationObject.getInt("id");
                                String location_name = locationObject.getString("location_name");
                                String visit_time = locationObject.getString("visit_time");

                                LocationModel location = new LocationModel(id, location_name, visit_time);
                                locationList.add(location);
                            }

                            // Notify the adapter about data changes
                            locationAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue
        queue.add(jsonArrayRequest);
    }
}