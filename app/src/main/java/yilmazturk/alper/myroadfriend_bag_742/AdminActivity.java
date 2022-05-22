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
            switch (item.getItemId()) {
                case R.id.adminHome:
                    selectedFragment = new AdminHomeFragment();
                    break;
                case R.id.disUsers:
                    selectedFragment = new AdminDisableUserFragment();
                    break;
                case R.id.remTrips:
                    selectedFragment = new AdminRemoveTripFragment();
                    break;
                case R.id.profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.adminFrameLayout, selectedFragment).commit();
            }
            return true;
        });

    }
}