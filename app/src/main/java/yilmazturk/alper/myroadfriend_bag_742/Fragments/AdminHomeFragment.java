package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import yilmazturk.alper.myroadfriend_bag_742.Adapters.AdminHomeTripAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Adapters.AdminHomeUserAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerViewUser;
    private RecyclerView recyclerViewTrip;
    private AdminHomeUserAdapter adminHomeUserAdapter;
    private AdminHomeTripAdapter adminHomeTripAdapter;
    private ArrayList<String> userIDList;
    private ArrayList<User> userList;
    private ArrayList<Trip> tripList;
    private LinearLayoutManager linearLayoutManager;
    private static DatabaseReference database;
    private static Context context;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userIDList = new ArrayList<>();
        userList = new ArrayList<>();
        tripList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        database = FirebaseDatabase.getInstance().getReference();
        context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View adminHomeFragment = inflater.inflate(R.layout.fragment_admin_home, container, false);

        recyclerViewUser = adminHomeFragment.findViewById(R.id.recyclerViewAdminHomeUser);
        recyclerViewTrip = adminHomeFragment.findViewById(R.id.recyclerViewAdminHomeTrip);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewUser.setLayoutManager(linearLayoutManager);
        recyclerViewUser.setNestedScrollingEnabled(false);
        recyclerViewTrip.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTrip.setNestedScrollingEnabled(false);

        showUsers();
        showTrips();


        return adminHomeFragment;
    }

    private void showUsers() {
        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uds : snapshot.getChildren()) {

                    User user = uds.getValue(User.class);
                    userIDList.add(uds.getKey());
                    userList.add(user);

                }
                adminHomeUserAdapter = new AdminHomeUserAdapter(userIDList, userList);
                recyclerViewUser.setAdapter(adminHomeUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void disableUser(String disableUserID) {

        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uds : snapshot.getChildren()) {
                    if (uds.getKey().equals(disableUserID)) {
                        User user = uds.getValue(User.class);
                        database.child("Disabled Users").child(uds.getKey()).setValue(user);
                        uds.getRef().removeValue();
                        Toast.makeText(context, "User " + user.getName() + " is disabled", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showTrips() {
        database.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tds : snapshot.getChildren()) {
                    for (DataSnapshot uTds : tds.getChildren()) {
                        Trip trip = uTds.getValue(Trip.class);
                        tripList.add(trip);
                    }
                    adminHomeTripAdapter = new AdminHomeTripAdapter(tripList);
                    recyclerViewTrip.setAdapter(adminHomeTripAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}