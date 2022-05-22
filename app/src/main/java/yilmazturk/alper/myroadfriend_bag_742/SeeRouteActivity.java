package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;
import yilmazturk.alper.myroadfriend_bag_742.RouteHelper.FetchURL;
import yilmazturk.alper.myroadfriend_bag_742.RouteHelper.TaskLoadedCallback;
import yilmazturk.alper.myroadfriend_bag_742.databinding.ActivityAddRouteBinding;
import yilmazturk.alper.myroadfriend_bag_742.databinding.ActivitySeeRouteBinding;

public class SeeRouteActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private ActivitySeeRouteBinding binding;
    private static ArrayList<LatLng> markerPoints = new ArrayList<>();
    String markerLetter = "ABCDEFGHIJ";

    private TextView dNameSurnameTxt, uniNameTxt, cityNameTxt;
    private static String dNameSurname, uniName, cityName;

    public static void setRouteInfo(ArrayList<LatLng> markPoint, String dNameSurname1, String uniName1, String cityName1) {
        markerPoints = markPoint;
        dNameSurname = dNameSurname1;
        uniName = uniName1;
        cityName = cityName1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySeeRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.seeRouteMap);
        mapFragment.getMapAsync(this);

        dNameSurnameTxt = findViewById(R.id.dNameSurnameSeeRoute);
        uniNameTxt = findViewById(R.id.uniNameSeeRoute);
        cityNameTxt = findViewById(R.id.cityNameSeeRoute);

        dNameSurnameTxt.setText(dNameSurname);
        uniNameTxt.setText(uniName);
        cityNameTxt.setText(cityName);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double distance = SphericalUtil.computeDistanceBetween(markerPoints.get(0), markerPoints.get(1));
        Log.i("Distance", "" + distance / 1000 + " km");

        LatLng turkey = new LatLng(39.069732317424645, 35.4112759901074);
        //mMap.addMarker(new MarkerOptions().position(turkey).title("Marker in Turkey"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey, 10));

        IconGenerator iconFactory = new IconGenerator(this);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < markerPoints.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(markerPoints.get(i)).title("Lat: " + markerPoints.get(i).latitude + " Lng: " + markerPoints.get(i).longitude)
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(markerLetter.charAt(i) + ""))));

            builder.include(markerPoints.get(i));
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.15);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cameraUpdate, 2000, null);

        List<String> urls = getDirectionsUrl(markerPoints);
        if (urls.size() >= 1) {
            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                new FetchURL(SeeRouteActivity.this).execute(url);
            }
        }
    }

    private List<String> getDirectionsUrl(ArrayList<LatLng> markerPoints) {

        List<String> urls = new ArrayList<>();
        if (markerPoints.size() > 1) {
            String strOrigin = markerPoints.get(0).latitude + "," + markerPoints.get(0).longitude;
            String strDest = markerPoints.get(1).latitude + "," + markerPoints.get(1).longitude;

            String mode = "mode=driving";
            String parameters = "origin=" + strOrigin + "&destination=" + strDest + "&" + mode;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.maps_api_key);
            urls.add(url);

            for (int i = 2; i < markerPoints.size(); i++) {

                strOrigin = strDest;
                strDest = markerPoints.get(i).latitude + "," + markerPoints.get(i).longitude;
                parameters = "origin=" + strOrigin + "&destination=" + strDest + "&" + mode;
                url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.maps_api_key);
                urls.add(url);
            }
        }
        return urls;
    }

    @Override
    public void onTaskDone(Object... values) {
        mMap.addPolyline((PolylineOptions) values[0]);
    }


}