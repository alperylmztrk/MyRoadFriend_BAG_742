package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;
import yilmazturk.alper.myroadfriend_bag_742.Adapters.MyTripAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.Chat;
import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Passenger;
import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.Time;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class DriverHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyTripAdapter myTripAdapter;
    private ArrayList<Trip> tripList;
    private ArrayList<String> strDayAndTime;
    private ArrayList<UniDetail> uniDetailList;
    private UniList uniList;
    private ArrayList<Route> currentDriverRouteList;
    private Driver currentDriver;
    private LinearLayoutManager linearLayoutManager;

    private TextView lastMsgSenderUsername, lastMsgDate, lastMsgTime, lastMsgText;

    CircleImageView driverImage;
    private String strImage;
    TextView nameSurname;
    Button btnViewProfile;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference database;


    public DriverHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripList = new ArrayList<>();
        strDayAndTime = new ArrayList<>();
        currentDriverRouteList = new ArrayList<>();
        uniDetailList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity());

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        uniList = getUniList();

        String driverID = firebaseUser.getUid();
        showDriverInfo(driverID);
        showMyTrip(driverID);
        showLastMessage(driverID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View driverHomeFragment = inflater.inflate(R.layout.fragment_driver_home, container, false);

        recyclerView = driverHomeFragment.findViewById(R.id.recyclerViewDriverHome);
        driverImage = driverHomeFragment.findViewById(R.id.photoDriver);
        nameSurname = driverHomeFragment.findViewById(R.id.nameSurnameDHome);
        btnViewProfile = driverHomeFragment.findViewById(R.id.driverViewProfile);
        lastMsgSenderUsername = driverHomeFragment.findViewById(R.id.lastMsgSenderDriverHome);
        lastMsgDate = driverHomeFragment.findViewById(R.id.lastMsgDateDriverHome);
        lastMsgTime = driverHomeFragment.findViewById(R.id.lastMsgTimeDriverHome);
        lastMsgText = driverHomeFragment.findViewById(R.id.lastMsgTextDriverHome);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        btnViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();

            }
        });


        return driverHomeFragment;
    }

    private void showDriverInfo(String driverID) {

        database.child("Users").child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("image")) {
                    strImage = snapshot.child("image").getValue().toString();
                    Glide.with(getContext()).load(strImage).into(driverImage);
                }

                Driver driver = snapshot.getValue(Driver.class);
                String strNameSurname = driver.getName() + " " + driver.getSurname();
                nameSurname.setText(strNameSurname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Driver Home Fragment", "showDriverInfo:onCancelled", error.toException());
            }
        });

    }

    private UniList getUniList() {
        UniList uniList = new UniList();
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
        return uniList;
    }

    private void showMyTrip(String currentDriverID) {

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //check driver has a trip or not
                Boolean hasTrip = false;
                DataSnapshot tripSnapshot = snapshot.child("Trips");
                for (DataSnapshot tds : tripSnapshot.getChildren()) {
                    for (DataSnapshot tds2 : tds.getChildren()) {
                        Trip trip = tds2.getValue(Trip.class);
                        if (trip.getDriverID().equals(currentDriverID)) {
                            hasTrip = true;
                        }
                    }
                }
                if (!hasTrip) {
                    Toast.makeText(getActivity(), "You do not have a trip! ", Toast.LENGTH_SHORT).show();
                } else {

                    for (DataSnapshot routeDS : snapshot.child("Routes").getChildren()) {
                        String routeID = routeDS.child("routeID").getValue().toString();
                        String rTripID = routeDS.child("tripID").getValue().toString();

                        for (DataSnapshot tds : tripSnapshot.getChildren()) {
                            for (DataSnapshot tds2 : tds.getChildren()) {
                                Trip trip = tds2.getValue(Trip.class);
                                if (rTripID.equals(trip.getTripID()) && trip.getDriverID().equals(currentDriverID)) {
                                    tripList.add(trip);
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

                                    currentDriverRouteList.add(route);

                                    String uniName = tds.getKey();
                                    for (int i = 0; i < uniList.getUniDetailList().size(); i++) {
                                        if (uniName.equals(uniList.getUniDetailList().get(i).getName())) {
                                            UniDetail uniDetail = uniList.getUniDetailList().get(i);
                                            uniDetailList.add(uniDetail);
                                            break;
                                        }
                                    }
                                    DataSnapshot driverSnapshot = snapshot.child("Users").child(currentDriverID);
                                    currentDriver = driverSnapshot.getValue(Driver.class);

                                    StringBuilder sbDayAndTime = new StringBuilder();
                                    String prefix = "";

                                    DataSnapshot rTripDS = tds.child(rTripID);
                                    for (DataSnapshot timeDS : rTripDS.child("Time").getChildren()) {

                                        Time time = timeDS.getValue(Time.class);
                                        Log.i("Time info", timeDS.getKey() + " " + time.getCheckIn() + "-" + time.getCheckOut());
                                        sbDayAndTime.append(prefix);
                                        prefix = "\n";
                                        sbDayAndTime.append(timeDS.getKey() + " " + time.getCheckIn() + "-" + time.getCheckOut());
                                    }
                                    strDayAndTime.add(sbDayAndTime.toString());
                                }
                            }
                        }
                    }
                }

                myTripAdapter = new MyTripAdapter(tripList, strDayAndTime, currentDriverRouteList, uniDetailList, currentDriver, strImage, getActivity());
                recyclerView.setAdapter(myTripAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void deleteTrip(Trip trip, Route route) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot tripSnapshot = snapshot.child("Trips");
                for (DataSnapshot tds : tripSnapshot.getChildren()) {
                    for (DataSnapshot uniTds : tds.getChildren()) {
                        String tripID = uniTds.child("tripID").getValue().toString();
                        if (tripID.equals(trip.getTripID())) {
                            uniTds.getRef().removeValue();
                        }
                    }
                }
                DataSnapshot routeSnapshot = snapshot.child("Routes");
                for (DataSnapshot rds : routeSnapshot.getChildren()) {
                    String routeID = rds.child("routeID").getValue().toString();
                    if (routeID.equals(route.getRouteID())) {
                        rds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showLastMessage(String driverID) {

        database.addValueEventListener(new ValueEventListener() {
            String senderUsernameStr;
            String lastMsgDateStr;
            String lastMsgTimeStr;
            String lastMsgTextStr;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot chatSnapshot = snapshot.child("Chats");
                for (DataSnapshot cds : chatSnapshot.getChildren()) {
                    Chat chat = cds.getValue(Chat.class);

                    if (chat.getReceiverID().equals(driverID)) {

                        DataSnapshot senderSnapshot = snapshot.child("Users").child(chat.getSenderID());

                        Passenger passenger = senderSnapshot.getValue(Passenger.class);
                        senderUsernameStr = passenger.getUsername();
                        lastMsgDateStr = chat.getDate();
                        lastMsgTimeStr = chat.getTime();
                        lastMsgTextStr = chat.getMessage();
                    }
                }
                lastMsgSenderUsername.setText(senderUsernameStr);
                lastMsgDate.setText(lastMsgDateStr);
                lastMsgTime.setText(lastMsgTimeStr);
                lastMsgText.setText(lastMsgTextStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}