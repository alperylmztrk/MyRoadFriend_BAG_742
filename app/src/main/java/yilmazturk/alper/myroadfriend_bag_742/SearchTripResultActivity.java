package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import yilmazturk.alper.myroadfriend_bag_742.Adapters.TripAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.Time;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;

public class SearchTripResultActivity extends AppCompatActivity {

    private ImageView imgNoData;
    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private ArrayList<Driver> driverList;
    private ArrayList<String> strDriverImageList;
    private ArrayList<UniDetail> uniDetailList;
    private UniList uniList;
    private ArrayList<String> strDayAndTime;
    private static String searchedUniName;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Route> uniFoundedRoutes;
    Context context;

    private LatLng selectedLocation;

    ArrayList<ArrayList<List<LatLng>>> allRoutePointsList;
    ArrayList<Route> foundedRoutes;

    private static ArrayList<List<LatLng>> allRoutePoints = new ArrayList<>();
    private static ArrayList<Route> routeList = new ArrayList<>();

    public static void setSearchedTripInfo(ArrayList<List<LatLng>> allPoints, ArrayList<Route> routes, String uniName) {
        allRoutePoints = allPoints;
        routeList = routes;
        searchedUniName = uniName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip_result);

        imgNoData = findViewById(R.id.imgNoData);

        context = getApplicationContext();

        allRoutePointsList = new ArrayList<>();
        foundedRoutes = new ArrayList<>();
        uniFoundedRoutes = new ArrayList<>();

        selectedLocation = SearchTripMapActivity.getSelectedLocation();

        driverList = new ArrayList<>();
        strDriverImageList = new ArrayList<>();
        strDayAndTime = new ArrayList<>();
        uniDetailList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerViewSearchResults);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

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


        int s = 0;

        for (int i = 0; i < routeList.size(); i++) {

            ArrayList<List<LatLng>> routePointParts = new ArrayList<>();
            for (int j = 0; j < (routeList.get(i).getWaypoints().size() - 1); j++) {

                routePointParts.add(allRoutePoints.get(s));
                s++;
            }
            allRoutePointsList.add(routePointParts);
        }

        for (int i = 0; i < allRoutePointsList.size(); i++) {
            for (int j = 0; j < allRoutePointsList.get(i).size(); j++) {
                for (int n = 0; n < allRoutePointsList.get(i).get(j).size(); n++) {
                    double distance = SphericalUtil.computeDistanceBetween(selectedLocation, allRoutePointsList.get(i).get(j).get(n));
                    if (distance / 1000 <= 1) {
                        foundedRoutes.add(routeList.get(i));
                        break;
                    }
                }
            }
        }

        foundedRoutes = new ArrayList<>(new HashSet<>(foundedRoutes));

        for (int i = 0; i < foundedRoutes.size(); i++) {
            Log.w("FoundedRouteID", "" + foundedRoutes.get(i).getRouteID());
        }

        setTripAdapter();

    }

    private void setTripAdapter() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot tripSnapshot = snapshot.child("Trips");

                for (int i = 0; i < foundedRoutes.size(); i++) {
                    String rTripID = foundedRoutes.get(i).getTripID();

                    for (DataSnapshot tds : tripSnapshot.getChildren()) {
                        DataSnapshot rTripDS = tds.child(rTripID);
                        String uniName = tds.getKey();
                        if (rTripDS.getValue() != null && uniName.equals(searchedUniName)) {
                            Trip trip = rTripDS.getValue(Trip.class);
                            uniFoundedRoutes.add(foundedRoutes.get(i));

                            for (int j = 0; j < uniList.getUniDetailList().size(); j++) {
                                if (uniName.equals(uniList.getUniDetailList().get(j).getName())) {
                                    UniDetail uniDetail = uniList.getUniDetailList().get(j);
                                    uniDetailList.add(uniDetail);
                                    break;
                                }
                            }
                            String driverID = trip.getDriverID();
                            DataSnapshot driverSnapshot = snapshot.child("Users").child(driverID);
                            if (driverSnapshot.hasChild("image")) {
                                String strImage = driverSnapshot.child("image").getValue().toString();
                                strDriverImageList.add(strImage);
                            } else {
                                strDriverImageList.add(null);
                            }
                            Driver driver = driverSnapshot.getValue(Driver.class);

                            driverList.add(driver);

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

                if (uniFoundedRoutes.isEmpty()) {
                    imgNoData.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchTripResultActivity.this, "No Trips Founded!", Toast.LENGTH_SHORT).show();
                }
                tripAdapter = new TripAdapter(driverList, strDriverImageList, uniDetailList, strDayAndTime, uniFoundedRoutes, context);
                recyclerView.setAdapter(tripAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}