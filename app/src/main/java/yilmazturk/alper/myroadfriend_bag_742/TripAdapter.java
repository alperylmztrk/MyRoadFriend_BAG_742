package yilmazturk.alper.myroadfriend_bag_742;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.Time;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private ArrayList<Driver> driverList;
    private ArrayList<String> dayAndTimeList;
    private ArrayList<Route> routeList;
    private ArrayList<UniDetail> uniDetailList;

    LayoutInflater inflater;


    public TripAdapter(ArrayList<Driver> driverList, ArrayList<UniDetail> uniDetailList, ArrayList<String> dayAndTimeList, ArrayList<Route> routeList) {
        this.driverList = driverList;
        this.uniDetailList = uniDetailList;
        this.dayAndTimeList = dayAndTimeList;
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_view_trip, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setData(driverList.get(position), uniDetailList.get(position), dayAndTimeList.get(position), routeList.get(position));

    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView profilePhoto, imgUni;
        TextView nameSurname, username, uniName, dayAndTime, txtRoute, txtMsg;
        ImageButton imgBtnRoute, imgBtnMsg;
        Route route;
        UniDetail uniDetail;
        Driver driver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            imgUni = itemView.findViewById(R.id.imgUni);
            nameSurname = itemView.findViewById(R.id.nameSurname);
            username = itemView.findViewById(R.id.username);
            uniName = itemView.findViewById(R.id.uniName);
            dayAndTime = itemView.findViewById(R.id.dayAndTime);
            imgBtnRoute = itemView.findViewById(R.id.imageButtonRoute);
            txtRoute = itemView.findViewById(R.id.textViewRoute);
            imgBtnMsg = itemView.findViewById(R.id.imageButtonMessage);
            txtMsg = itemView.findViewById(R.id.textViewMessage);
            route = new Route();
            uniDetail = new UniDetail();
            driver = new Driver();


            imgBtnRoute.setOnClickListener(this);
            txtRoute.setOnClickListener(this);
            imgBtnMsg.setOnClickListener(this);
            txtMsg.setOnClickListener(this);

        }

        @SuppressLint("SetTextI18n")
        private void setData(Driver driver, UniDetail uniDetail, String dayAndTime, Route route) {

            this.route = route;
            this.uniDetail = uniDetail;
            this.driver = driver;
            nameSurname.setText(driver.getName() + " " + driver.getSurname());
            username.setText(driver.getUsername());
            this.uniName.setText(uniDetail.getName());
            this.dayAndTime.setText(dayAndTime);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageButtonRoute:
                case R.id.textViewRoute:
                    //See Route
                    Log.i("adapter", "" + route.getWaypoints());
                    String dNameSurname = driver.getName() + " " + driver.getSurname();
                    SeeRouteActivity.setRouteInfo(route.getWaypoints(), dNameSurname, uniDetail.getName(), uniDetail.getCity());
                    view.getContext().startActivity(new Intent(view.getContext(), SeeRouteActivity.class));

                    break;
                case R.id.imageButtonMessage:
                case R.id.textViewMessage:
                    // Send Message
                    MessageActivity.setReceiver(driver);
                    view.getContext().startActivity(new Intent(view.getContext(), MessageActivity.class));

                    break;
            }
        }

    }

}
