package com.smritiraksha;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private List<LocationModel> locations;

    public LocationAdapter(List<LocationModel> locations) {
        this.locations = locations;
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView locationName, visitTime;

        public LocationViewHolder(View view) {
            super(view);
            locationName = view.findViewById(R.id.location_name);
            visitTime = view.findViewById(R.id.visit_time);
        }
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_locations, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationModel location = locations.get(position);
        holder.locationName.setText(location.getLocation_name());
        holder.visitTime.setText("Visited on: " + location.getVisit_time());
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
