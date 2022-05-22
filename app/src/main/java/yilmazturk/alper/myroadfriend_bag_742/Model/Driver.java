package yilmazturk.alper.myroadfriend_bag_742.Model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Driver extends User {


    public Driver() {
    }

    public Driver(String name, String surname, String username, String email, String password, String userType) {
        super(name, surname, username, email, password, userType);
    }


    public void postTrip(String uniName, String driverID, ArrayList<String> days, ArrayList<Time> times, ArrayList<LatLng> waypoints) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tripRef = database.child("Trips").child(uniName);
        // Unique key for each trip
        String tripID = tripRef.push().getKey();
        tripRef.child(tripID).setValue(new Trip(tripID, driverID));
        for (int i = 0; i < days.size(); i++) {

            if (days.get(i).equals("Monday")) {
                tripRef.child(tripID).child("Time").child(days.get(i)).setValue(times.get(0));
                Log.i("Time", "Monday");
            }
            if (days.get(i).equals("Tuesday")) {
                tripRef.child(tripID).child("Time").child(days.get(i)).setValue(times.get(1));
                Log.i("Time", "Tuesday");
            }
            if (days.get(i).equals("Wednesday")) {
                tripRef.child(tripID).child("Time").child(days.get(i)).setValue(times.get(2));
                Log.i("Time", "Wednesday");
            }
            if (days.get(i).equals("Thursday")) {
                tripRef.child(tripID).child("Time").child(days.get(i)).setValue(times.get(3));
                Log.i("Time", "Thursday");
            }
            if (days.get(i).equals("Friday")) {
                tripRef.child(tripID).child("Time").child(days.get(i)).setValue(times.get(4));
                Log.i("Time", "Friday");
            }
        }
        DatabaseReference routeRef = database.child("Routes");
        // Unique key for each route
        String routeID = routeRef.push().getKey();
        routeRef.child(routeID).setValue(new Route(routeID, tripID, waypoints));

    }


}
