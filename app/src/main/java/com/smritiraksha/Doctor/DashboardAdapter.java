package com.smritiraksha.Doctor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardAdapter extends FragmentStateAdapter {

    private final String docEmail, patientId, patientName,patientEmail;
        public DashboardAdapter(@NonNull FragmentActivity activity,String docEmail, String patientId, String patientName,String patientEmail) {
            super(activity);
            this.docEmail = docEmail;
            this.patientId = patientId;
            this.patientName = patientName;
            this.patientEmail=patientEmail;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Bundle args = new Bundle();
            args.putString("doc_email", docEmail);
            args.putString("patient_id", patientId);
            args.putString("patient_name", patientName);
            args.putString("patientEmail",patientEmail);
            switch (position) {
                case 0:
                    OverviewFragment overviewFragment = new OverviewFragment();
                    overviewFragment.setArguments(args);
                    return overviewFragment;
                case 1:
                    MedicationFragment medicationFragment = new MedicationFragment();
                    medicationFragment.setArguments(args);
                    return medicationFragment;
                case 2:
                    BehaviorFragment behaviorFragment = new BehaviorFragment();
                    behaviorFragment.setArguments(args);
                    return behaviorFragment;
                default:
                    return new OverviewFragment();
            }

        }

        @Override
        public int getItemCount() {
            return 3;
        }
}
