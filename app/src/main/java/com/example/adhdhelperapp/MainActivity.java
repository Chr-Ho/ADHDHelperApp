package com.example.adhdhelperapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new TaskListFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_task_list) {
                selectedFragment = new TaskListFragment();
            } else if (itemId == R.id.navigation_focus_timer) {
                selectedFragment = new FocusTimerFragment();
            } else if (itemId == R.id.navigation_resources) {
                selectedFragment = new ResourcesFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        // Replace the current fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
