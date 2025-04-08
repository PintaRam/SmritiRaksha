package com.smritiraksha.CareTaker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.smritiraksha.Adapters.MedAdapter;
import com.smritiraksha.Adapters.MonitorAdapter;
import com.smritiraksha.Models.MedItem;
import com.smritiraksha.Models.MonitorItem;
import com.smritiraksha.Patient.PatientLocation;
import com.smritiraksha.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link remainder_guide#newInstance} factory method to
 * create an instance of this fragment.
 */
public class remainder_guide extends Fragment {

    private List<MonitorItem> monitorItems;
    private List<MedItem> medItems;
    private LinearLayout guideRem, monitor_lyt,medrem_lyt,Treat_Planlyt,Act_monitlyt;
    private RecyclerView monitorRecyclerView, medRecyclerView;
    private Button medbtn,monitorbtn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public remainder_guide() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment remainder_guide.
     */
    // TODO: Rename and change types and number of parameters
    public static remainder_guide newInstance(String param1, String param2) {
        remainder_guide fragment = new remainder_guide();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_remainder_guide, container, false);

        // Initialize views
        guideRem = rootView.findViewById(R.id.guide_rem);
        Treat_Planlyt =rootView.findViewById(R.id.Treat_Plan);
        Act_monitlyt=rootView.findViewById(R.id.act_monitor);
        monitor_lyt=rootView.findViewById(R.id.monitor_lyt);
        medrem_lyt=rootView.findViewById(R.id.med_lyt);
        monitorRecyclerView = rootView.findViewById(R.id.monitor_recyclerview);
        medRecyclerView = rootView.findViewById(R.id.med_recyclerview);
        monitorbtn=rootView.findViewById(R.id.monitor_lyt_back);
        medbtn=rootView.findViewById(R.id.medication_lyt_back);

        monitorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideRem.setVisibility(View.VISIBLE);
                monitor_lyt.setVisibility(View.GONE);
                medrem_lyt.setVisibility(View.GONE);
            }
        });

        medbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideRem.setVisibility(View.VISIBLE);
                monitor_lyt.setVisibility(View.GONE);
                medrem_lyt.setVisibility(View.GONE);
            }
        });

        // Set click listeners
        Act_monitlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonitorRecyclerView();
            }
        });

        Treat_Planlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedRecyclerView();
            }
        });


        // Set Layout Managers
        monitorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        monitorRecyclerView.setHasFixedSize(true);
        medRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medRecyclerView.setHasFixedSize(true);

        // Sample Data
        monitorItems = new ArrayList<>();
        monitorItems.add(new MonitorItem(123, "Monitor@gmail.com"));
        monitorItems.add(new MonitorItem(345, "blood@gmail.com"));

        medItems = new ArrayList<>();
        medItems.add(new MedItem(123, "Take@gmail.com"));
        medItems.add(new MedItem(2367, "500mg@gmail.com"));

        // Set Adapters
        MonitorAdapter monitorAdapter = new MonitorAdapter(monitorItems, new MonitorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MonitorItem item) {
                // Navigate to MonitorActivity with selected item data
                Intent intent = new Intent(getContext(), PatientLocation.class);
                intent.putExtra("monitor_id", item.getId());
                intent.putExtra("monitor_email", item.getEmail());
                startActivity(intent);
            }
        });

        MedAdapter medAdapter = new MedAdapter(medItems, new MedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MedItem item) {
                // Navigate to MedActivity with selected item data
                Intent intent = new Intent(getContext(), MedicalSchedule.class);
                intent.putExtra("med_id", item.getId());
                intent.putExtra("med_dosage", item.getEmail());
                startActivity(intent);
            }
        });

        monitorRecyclerView.setAdapter(monitorAdapter);
        monitorRecyclerView.getAdapter().notifyDataSetChanged();

        medRecyclerView.setAdapter(medAdapter);
        medRecyclerView.getAdapter().notifyDataSetChanged();



        return rootView;
    }
    // Method to show the monitor RecyclerView and hide others
    private void showMonitorRecyclerView() {
        guideRem.setVisibility(View.GONE);
        medrem_lyt.setVisibility(View.GONE);
        monitor_lyt.setVisibility(View.VISIBLE);
        if (monitorRecyclerView.getAdapter() == null) {
            MonitorAdapter monitorAdapter = new MonitorAdapter(monitorItems, new MonitorAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(MonitorItem item) {
                    Intent intent = new Intent(getContext(), PatientLocation.class);
                    intent.putExtra("monitor_id", item.getId());
                    intent.putExtra("monitor_email", item.getEmail());
                    startActivity(intent);
                }
            });
            monitorRecyclerView.setAdapter(monitorAdapter);
        }

        monitorRecyclerView.getAdapter().notifyDataSetChanged(); // Refresh
    }

    // Method to show the medication RecyclerView and hide others
    private void showMedRecyclerView() {
        guideRem.setVisibility(View.GONE);
        monitor_lyt.setVisibility(View.GONE);
        medrem_lyt.setVisibility(View.VISIBLE);
        if (medRecyclerView.getAdapter() == null) {
            MedAdapter medAdapter = new MedAdapter(medItems, new MedAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(MedItem item) {
                    Intent intent = new Intent(getContext(), MedicalSchedule.class);
                    intent.putExtra("med_id", item.getId());
                    intent.putExtra("med_dosage", item.getEmail());
                    startActivity(intent);
                }
            });
            medRecyclerView.setAdapter(medAdapter);
        }

        medRecyclerView.getAdapter().notifyDataSetChanged(); // Refresh
    }
}