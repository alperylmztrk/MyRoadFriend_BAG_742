package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yilmazturk.alper.myroadfriend_bag_742.Fragments.DriverHomeFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.MessageFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.NotificationFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.PassengerHomeFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.ProfileFragment;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    FloatingActionButton fab;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Boolean isDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fab = findViewById(R.id.fab);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        determineUserType();

        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.home:
                    startHomeFragment();
                    break;
                case R.id.messages:
                    selectedFragment = new MessageFragment();
                    break;
                case R.id.notifications:
                    selectedFragment = new NotificationFragment();
                    break;
                case R.id.profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
            }

            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDriver) {
                    startActivity(new Intent(MainActivity.this, PostTripActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, SearchTripActivity.class));
                }
            }
        });


    }

    private void determineUserType() {

        String userID = firebaseUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String userType = user.getUserType();
                if (userType.equals("Passenger")) {
                    isDriver = false;
                    fab.setImageResource(R.drawable.ic_round_search);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PassengerHomeFragment()).commit();
                } else {
                    isDriver = true;
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DriverHomeFragment()).commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void startHomeFragment() {

        String userID = firebaseUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getUserType().equals("Passenger")) {
                    selectedFragment = new PassengerHomeFragment();
                } else {
                    selectedFragment = new DriverHomeFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}