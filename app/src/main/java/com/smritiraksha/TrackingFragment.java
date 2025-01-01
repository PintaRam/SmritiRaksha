package com.smritiraksha;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText searchBar,destinationInput,sourceInput;
    private LatLng srcLoc,destLoc;
    private ArrayList<Polyline>polylines=null;
    double userlat,userlong;
    FusedLocationProviderClient fusedLocationProviderClient;
    public TrackingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackingFragment.
     */
    public static TrackingFragment newInstance(String param1, String param2) {
        TrackingFragment fragment = new TrackingFragment();
        Bundle args = new Bundle();
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        // Initialize search bar
        searchBar = view.findViewById(R.id.search_bar);
        sourceInput = view.findViewById(R.id.source_input);
        destinationInput = view.findViewById(R.id.destination_input);

        // Initialize MapView
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());
        // Floating Action Button
        FloatingActionButton fab = view.findViewById(R.id.fab_create_journey);
        fab.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.VISIBLE) {
                // Hide search bar with animation
                searchBar.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            searchBar.setVisibility(View.GONE);
                            // Show source & destination inputs with animation
                            sourceInput.setVisibility(View.VISIBLE);
                            destinationInput.setVisibility(View.VISIBLE);
                            sourceInput.setAlpha(0f); // Start with fully transparent
                            destinationInput.setAlpha(0f); // Start with fully transparent
                            sourceInput.animate()
                                    .alpha(1f)
                                    .setDuration(300);
                            destinationInput.animate()
                                    .alpha(1f)
                                    .setDuration(300);
                        });

                // Change the FAB icon to a new one
                fab.setImageResource(R.drawable.ic_map_home);

            } else {
                // Revert back to original state (show search bar with animation)
                sourceInput.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            sourceInput.setVisibility(View.GONE);
                            destinationInput.setVisibility(View.GONE);
                            searchBar.setVisibility(View.VISIBLE);
                            searchBar.setAlpha(0f); // Start with fully transparent
                            searchBar.animate()
                                    .alpha(1f)
                                    .setDuration(300);
                        });

                // Change the FAB icon back to original
                fab.setImageResource(R.drawable.ic_direction);
            }
        });

        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sourceLocation = sourceInput.getText().toString().trim();
                String destinationLocation = destinationInput.getText().toString().trim();

                if (!sourceLocation.isEmpty() && !destinationLocation.isEmpty()) {
                    // Show path when both inputs are filled
                    srcLoc=getLatLngFromAddress(sourceLocation);
                    destLoc=getLatLngFromAddress(destinationLocation);
                    getRoutePoints(srcLoc,destLoc);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        searchBar.addTextChangedListener(new TextWatcher() {
            private android.os.Handler handler = new android.os.Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No operation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = searchBar.getText().toString().trim();

                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }

                runnable = () -> {
                    if (!searchText.isEmpty()) {
                        LatLng searchLocation = getLatLngFromAddress(searchText);
                        if (searchLocation != null) {
                            updateMapWithMarker(searchLocation, "Search: " + searchText);
                        } else {
                            Toast.makeText(getContext(), "Location not found: " + searchText, Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                // Execute the search with a 500ms delay
                handler.postDelayed(runnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No operation
            }
        });


        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        fetchlocation();
    }

    private void fetchlocation()
    {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task= fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                userlat=location.getLatitude();
                userlong=location.getLongitude();

                LatLng latLng =new LatLng(userlat,userlong);
                CameraPosition camera=new CameraPosition.Builder().target(latLng).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            }
        });

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

    @Override
    public void onRouteFailure(ErrorHandling e) {

    }

    @Override
    public void onRouteStart() {

    }

    public void getRoutePoints(LatLng start, LatLng end) {
        if (start == null || end == null) {
            Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_LONG).show();
            Log.e("TAG", " latlngs are null");
        } else {
            RouteDrawing routeDrawing = new RouteDrawing.Builder()
                    .context(getContext())  // pass your activity or fragment's context
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this).alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routeDrawing.execute();
        }

    }

    // Method to convert address to LatLng
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
    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> routeInfoModelArrayList, int routeIndexing) {
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<Polyline> polylines = new ArrayList<>();
        for (int i = 0; i < routeInfoModelArrayList.size(); i++) {
            if (i == routeIndexing) {
                Log.e("TAG", "onRoutingSuccess: routeIndexing" + routeIndexing);
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(12);
                polylineOptions.addAll(routeInfoModelArrayList.get(routeIndexing).getPoints());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.endCap(new RoundCap());
                Polyline polyline = googleMap.addPolyline(polylineOptions);
                polylines.add(polyline);
            }
        }
    }
    private void updateMapWithMarker(LatLng location, String title) {
        if (googleMap != null) {
            googleMap.clear(); // Clear existing markers
            googleMap.addMarker(new MarkerOptions().position(location).title(title));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
        }
    }
    @Override
    public void onRouteCancelled() {

    }
}
