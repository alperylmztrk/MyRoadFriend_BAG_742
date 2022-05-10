package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;
import yilmazturk.alper.myroadfriend_bag_742.RouteHelper.FetchURL;
import yilmazturk.alper.myroadfriend_bag_742.RouteHelper.TaskLoadedCallback;

public class SearchTripActivity extends AppCompatActivity implements TaskLoadedCallback {

    Spinner uniSpinner;
    int spinSelectPos;
    TextView txtPickUp, txtUniDest;
    UniList uniList;
    List<String> spinnerData;
    ArrayAdapter<String> dataAdapter;
    String selUniName;
    LinearLayout linLayPickUp;
    Button btnFindTrip;
    private LatLng selectedLocation;

    ArrayList<Route> routeList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        selectedLocation = SearchTripMapActivity.getSelectedLocation();


        Log.w("Location On Resume",""+selectedLocation);
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this,Locale.getDefault());

        if(selectedLocation!=null){
            try {
                addresses=geocoder.getFromLocation(selectedLocation.latitude,selectedLocation.longitude,1);
                txtPickUp.setText(addresses.get(0).getThoroughfare()+", "+addresses.get(0).getSubLocality());
                Log.w("Address",""+addresses.get(0).getAddressLine(0));
                Log.w("Address",""+addresses.get(0).getSubThoroughfare());
                Log.w("Address",""+addresses.get(0).getSubLocality());
                Log.w("Address",""+addresses.get(0));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip);

        uniSpinner = findViewById(R.id.searchTripUniSpinner);
        txtPickUp=findViewById(R.id.textViewPickUpAddress);
        txtUniDest = findViewById(R.id.textViewUniDest);
        linLayPickUp = findViewById(R.id.linLayPickUp);
        btnFindTrip = findViewById(R.id.btnFindTrip);


        Log.w("SelLoc",""+selectedLocation);

        uniList = new UniList();
        spinnerData = new ArrayList<>();

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

        for (int i = 0; i < uniList.getUniDetailList().size(); i++) {
            spinnerData.add(uniList.getUniDetailList().get(i).getName());
        }
        //Sorting the unis according to Turkish alphabet
        Collections.sort(spinnerData, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                Collator collator = Collator.getInstance(new Locale("tr", "TR"));
                return collator.compare(s1, s2);
            }
        });
        spinnerData.add(0, "Select your university");

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerData);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        uniSpinner.setAdapter(dataAdapter);

        uniSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinSelectPos = position;
                for (int i = 0; i < uniList.getUniDetailList().size(); i++) {
                    if (spinnerData.get(position).equals(uniList.getUniDetailList().get(i).getName())) {
                        selUniName = uniList.getUniDetailList().get(i).getName();
                        txtUniDest.setText(selUniName);
                        Log.d("SearchTrip", uniList.getUniDetailList().get(i).getName() + " City: " + uniList.getUniDetailList().get(i).getCity());

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        getRoutePoints();

        linLayPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchTripActivity.this, SearchTripMapActivity.class));
            }
        });

        btnFindTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getRoutePoints();
                SearchTripResultActivity.setSearchedTripInfo(allRoutePoint, routeList, selUniName);
                startActivity(new Intent(SearchTripActivity.this, SearchTripResultActivity.class));

            }
        });


    }

    private void getRoutePoints() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Routes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot routeDS : snapshot.getChildren()) {


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
                    Log.i("RouteID", "" + route.getRouteID());
                    Log.i("RouteTripID", "" + route.getTripID());
                    Log.i("Waypoints", "" + route.getWaypoints());
                    routeList.add(route);

                    List<String> urls = getDirectionsUrl(route.getWaypoints());
                    if (urls.size() >= 1) {
                        for (int i = 0; i < urls.size(); i++) {
                            String url = urls.get(i);
                            new FetchURL(SearchTripActivity.this).execute(url);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private List<String> getDirectionsUrl(ArrayList<LatLng> markerPoints) {

        List<String> urls = new ArrayList<>();
        if (markerPoints.size() > 1) {
            String strOrigin = markerPoints.get(0).latitude + "," + markerPoints.get(0).longitude;
            String strDest = markerPoints.get(1).latitude + "," + markerPoints.get(1).longitude;

            String mode = "mode=driving";
            String parameters = "origin=" + strOrigin + "&destination=" + strDest + "&" + mode;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key="+getString(R.string.maps_api_key);
            urls.add(url);

            for (int i = 2; i < markerPoints.size(); i++) {

                strOrigin = strDest;
                strDest = markerPoints.get(i).latitude + "," + markerPoints.get(i).longitude;
                parameters = "origin=" + strOrigin + "&destination=" + strDest + "&" + mode;
                url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key="+getString(R.string.maps_api_key);
                urls.add(url);
            }
        }
        return urls;
    }

    ArrayList<List<LatLng>> allRoutePoint = new ArrayList<>();

    @Override
    public void onTaskDone(Object... values) {

        PolylineOptions polylineOptions = (PolylineOptions) values[0];
        List<LatLng> routePoints = polylineOptions.getPoints();
        Log.e("Route Points", "" + routePoints);

        allRoutePoint.add(routePoints);


    }

}