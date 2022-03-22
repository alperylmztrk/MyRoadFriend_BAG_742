package yilmazturk.alper.myroadfriend_bag_742.Model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class Driver extends User {


    public Driver() {
    }

    public Driver(String name, String surname, String username, String email, String password, String userType) {
        super(name, surname, username, email, password, userType);
    }


    public void postTrip(DatabaseReference database, String uniID, ArrayList<String> days, String userID, ArrayList<Time> times) {

        DatabaseReference tripRef = database.child("Trips").child(uniID);
        // Unique key for each trip
        String tripID = tripRef.push().getKey();
        tripRef.child(tripID).setValue(new Trip(tripID, userID));
        for (int i = 0; i < days.size(); i++) {
            Log.i("Time", "FOR");
            if (days.get(i).equals("Monday")) {
                tripRef.child(tripID).child(days.get(i)).setValue(times.get(0));
                Log.i("Time", "Monday");
            }
            if (days.get(i).equals("Tuesday")) {
                tripRef.child(tripID).child(days.get(i)).setValue(times.get(1));
                Log.i("Time", "Tuesday");
            }
            if (days.get(i).equals("Wednesday")) {
                tripRef.child(tripID).child(days.get(i)).setValue(times.get(2));
                Log.i("Time", "Wednesday");
            }
            if (days.get(i).equals("Thursday")) {
                tripRef.child(tripID).child(days.get(i)).setValue(times.get(3));
                Log.i("Time", "Thursday");
            }
            if (days.get(i).equals("Friday")) {
                tripRef.child(tripID).child(days.get(i)).setValue(times.get(4));
                Log.i("Time", "Friday");
            }
        }
    }


}
