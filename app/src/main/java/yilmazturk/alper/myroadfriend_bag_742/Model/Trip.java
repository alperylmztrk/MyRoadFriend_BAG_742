package yilmazturk.alper.myroadfriend_bag_742.Model;

public class Trip {

    private String tripID, driverID;

    public Trip() {
    }

    public Trip(String tripID, String userID) {
        this.tripID = tripID;
        this.driverID = userID;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
}
