package yilmazturk.alper.myroadfriend_bag_742;

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

import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.Time;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;


public class PassengerHomeFragment extends Fragment {
    RecyclerView recyclerView;
    TripAdapter tripAdapter;
    ArrayList<Driver> driverList;

    ArrayList<UniDetail> uniDetailList;
    private  UniList uniList;
    ArrayList<Route> routeList;
    ArrayList<ArrayList<String>> dayList;
    ArrayList<ArrayList<Time>> timeList;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference database;



    public PassengerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        driverList = new ArrayList<>();
        dayList = new ArrayList<>();
        timeList = new ArrayList<>();
        routeList = new ArrayList<>();
        uniDetailList = new ArrayList<>();
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
                        Log.i("ROTA ID", routeID);
                        Log.i("ROTA trip ID", rTripID);
                        ArrayList<LatLng> waypointList = new ArrayList<>();
                        for (DataSnapshot rWayDS : routeDS.child("waypoints").getChildren()) {

                            String lat = rWayDS.child("latitude").getValue().toString();
                            String lng = rWayDS.child("longitude").getValue().toString();

                            Log.i("ROTA Lat", lat);
                            Log.i("ROTA Lng", lng);

                            double latitude = Double.parseDouble(lat);
                            double longitude = Double.parseDouble(lng);
                            LatLng waypoint = new LatLng(latitude, longitude);
                            waypointList.add(waypoint);
                        }
                        Route route = new Route(routeID, rTripID, waypointList);

                        routeList.add(route);


                        DataSnapshot trip2Snapshot = snapshot.child("Trips");

                        for (DataSnapshot tds2 : trip2Snapshot.getChildren()) {
                            DataSnapshot rTripDS = tds2.child(rTripID);
                            if (rTripDS.getValue() != null) {
                                Trip trip = rTripDS.getValue(Trip.class);
                                String uniName = tds2.getKey();

                                for (int i = 0; i< uniList.getUniDetailList().size(); i++){
                                    if (uniName.equals(uniList.getUniDetailList().get(i).getName())){
                                        UniDetail uniDetail= uniList.getUniDetailList().get(i);
                                        uniDetailList.add(uniDetail);
                                        break;
                                    }
                                }

                                String driverID = trip.getDriverID();
                                Log.i("UNİ NAMEE", "" + uniName);
                                Log.i("TRIP driverID", "" + tds2.child(rTripID).child("driverID").getValue());
                                Log.i("TRIP driverID", "" + driverID);
                                DataSnapshot driverSnapshot = snapshot.child("Users").child(driverID);
                                Driver driver = driverSnapshot.getValue(Driver.class);
                                Log.i("Driver", " " + driver.getName());

                                Log.i("PassHomeRouteID", "" + route.getRouteID());
                                Log.i("PassHomeRoute", "" + route.getWaypoints());

                               // uniList.add(uniName);
                                driverList.add(driver);
                                ArrayList<String> days = new ArrayList<>();
                                ArrayList<Time> times = new ArrayList<>();

                                // Gün kadar çalışıyor
                                for (DataSnapshot timeDS : rTripDS.child("Time").getChildren()) {

                                    Time time = timeDS.getValue(Time.class);
                                    Log.i("Time info", timeDS.getKey() + " " + time.getCheckIn() + "-" + time.getCheckOut());
                                    days.add(timeDS.getKey());
                                    times.add(time);
                                }
                                dayList.add(days);
                                timeList.add(times);
                            }
                        }
                    }
                }
                Log.i("Day Info", " " + dayList);

                tripAdapter = new TripAdapter(driverList, uniDetailList, timeList, dayList, routeList);
                recyclerView.setAdapter(tripAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return passengerHomeFragment;
    }
}