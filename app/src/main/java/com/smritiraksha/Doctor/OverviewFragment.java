package com.smritiraksha.Doctor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  String patienEmail="john.doe@example.com";
    private LineChart visitChart;
    private ScrollView scrollview;
    private GoogleMap mMap;
    private FrameLayout mapOverlay;
    private boolean isChartAnimated = false;

    private String patientId;
    private String patientName;



    // Example data
    private List<LocationData> locationList = Arrays.asList(
            new LocationData("10:00", 13.0117, 77.6851),
            new LocationData("11:00", 13.1002, 77.5963),
            new LocationData("12:00", 12.9766, 77.5712),
            new LocationData("1:00",12.9352,77.6245)
    );

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        //args.putString("email", email); // Pass email as an argument
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            patientId = getArguments().getString("patient_id");
            patientName = getArguments().getString("patient_name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);



        Log.d("OverviewFragment", "Patient ID: " + patientId);
        Log.d("OverviewFragment", "Patient Name: " + patientName);

        // 🚀 Call the function to fetch data
        fetchPatientDetails(view);
        visitChart = view.findViewById(R.id.visit_chart);
        scrollview=view.findViewById(R.id.docscroll);
        mapOverlay = view.findViewById(R.id.map_touch_overlay);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this::onMapReady);
        drawLineChart(view);

        mapOverlay.setOnTouchListener((v, event) -> {
            // Stop ScrollView from intercepting map gestures
            scrollview.requestDisallowInterceptTouchEvent(true);
            // Allow map to handle the gesture
            return false;
        });

        return view;
        //return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (LocationData location : locationList) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Add marker
            mMap.addMarker(new MarkerOptions().position(latLng).title(location.getTime()));

            // Include in bounds
            boundsBuilder.include(latLng);
        }

        // Compute bounds and center
        LatLngBounds bounds = boundsBuilder.build();
        LatLng center = bounds.getCenter();

        // Compute the max distance from center to any point
        float[] result = new float[1];
        double maxDistance = 0;
        for (LocationData location : locationList) {
            android.location.Location.distanceBetween(
                    center.latitude, center.longitude,
                    location.getLatitude(), location.getLongitude(),
                    result
            );
            if (result[0] > maxDistance) maxDistance = result[0];
        }

        // Add a single circle covering all points
        mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(maxDistance + 120) // Add padding in meters
                .strokeColor(Color.BLACK)
                .fillColor(Color.argb(80, 0, 255, 255)) // Cyan
                .strokeWidth(2));

        // Move camera with padding around bounds
        int padding = 150; // pixels of padding around the bounds
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }



    // Method to fetch patient data and update the UI
    private void fetchPatientDetails(View view) {
        String url = Constants.FETCH_PATIENT_BYID; // Your PHP API endpoint

        // Make the network request to your API (example using Volley)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?id=" + patientId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean error = jsonResponse.getBoolean("error");

                            if (!error) {
                                JSONObject patientData = jsonResponse.getJSONObject("patient_data");

                                String name = patientData.getString("patient_name");
                                String patientId = patientData.getString("patient_name");
                                String guidename = patientData.getString("guide_name");

                                //String dementiaStage = patientData.getString("guide_name");
                                //String status = patientData.getString("Status");
                                //String lastVisit = patientData.getString("LastVisit");

                                // Update UI elements
                                TextView doctorName = view.findViewById(R.id.PatientName);
                                TextView doctorId = view.findViewById(R.id.PatientId);
                                TextView guideName=view.findViewById(R.id.GuideName);
                                TextView dementiaStageView = view.findViewById(R.id.dementiaStage);
                                TextView statusView = view.findViewById(R.id.status);
                                TextView lastVisitView = view.findViewById(R.id.PatientlastVisit);

                                doctorName.setText("🧍 " + name);
                                doctorId.setText("🆔 ID: " + patientId);
                                guideName.setText("🧑‍🤝‍🧑 "+guidename);
                                dementiaStageView.setText("🧠 Dementia Stage: ");
                                statusView.setText("📊 Status: ");
                                lastVisitView.setText("⏱ Last Visit: ");



                            } else {
                                Toast.makeText(getContext(), "Error fetching patient data", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to parse data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set up request queue and add the request to it
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);
    }


    private void drawLineChart(View view) {
        List<Entry> entries = new ArrayList<>();

        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.patientwalk); // Your marker icon

        // Adding data points to the chart
        for (int i = 0; i < locationList.size(); i++) {
            Entry entry = new Entry(i, (float) locationList.get(i).getLatitude());
            entry.setIcon(icon);  // Set the image icon for the point
            entries.add(entry);
        }

        // Create a LineDataSet
        LineDataSet dataSet = new LineDataSet(entries, "Latitude Over Time");
        dataSet.setColor(Color.BLUE);  // Line color
        dataSet.setDrawIcons(true);
        dataSet.setValueTextColor(Color.BLACK);  // Value text color
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);  // Hide values on the chart
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);  // Smooth curve

        // Create LineData
        LineData lineData = new LineData(dataSet);
        visitChart.setData(lineData);

        // Show X and Y axes
        XAxis xAxis = visitChart.getXAxis();
        xAxis.setEnabled(true);  // Enable X-axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        visitChart.getAxisLeft().setEnabled(true); // Enable left Y-axis
        visitChart.getAxisRight().setEnabled(false);

        // Customize grid lines
        visitChart.getXAxis().setDrawGridLines(false);
        visitChart.getAxisLeft().setDrawGridLines(false);
        visitChart.getAxisRight().setDrawGridLines(false);



        // Add listener for displaying data on hover/touch
        visitChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                if (index >= 0 && index < locationList.size()) {
                    LocationData data = locationList.get(index);
                    String message = "Location: " + data.getTime() + "\n" +
                            "Latitude: " + data.getLatitude() + "\n" +
                            "Longitude: " + data.getLongitude();
                    // Show the data in a Toast or in a TextView
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Optional: Handle when nothing is selected (e.g., clear the data display)
            }
        });

        // Hide the description label at the bottom
        visitChart.getDescription().setEnabled(false);

        // Set chart background to transparent
        visitChart.setBackgroundColor(Color.TRANSPARENT);
        visitChart.setDrawBorders(false);

        // Refresh the chart
        visitChart.invalidate();  // Refresh
    }

    @Override
    public void onResume() {
        super.onResume();

        // Scroll listener to trigger the chart animation only when the chart comes into view// The parent ScrollView

        scrollview.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int[] location = new int[2];
            visitChart.getLocationOnScreen(location);
            int chartTop = location[1];
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            // If the chart is within the screen, trigger animation
            if (chartTop < screenHeight && chartTop + visitChart.getHeight() > 0) {
                if (!isChartAnimated) {  // Add a flag to prevent re-triggering
                    visitChart.animateX(2000);  // Animate chart when in view
                    isChartAnimated = true;  // Set flag to true to avoid re-triggering
                }
            }
        });
    }

    static class LocationData {
        private final String time;
        private final double latitude;
        private final double longitude;

        public LocationData(String time, double latitude, double longitude) {
            this.time = time;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getTime() { return time; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }
}