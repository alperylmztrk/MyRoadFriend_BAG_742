package yilmazturk.alper.myroadfriend_bag_742;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import yilmazturk.alper.myroadfriend_bag_742.Fragments.AdminDisableUserFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.AdminHomeFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.AdminRemoveTripFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.ProfileFragment;

public class AdminActivity extends AppCompatActivity {
    BottomNavigationView adminBottomNavView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminBottomNavView = findViewById(R.id.adminBottomNavigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.adminFrameLayout, new AdminHomeFragment()).commit();

        adminBottomNavView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.adminHome) {
                selectedFragment = new AdminHomeFragment();
            } else if (item.getItemId() == R.id.disUsers) {
                selectedFragment = new AdminDisableUserFragment();
            } else if (item.getItemId() == R.id.remTrips) {
                selectedFragment = new AdminRemoveTripFragment();
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.adminFrameLayout, selectedFragment).commit();
            }
            return true;
        });

    }
}