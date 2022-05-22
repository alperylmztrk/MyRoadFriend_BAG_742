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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import yilmazturk.alper.myroadfriend_bag_742.RouteHelper.TaskLoadedCallback;
import yilmazturk.alper.myroadfriend_bag_742.databinding.ActivityAddRouteBinding;
import yilmazturk.alper.myroadfriend_bag_742.databinding.ActivitySearchTripMapBinding;

public class SearchTripMapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private ActivitySearchTripMapBinding binding;
    SupportMapFragment mapFragment;
    private static LatLng selectedLocation;
    private Button btnUseLocation;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchTripMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.searchTripMap);
        mapFragment.getMapAsync(this);

        btnUseLocation = findViewById(R.id.btnUseLocation);

        client = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();

    }

    private void getCurrentLocation() {
        //check permission
        if (ActivityCompat.checkSelfPermission(SearchTripMapActivity.this,
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
                                MarkerOptions options = new MarkerOptions().position(latLng).title("You are here");

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                //mMap.addMarker(options);
                            }
                        });
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(SearchTripMapActivity.this,
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(SearchTripMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        btnUseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedLocation = mMap.getCameraPosition().target;
                finish();

            }
        });
    }

    public static LatLng getSelectedLocation() {
        return selectedLocation;
    }

    @Override
    public void onTaskDone(Object... values) {

    }
}