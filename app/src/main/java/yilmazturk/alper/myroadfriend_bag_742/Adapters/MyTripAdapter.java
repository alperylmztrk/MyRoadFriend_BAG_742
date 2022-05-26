package yilmazturk.alper.myroadfriend_bag_742.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import yilmazturk.alper.myroadfriend_bag_742.Fragments.DriverHomeFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.EditMyTripDialogFragment;
import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;
import yilmazturk.alper.myroadfriend_bag_742.R;
import yilmazturk.alper.myroadfriend_bag_742.SeeRouteActivity;

public class MyTripAdapter extends RecyclerView.Adapter<MyTripAdapter.ViewHolder> {

    private ArrayList<Trip> tripList;
    private ArrayList<String> myDayAndTimeList;
    private ArrayList<Route> myRouteList;
    private ArrayList<UniDetail> myUniDetailList;
    private Driver currentDriver;
    private String strDriverImage;
    FragmentActivity activity;

    LayoutInflater inflater;

    public MyTripAdapter(ArrayList<Trip> tripList, ArrayList<String> myDayAndTimeList, ArrayList<Route> myRouteList, ArrayList<UniDetail> myUniDetailList, Driver currentDriver, String strDriverImage, FragmentActivity activity) {
        this.tripList = tripList;
        this.myDayAndTimeList = myDayAndTimeList;
        this.myRouteList = myRouteList;
        this.myUniDetailList = myUniDetailList;
        this.currentDriver = currentDriver;
        this.strDriverImage = strDriverImage;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyTripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_view_my_trip, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTripAdapter.ViewHolder holder, int position) {

        holder.setData(tripList.get(position), myUniDetailList.get(position), myDayAndTimeList.get(position), myRouteList.get(position), currentDriver);
    }

    @Override
    public int getItemCount() {
        return myRouteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView uniName, dayAndTime, txtRoute, txtEdit, txtDelete;
        ImageButton imgBtnRoute, imgBtnEdit, imgBtnDelete;
        Trip trip;
        Route route;
        UniDetail uniDetail;
        Driver currentDriver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            uniName = itemView.findViewById(R.id.myUniName);
            dayAndTime = itemView.findViewById(R.id.myDayAndTime);
            txtRoute = itemView.findViewById(R.id.myTxtRoute);
            txtEdit = itemView.findViewById(R.id.myTxtEditTrip);
            txtDelete = itemView.findViewById(R.id.myTxtDelTrip);
            imgBtnRoute = itemView.findViewById(R.id.myImgBtnRoute);
            imgBtnEdit = itemView.findViewById(R.id.myImgBtnEditTrip);
            imgBtnDelete = itemView.findViewById(R.id.myImgBtnDelTrip);
            trip = new Trip();
            route = new Route();
            uniDetail = new UniDetail();
            currentDriver = new Driver();

            imgBtnRoute.setOnClickListener(this);
            txtRoute.setOnClickListener(this);
            imgBtnEdit.setOnClickListener(this);
            txtEdit.setOnClickListener(this);
            imgBtnDelete.setOnClickListener(this);
            txtDelete.setOnClickListener(this);

        }

        private void setData(Trip trip, UniDetail uniDetail, String dayAndTime, Route route, Driver currentDriver) {
            this.trip = trip;
            this.route = route;
            this.uniDetail = uniDetail;
            uniName.setText(uniDetail.getName());
            this.dayAndTime.setText(dayAndTime);
            this.currentDriver = currentDriver;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.myImgBtnRoute:
                case R.id.myTxtRoute:
                    //See Route
                    String dNameSurname = currentDriver.getName() + " " + currentDriver.getSurname();
                    SeeRouteActivity.setRouteInfo(route.getWaypoints(), strDriverImage, dNameSurname, uniDetail.getName(), uniDetail.getCity());
                    view.getContext().startActivity(new Intent(view.getContext(), SeeRouteActivity.class));
                    break;
                case R.id.myImgBtnEditTrip:
                case R.id.myTxtEditTrip:
                    //Edit Trip
                    EditMyTripDialogFragment editMyTripDialogFragment = new EditMyTripDialogFragment(trip);
                    editMyTripDialogFragment.show(activity.getSupportFragmentManager(), "UpdateMyTrip");
                    break;
                case R.id.myImgBtnDelTrip:
                case R.id.myTxtDelTrip:
                    //Delete Route
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
                    alertDialog.setTitle("Delete Trip!")
                            .setMessage("Are you sure you really want to delete this trip?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DriverHomeFragment driverHomeFragment = new DriverHomeFragment();
                            driverHomeFragment.deleteTrip(trip, route);
                            activity.recreate();
                        }
                    }).setNegativeButton("No", null).show();
                    break;
            }
        }
    }
}
