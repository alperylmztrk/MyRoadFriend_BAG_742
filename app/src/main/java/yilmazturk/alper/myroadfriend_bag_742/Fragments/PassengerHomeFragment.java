package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import yilmazturk.alper.myroadfriend_bag_742.Adapters.TripAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.Time;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;
import yilmazturk.alper.myroadfriend_bag_742.R;


public class PassengerHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private ArrayList<Driver> driverList;
    private ArrayList<String> strDriverImageList;
    private ArrayList<String> strDayAndTime;
    private ArrayList<UniDetail> uniDetailList;
    private UniList uniList;
    private ArrayList<Route> routeList;
    private LinearLayoutManager linearLayoutManager;
    DatabaseReference database;
    Context context;


    public PassengerHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        driverList = new ArrayList<>();
        strDriverImageList = new ArrayList<>();
        strDayAndTime = new ArrayList<>();
        routeList = new ArrayList<>();
        uniDetailList = new ArrayList<>();
        context = getContext();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        database = FirebaseDatabase.getInstance().getReference();

        try {
            //Load File
            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.universities)));
            StringBuilder jsonBuilder = new StringBuilder();
            for (String line = null; (line = jsonReader.readLine()) != null; ) {
                jsonBuilder.append(line).append("\n");
            }

            Gson gson = new Gson();
            uniList = gson.fromJson(jsonBuilder.toString(), UniList.class);

        } catch (FileNotFoundException e) {
            Log.e("jsonFile", "file not found");
        } catch (IOException e) {
            Log.e("jsonFile", "IOerror");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View passengerHomeFragment = inflater.inflate(R.layout.fragment_passenger_home, container, false);

        recyclerView = passengerHomeFragment.findViewById(R.id.recyclerViewPassengerHome);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot tripSnapshot = snapshot.child("Trips");
                if (!tripSnapshot.exists()) {
                    Toast.makeText(getActivity(), "No Trips", Toast.LENGTH_SHORT).show();
                } else {

                    for (DataSnapshot routeDS : snapshot.child("Routes").getChildren()) {
                        String routeID = routeDS.child("routeID").getValue().toString();
                        String rTripID = routeDS.child("tripID").getValue().toString();

                        ArrayList<LatLng> waypointList = new ArrayList<>();
                        for (DataSnapshot rWayDS : routeDS.child("waypoints").getChildren()) {

                            String lat = rWayDS.child("latitude").getValue().toString();
                            String lng = rWayDS.child("longitude").getValue().toString();
                            double latitude = Double.parseDouble(lat);
                            double longitude = Double.parseDouble(lng);
                            LatLng waypoint = new LatLng(latitude, longitude);
                            waypointList.add(waypoint);
                        }
                        Route route = new Route(routeID, rTripID, waypointList);

                        routeList.add(route);

                        for (DataSnapshot tds : tripSnapshot.getChildren()) {
                            DataSnapshot rTripDS = tds.child(rTripID);
                            if (rTripDS.getValue() != null) {
                                Trip trip = rTripDS.getValue(Trip.class);
                                String uniName = tds.getKey();

                                for (int i = 0; i < uniList.getUniDetailList().size(); i++) {
                                    if (uniName.equals(uniList.getUniDetailList().get(i).getName())) {
                                        UniDetail uniDetail = uniList.getUniDetailList().get(i);
                                        uniDetailList.add(uniDetail);
                                        break;
                                    }
                                }
                                String driverID = trip.getDriverID();
                                DataSnapshot driverSnapshot = snapshot.child("Users").child(driverID);
                                Driver driver = driverSnapshot.getValue(Driver.class);
                                driverList.add(driver);

                                if (driverSnapshot.hasChild("image")) {
                                    String strImage = driverSnapshot.child("image").getValue().toString();
                                    strDriverImageList.add(strImage);
                                } else {
                                    strDriverImageList.add(null);
                                }

                                StringBuilder sbDayAndTime = new StringBuilder();
                                String prefix = "";

                                for (DataSnapshot timeDS : rTripDS.child("Time").getChildren()) {

                                    Time time = timeDS.getValue(Time.class);
                                    sbDayAndTime.append(prefix);
                                    prefix = "\n";
                                    sbDayAndTime.append(timeDS.getKey() + " " + time.getCheckIn() + "-" + time.getCheckOut());
                                }
                                strDayAndTime.add(sbDayAndTime.toString());
                            }
                        }
                    }
                }
                tripAdapter = new TripAdapter(driverList, strDriverImageList, uniDetailList, strDayAndTime, routeList, context);
                recyclerView.setAdapter(tripAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return passengerHomeFragment;
    }
}