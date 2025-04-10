package com.smritiraksha.Doctor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardAdapter extends FragmentStateAdapter {

        public DashboardAdapter(@NonNull FragmentActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new OverviewFragment();
                case 1: return new MedicationFragment();
                case 2: return new BehaviorFragment();
                default: return new OverviewFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
}
