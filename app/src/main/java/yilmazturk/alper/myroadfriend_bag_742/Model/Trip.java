package yilmazturk.alper.myroadfriend_bag_742.Model;

import java.util.ArrayList;

public class Trip {

    private String tripID, userID;

    public Trip() {
    }

    public Trip(String tripID, String userID) {
        this.tripID = tripID;
        this.userID = userID;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
