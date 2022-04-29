package yilmazturk.alper.myroadfriend_bag_742.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Route {
    private String routeID, tripID;
    ArrayList<LatLng> waypoints;

    public Route() {
    }

    public Route(String routeID, String tripID, ArrayList<LatLng> waypoints) {
        this.routeID = routeID;
        this.tripID = tripID;
        this.waypoints = waypoints;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public ArrayList<LatLng> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<LatLng> waypoints) {
        this.waypoints = waypoints;
    }
}
