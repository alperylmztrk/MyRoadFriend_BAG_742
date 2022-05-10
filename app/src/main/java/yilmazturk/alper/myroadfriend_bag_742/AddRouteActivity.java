package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import yilmazturk.alper.myroadfriend_bag_742.RouteHelper.FetchURL;
import yilmazturk.alper.myroadfriend_bag_742.RouteHelper.TaskLoadedCallback;
import yilmazturk.alper.myroadfriend_bag_742.databinding.ActivityAddRouteBinding;

public class AddRouteActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private ActivityAddRouteBinding binding;
    private static ArrayList<LatLng> markerPoints;
    private static String uniLocation;
    private Polyline currentPolyline;
    private Button btnViewRoute, btnSaveRoute;
    private ArrayList<Marker> markers = new ArrayList<>();
    private String markerLetter = "ABCDEFGHIJ";
    private int i = 0;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.createRouteMap);
        mapFragment.getMapAsync(this);

        btnViewRoute = findViewById(R.id.btnViewRoute);
        btnSaveRoute = findViewById(R.id.btnSaveRoute);


        client = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();

    }

    private void getCurrentLocation() {
        //check permission
        if (ActivityCompat.checkSelfPermission(AddRouteActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                MarkerOptions options = new MarkerOptions().position(latLng).title("I am here");

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                mMap.addMarker(options);
                            }
                        });
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(AddRouteActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng sukent = new LatLng(39.8513065973451, 32.7050974437213);
        mMap.addMarker(new MarkerOptions().position(sukent).title("Marker in Turkey"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sukent, 15));

        markerPoints = new ArrayList<>();

        IconGenerator iconFactory = new IconGenerator(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (i > 9) {
                    Toast.makeText(AddRouteActivity.this, "You cannot add more", Toast.LENGTH_SHORT).show();
                } else {
                    markerPoints.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Coordinates Lat: " + latLng.latitude + " Lng:" + latLng.longitude)
                            .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(markerLetter.charAt(i) + ""))));
                    i++;
                }

            }
        });


        btnViewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> urls = getDirectionsUrl(markerPoints);
                if (urls.size() >= 1) {
                    for (int i = 0; i < urls.size(); i++) {
                        String url = urls.get(i);
                        new FetchURL(AddRouteActivity.this).execute(url);
                    }
                }
            }
        });
        btnSaveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerPoints.size() < 2) {
                    Toast.makeText(AddRouteActivity.this, "Please create route", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }

            }
        });
    }

    public static ArrayList<LatLng> getMarkerPoints() {
        return markerPoints;
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

    private static ArrayList<List<LatLng>> routePoints=new ArrayList<>();

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null) {
            //   currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        Log.i("rrrrrrrrrrrrrrrr",""+currentPolyline.getPoints());
        routePoints.add(currentPolyline.getPoints());

    }

    public static ArrayList<List<LatLng>> getRoutePoints() {
        return routePoints;
    }

}