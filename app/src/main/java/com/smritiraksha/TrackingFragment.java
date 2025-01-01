package com.smritiraksha;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A Fragment that displays a map with the ability to create journeys.
 */
public class TrackingFragment extends Fragment implements OnMapReadyCallback, RouteListener {

    private static final int LOCATION_REQUEST_CODE = 101;

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText searchBar, destinationInput, sourceInput;
    private LatLng srcLoc, destLoc;
    private  LatLng testSource,testDestination;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;

    public TrackingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        // Initialize UI components
        searchBar = view.findViewById(R.id.search_bar);
        sourceInput = view.findViewById(R.id.source_input);
        destinationInput = view.findViewById(R.id.destination_input);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        FloatingActionButton fab = view.findViewById(R.id.fab_create_journey);
        fab.setOnClickListener(v -> toggleSearchInputs(fab));

        // Attach TextWatcher to inputs
        TextWatcher textWatcher = createTextWatcher();
        sourceInput.addTextChangedListener(textWatcher);
        destinationInput.addTextChangedListener(textWatcher);

        searchBar.addTextChangedListener(createSearchBarWatcher());
        testSource = new LatLng(37.7749, -122.4194); // San Francisco
        testDestination = new LatLng(34.0522, -118.2437); // Los Angeles
        getRoutePoints(testSource, testDestination);
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
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition camera = new CameraPosition.Builder().target(latLng).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            }
        });
    }

    private void toggleSearchInputs(FloatingActionButton fab) {
        if (searchBar.getVisibility() == View.VISIBLE) {
            // Hide search bar, show source & destination inputs
            searchBar.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                searchBar.setVisibility(View.GONE);
                sourceInput.setVisibility(View.VISIBLE);
                destinationInput.setVisibility(View.VISIBLE);
                sourceInput.setAlpha(0f);
                destinationInput.setAlpha(0f);
                sourceInput.animate().alpha(1f).setDuration(300);
                destinationInput.animate().alpha(1f).setDuration(300);
            });
            fab.setImageResource(R.drawable.ic_map_home);
        } else {
            // Hide inputs, show search bar
            sourceInput.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                sourceInput.setVisibility(View.GONE);
                destinationInput.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                searchBar.setAlpha(0f);
                searchBar.animate().alpha(1f).setDuration(300);
            });
            fab.setImageResource(R.drawable.ic_direction);
        }
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable delayedAction;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sourceLocation = sourceInput.getText().toString().trim();
                String destinationLocation = destinationInput.getText().toString().trim();

                if (delayedAction != null) {
                    handler.removeCallbacks(delayedAction);
                }

                delayedAction = () -> {
                    if (!sourceLocation.isEmpty() && !destinationLocation.isEmpty()) {
                        srcLoc = getLatLngFromAddress(sourceLocation);
                        destLoc = getLatLngFromAddress(destinationLocation);

                        if (srcLoc != null && destLoc != null) {
                            getRoutePoints(srcLoc, destLoc);
                        } else {
                            Toast.makeText(getContext(), "Invalid address entered", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                handler.postDelayed(delayedAction, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private TextWatcher createSearchBarWatcher() {
        return new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }

                runnable = () -> {
                    String searchText = searchBar.getText().toString().trim();
                    if (!searchText.isEmpty()) {
                        LatLng searchLocation = getLatLngFromAddress(searchText);
                        if (searchLocation != null) {
                            updateMapWithMarker(searchLocation, "Search: " + searchText);
                        } else {
                            Toast.makeText(getContext(), "Location not found: " + searchText, Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                handler.postDelayed(runnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getRoutePoints(LatLng start, LatLng end) {
        if (start == null || end == null) {
            Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_LONG).show();
            return;
        }
        RouteDrawing routeDrawing = new RouteDrawing.Builder()
                .context(getContext())
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(start, end)
                .build();
        routeDrawing.execute();
    }

    @Override
    public void onRouteFailure(ErrorHandling e) {
        Toast.makeText(getContext(), "Route calculation failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteStart() {
        Toast.makeText(getContext(), "Calculating route...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> routeInfoModelArrayList, int routeIndexing) {
        if (polylines != null) {
            for (Polyline polyline : polylines) {
                polyline.remove();
            }
            polylines.clear();
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(Color.BLACK)
                .width(12)
                .startCap(new RoundCap())
                .endCap(new RoundCap());
        List<LatLng> routePoints = routeInfoModelArrayList.get(routeIndexing).getPoints();
        polylineOptions.addAll(routeInfoModelArrayList.get(routeIndexing).getPoints());
        polylines.add(googleMap.addPolyline(polylineOptions));

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng point : routePoints) {
            boundsBuilder.include(point);
        }

        // Adjust the camera to include the full route
        LatLngBounds bounds = boundsBuilder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void updateMapWithMarker(LatLng location, String title) {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location).title(title));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
        }
    }

    @Override
    public void onRouteCancelled() {
        Toast.makeText(getContext(), "Route calculation cancelled.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
