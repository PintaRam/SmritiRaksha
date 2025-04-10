package com.smritiraksha.Doctor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.view.View;
import android.widget.ImageView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.smritiraksha.R;

public class DoctorDashboard extends AppCompatActivity {
        private ViewPager2 viewPager;
        private TabLayout tabLayout;
        private DashboardAdapter adapter;
        private DrawerLayout drawerLayout;
        private NavigationView navigationView;
        private ImageView profileIcon;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_doctor_dashboard);

            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);
            drawerLayout = findViewById(R.id.drawerLayout);
            navigationView = findViewById(R.id.navigationView);
            profileIcon = findViewById(R.id.profileIcon);

            drawerLayout.openDrawer(GravityCompat.END);

            adapter = new DashboardAdapter(this);
            viewPager.setAdapter(adapter);
            profileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(navigationView);
                }
            });


            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setIcon(R.drawable.ic_overview);
                                tab.setText("Overview");
                                break;
                            case 1:
                                tab.setIcon(R.drawable.ic_medication);
                                tab.setText("Medication");
                                break;
                            case 2:
                                tab.setIcon(R.drawable.ic_alert);
                                tab.setText("Alerts");
                                break;
                        }
                    }).attach();


            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_logout) {
                    // handle logout
                } else if (id == R.id.nav_settings) {
                    // handle settings
                }

                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            });

        }
}
