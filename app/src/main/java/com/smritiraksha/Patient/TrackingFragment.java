package com.smritiraksha.Patient;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codebyashish.googledirectionapi.AbstractRouting;
import com.codebyashish.googledirectionapi.ErrorHandling;
import com.codebyashish.googledirectionapi.RouteDrawing;
import com.codebyashish.googledirectionapi.RouteInfoModel;
import com.codebyashish.googledirectionapi.RouteListener;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class TrackingFragment extends Fragment implements OnMapReadyCallback, RouteListener {

    private static final int LOCATION_REQUEST_CODE = 101;
    private static final long LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds
    private static final long LOCATION_FASTEST_INTERVAL = 5000; // 5 seconds

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText searchBar, destinationInput, sourceInput;
    private LatLng srcLoc, destLoc, userloc;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Marker userMarker;

    public TrackingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        searchBar = view.findViewById(R.id.search_bar);
        sourceInput = view.findViewById(R.id.source_input);
        destinationInput = view.findViewById(R.id.destination_input);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        FloatingActionButton fab = view.findViewById(R.id.fab_create_journey);
        fab.setOnClickListener(v -> toggleSearchInputs(fab));

        TextWatcher textWatcher = createTextWatcher();
        sourceInput.addTextChangedListener(textWatcher);
        destinationInput.addTextChangedListener(textWatcher);
        searchBar.addTextChangedListener(createSearchBarWatcher());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        fetchLocation();

        googleMap.setOnMapClickListener(latLng -> {
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        });

        locationRequest = LocationRequest.create()
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(LOCATION_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void fetchLocation() {
        if (!isLocationEnabled()) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(requireActivity());
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

            task.addOnSuccessListener(response -> fetchLastLocation());
            task.addOnFailureListener(exception -> {
                if (exception instanceof ResolvableApiException) {
                    try {
                        ((ResolvableApiException) exception).startResolutionForResult(getActivity(), LOCATION_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Unable to enable location services.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fetchLastLocation();
        }
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userloc = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userloc, 15));

                if (userMarker == null) {
                    userMarker = googleMap.addMarker(new MarkerOptions()
                            .position(userloc)
                            .title("You")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }

                startLocationUpdates();
                updateLocationInDatabase(userloc.latitude, userloc.longitude);
            } else {
                Toast.makeText(getContext(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null || locationResult.getLocations().isEmpty()) return;

                        Location location = locationResult.getLocations().get(0);
                        LatLng updatedLoc = new LatLng(location.getLatitude(), location.getLongitude());

                        if (userMarker != null) {
                            userMarker.setPosition(updatedLoc);
                        } else {
                            userMarker = googleMap.addMarker(new MarkerOptions()
                                    .position(updatedLoc)
                                    .title("You")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        }

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updatedLoc, 20));

                        updateLocationInDatabase(updatedLoc.latitude, updatedLoc.longitude);
                    }
                },
                Looper.getMainLooper());
    }

    private void updateLocationInDatabase(double latitude, double longitude) {
        if (!isAdded()) {
            Log.w("TrackingFragment", "Fragment not attached. Skipping location update.");
            return;
        }

        Context appContext = getContext().getApplicationContext();
        String url = Constants.Patient_location;
        String patient_id = "PT01Sri";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean error = jsonResponse.getBoolean("error");
                        String message = jsonResponse.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to update location", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_id", patient_id);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                return params;
            }
        };

        Volley.newRequestQueue(appContext).add(stringRequest);
    }

    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void toggleSearchInputs(FloatingActionButton fab) {
        if (sourceInput.getVisibility() == View.VISIBLE) {
            sourceInput.setVisibility(View.GONE);
            destinationInput.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.ic_add_location);
        } else {
            sourceInput.setVisibility(View.VISIBLE);
            destinationInput.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_remove_location);
        }
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                String sourceText = sourceInput.getText().toString();
                String destText = destinationInput.getText().toString();
                if (!sourceText.isEmpty() && !destText.isEmpty()) {
                    getRouteFromGoogleAPI(sourceText, destText);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    private TextWatcher createSearchBarWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if (searchBar.getText().length() > 0) {
                    searchLocation(searchBar.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    private void getRouteFromGoogleAPI(String source, String destination) {
        try {
            String originEncoded = URLEncoder.encode(source, "UTF-8");
            String destinationEncoded = URLEncoder.encode(destination, "UTF-8");
            String apiKey = getString(R.string.GMap_APIKEY);

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                    + originEncoded + "&destination=" + destinationEncoded + "&key=" + apiKey;

            Log.d("DirectionsAPI", "URL: " + url);

            fetchAndDrawRoute(url); // 👇 We'll define this next
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDrawRoute(String url) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String points = overviewPolyline.getString("points");

                            List<LatLng> decodedPath = decodePoly(points);
                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .addAll(decodedPath)
                                    .color(Color.BLUE)
                                    .width(12);

                            googleMap.addPolyline(polylineOptions);
                        } else {
                            Toast.makeText(getContext(), "No route found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch route", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
            poly.add(p);
        }

        return poly;
    }

    public void onRouteFound(List<RouteInfoModel> routeList) {
        if (routeList != null && !routeList.isEmpty()) {
            RouteInfoModel route = routeList.get(0);
            // Remove previous polyline if any
            for (Polyline polyline : polylines) {
                polyline.remove();
            }

            // Create a new polyline for the current route
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(route.getPoints())  // Add the points of the route
                    .width(10)  // Set polyline width
                    .color(Color.RED);  // Set polyline color

            // Add the polyline to the map
            Polyline polyline = googleMap.addPolyline(polylineOptions);
            polylines.add(polyline);
        }
    }

    public void onRouteError(String error) {
        Toast.makeText(getContext(), "Route drawing error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteFailure(ErrorHandling e) {
        // Handle any route failure
    }

    @Override
    public void onRouteStart() {
        // Handle any operations when route drawing starts
    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> list, int indexing) {
        // Handle operations when route drawing is successful
    }

    @Override
    public void onRouteCancelled() {
        // Handle operations when route drawing is cancelled
    }

}
