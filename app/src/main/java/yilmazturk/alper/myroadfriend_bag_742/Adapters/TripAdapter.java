package yilmazturk.alper.myroadfriend_bag_742.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import yilmazturk.alper.myroadfriend_bag_742.ChatActivity;
import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Route;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;
import yilmazturk.alper.myroadfriend_bag_742.R;
import yilmazturk.alper.myroadfriend_bag_742.SeeRouteActivity;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private ArrayList<Driver> driverList;
    private ArrayList<String> strDriverImageList;
    private ArrayList<String> dayAndTimeList;
    private ArrayList<Route> routeList;
    private ArrayList<UniDetail> uniDetailList;
    private Context context;

    LayoutInflater inflater;

    public TripAdapter(ArrayList<Driver> driverList, ArrayList<String> strDriverImageList, ArrayList<UniDetail> uniDetailList, ArrayList<String> dayAndTimeList, ArrayList<Route> routeList, Context context) {
        this.driverList = driverList;
        this.strDriverImageList = strDriverImageList;
        this.uniDetailList = uniDetailList;
        this.dayAndTimeList = dayAndTimeList;
        this.routeList = routeList;
        this.context = context;
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

        holder.setData(driverList.get(position), strDriverImageList.get(position), uniDetailList.get(position), dayAndTimeList.get(position), routeList.get(position));
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView profilePhoto;
        String strDriverImage;
        TextView nameSurname, username, uniName, dayAndTime, txtRoute, txtMsg;
        ImageButton imgBtnRoute, imgBtnMsg;
        Route route;
        UniDetail uniDetail;
        Driver driver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profilePhotoTrip);
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
        private void setData(Driver driver, String strDriverImage, UniDetail uniDetail, String dayAndTime, Route route) {
            this.route = route;
            this.uniDetail = uniDetail;
            this.driver = driver;
            nameSurname.setText(driver.getName() + " " + driver.getSurname());
            username.setText(driver.getUsername());
            uniName.setText(uniDetail.getName());
            this.dayAndTime.setText(dayAndTime);
            this.strDriverImage = strDriverImage;
            if (strDriverImage != null) {
                Glide.with(context).load(strDriverImage).into(profilePhoto);
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.imageButtonRoute || view.getId() == R.id.textViewRoute) {
                // See Route
                String dNameSurname = driver.getName() + " " + driver.getSurname();
                SeeRouteActivity.setRouteInfo(route.getWaypoints(), strDriverImage, dNameSurname, uniDetail.getName(), uniDetail.getCity());
                view.getContext().startActivity(new Intent(view.getContext(), SeeRouteActivity.class));
            } else if (view.getId() == R.id.imageButtonMessage || view.getId() == R.id.textViewMessage) {
                // Send Message
                ChatActivity.setReceiver(driver);
                view.getContext().startActivity(new Intent(view.getContext(), ChatActivity.class));
            }

        }
    }
}
