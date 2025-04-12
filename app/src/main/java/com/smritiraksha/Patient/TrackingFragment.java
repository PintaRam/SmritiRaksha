package com.smritiraksha.Patient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smritiraksha.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
// ... (package & imports remain same)

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
    private Marker userMarker;  // ✅ Marker that will move as user moves

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

        googleMap.setMyLocationEnabled(true);  // ✅ Show default blue dot with direction
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

                addCircleAroundLocation(userloc);
                startLocationUpdates();
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

                        Location location = locationResult.getLocations().get(0); // ✅ Corrected line
                        LatLng updatedLoc = new LatLng(location.getLatitude(), location.getLongitude());

                        if (userMarker != null) {
                            userMarker.setPosition(updatedLoc);  // ✅ Move marker
                        } else {
                            userMarker = googleMap.addMarker(new MarkerOptions()
                                    .position(updatedLoc)
                                    .title("You")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        }

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updatedLoc, 20));
                    }
                },
                Looper.getMainLooper());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (isLocationEnabled()) {
                fetchLastLocation();
            } else {
                Toast.makeText(getContext(), "Location services are not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override public void onRouteFailure(ErrorHandling errorHandling) {}
    @Override public void onRouteStart() {}
    @Override public void onRouteSuccess(ArrayList<RouteInfoModel> list, int indexing) {}
    @Override public void onRouteCancelled() {}

    private void toggleSearchInputs(FloatingActionButton fab) {
        if (searchBar.getVisibility() == View.VISIBLE) {
            searchBar.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                searchBar.setVisibility(View.GONE);
                sourceInput.setVisibility(View.VISIBLE);
                destinationInput.setVisibility(View.VISIBLE);
                sourceInput.animate().alpha(1f).setDuration(300);
                destinationInput.animate().alpha(1f).setDuration(300);
            });
            fab.setImageResource(R.drawable.ic_map_home);
        } else {
            sourceInput.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                sourceInput.setVisibility(View.GONE);
                destinationInput.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                searchBar.animate().alpha(1f).setDuration(300);
            });
            fab.setImageResource(R.drawable.ic_direction);
        }
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            private final Handler handler = new Handler();
            private Runnable delayedAction;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (delayedAction != null) handler.removeCallbacks(delayedAction);

                delayedAction = () -> {
                    String src = sourceInput.getText().toString().trim();
                    String dest = destinationInput.getText().toString().trim();

                    if (!src.isEmpty() && !dest.isEmpty()) {
                        srcLoc = getLatLngFromAddress(src);
                        destLoc = getLatLngFromAddress(dest);

                        if (srcLoc != null && destLoc != null) {
                            getRoutePoints(srcLoc, destLoc);
                        } else {
                            Toast.makeText(getContext(), "Invalid address.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                handler.postDelayed(delayedAction, 500);
            }
            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private TextWatcher createSearchBarWatcher() {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    LatLng latLng = getLatLngFromAddress(query);
                    if (latLng != null) {
                        updateMapWithMarker(latLng, "Search Result");
                    }
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (!addresses.isEmpty()) {
                Address result = addresses.get(0);
                return new LatLng(result.getLatitude(), result.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getRoutePoints(LatLng srcLoc, LatLng destLoc) {
        // Implement route drawing with Directions API
    }

    private void addCircleAroundLocation(LatLng latLng) {
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(500)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(50, 0, 0, 255))
                .strokeWidth(2));
    }

    private void updateMapWithMarker(LatLng latLng, String title) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
    }
}

