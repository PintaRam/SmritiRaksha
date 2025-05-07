package com.smritiraksha.Doctor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private List<Prescription> prescriptionList = new ArrayList<>();
    private PrescriptionAdapter adapter;
    private String patientEmail ;
    private static final int EDIT_PRESCRIPTION_REQUEST = 100;

    public MedicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MedicationFragment newInstance(String param1, String param2) {
        MedicationFragment fragment = new MedicationFragment();
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
            if (getArguments().containsKey("patientEmail")) {
                patientEmail = getArguments().getString("patientEmail");
                Log.d("MedicationFragment", "Received patientEmail: " + patientEmail);
            } else {
                Log.e("MedicationFragment", "patientEmail not found in arguments");
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medication, container, false);

        recyclerView = view.findViewById(R.id.prescriptionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("Medical Fragment",patientEmail);

        adapter = new PrescriptionAdapter(getContext(), prescriptionList, prescription -> {
            Intent intent = new Intent(getActivity(), EditPrescriptionActivity.class);
            intent.putExtra("prescription", prescription);
            startActivityForResult(intent, EDIT_PRESCRIPTION_REQUEST);
        });

        recyclerView.setAdapter(adapter);

        loadPrescriptions();
        //loadSamplePrescriptions();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PRESCRIPTION_REQUEST && resultCode == Activity.RESULT_OK) {
            loadPrescriptions(); // Refresh the list after edit
        }
    }

    private void loadPrescriptions() {
        String url = Constants.GET_Patinet_Prescription + "?email=" + patientEmail;
        Log.d("Medication patient", patientEmail);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            JSONArray array = jsonObject.getJSONArray("prescriptions");
                            prescriptionList.clear();

                            if (array.length() == 0) {
                                Toast.makeText(getContext(), "No prescription record found", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    Prescription p = new Prescription(
                                            obj.getString("id"),
                                            obj.getString("patient_email"),
                                            obj.getString("title"),
                                            obj.getString("description"),
                                            obj.getString("hour"),
                                            obj.getString("minute"),
                                            obj.getString("created_at")
                                    );
                                    prescriptionList.add(p);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Error fetching prescriptions", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to load prescriptions", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }


    private void loadSamplePrescriptions() {
        prescriptionList.clear();
        prescriptionList.add(new Prescription("1", "patient001", "Donepezil", "Used to treat confusion", "08", "30", "2025-04-14"));
        prescriptionList.add(new Prescription("2", "patient001", "Memantine", "Helps with memory", "10", "00", "2025-04-12"));
        prescriptionList.add(new Prescription("3", "patient001", "Rivastigmine", "Improves awareness", "14", "15", "2025-04-10"));

        prescriptionList.add(new Prescription("1", "patient001", "Donepezil", "Used to treat confusion", "08", "30", "2025-04-14"));
        prescriptionList.add(new Prescription("2", "patient001", "Memantine", "Helps with memory", "10", "00", "2025-04-12"));
        prescriptionList.add(new Prescription("3", "patient001", "Rivastigmine", "Improves awareness", "14", "15", "2025-04-10"));
        adapter.notifyDataSetChanged();
    }
}