package com.smritiraksha.Doctor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.smritiraksha.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BehaviorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BehaviorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart moodChart;
    private BarChart activityChart;
    private TextView alertMissedDoses, alertSOSTriggered;

    public BehaviorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BehaviorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BehaviorFragment newInstance(String param1, String param2) {
        BehaviorFragment fragment = new BehaviorFragment();
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
        View view = inflater.inflate(R.layout.fragment_behavior, container, false);

        moodChart = view.findViewById(R.id.moodChart);
        activityChart = view.findViewById(R.id.activityChart);
        alertMissedDoses = view.findViewById(R.id.alertMissedDoses);
        alertSOSTriggered = view.findViewById(R.id.alertSOSTriggered);

        setupMoodChart();
        setupActivityChart();
        displayAlerts();

        return view;
        }


    private void setupMoodChart() {
        ArrayList<Entry> moodEntries = new ArrayList<>();
        moodEntries.add(new Entry(1, 3)); // Mon
        moodEntries.add(new Entry(2, 4)); // Tue
        moodEntries.add(new Entry(3, 2)); // Wed
        moodEntries.add(new Entry(4, 5)); // Thu
        moodEntries.add(new Entry(5, 4)); // Fri
        moodEntries.add(new Entry(6, 3)); // Sat
        moodEntries.add(new Entry(7, 4)); // Sun

        LineDataSet dataSet = new LineDataSet(moodEntries, "Mood Level");
        dataSet.setColor(Color.MAGENTA);
        dataSet.setCircleColor(Color.DKGRAY);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        moodChart.setData(lineData);

        Description desc = new Description();
        desc.setText("Mood Trend (1-5)");
        moodChart.setDescription(desc);
        moodChart.invalidate();
    }

    private void setupActivityChart() {
        ArrayList<BarEntry> activityEntries = new ArrayList<>();
        activityEntries.add(new BarEntry(1, 2)); // Mon
        activityEntries.add(new BarEntry(2, 3)); // Tue
        activityEntries.add(new BarEntry(3, 1)); // Wed
        activityEntries.add(new BarEntry(4, 4)); // Thu
        activityEntries.add(new BarEntry(5, 3)); // Fri
        activityEntries.add(new BarEntry(6, 5)); // Sat
        activityEntries.add(new BarEntry(7, 4)); // Sun

        BarDataSet dataSet = new BarDataSet(activityEntries, "Activity Level");
        dataSet.setColors(Color.rgb(104, 241, 175));
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        activityChart.setData(barData);

        Description desc = new Description();
        desc.setText("Daily Activity");
        activityChart.setDescription(desc);
        activityChart.invalidate();
    }

    private void displayAlerts() {
        // You can dynamically fetch these from DB or server
        alertMissedDoses.setText("❗ Missed 3+ doses this week");
        alertSOSTriggered.setText("🚨 SOS Triggered on Apr 9");
    }
}