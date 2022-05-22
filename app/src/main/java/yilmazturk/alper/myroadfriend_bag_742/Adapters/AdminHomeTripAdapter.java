package yilmazturk.alper.myroadfriend_bag_742.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class AdminHomeTripAdapter extends RecyclerView.Adapter<AdminHomeTripAdapter.ViewHolder> {

    private ArrayList<Trip> tripList;

    LayoutInflater inflater;

    public AdminHomeTripAdapter(ArrayList<Trip> tripList) {
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_view_admin_home_trip, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminHomeTripAdapter.ViewHolder holder, int position) {
        holder.setData(tripList.get(position));
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tripID, driverID;
        Button btnRemoveTrip;
        Trip trip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripID = itemView.findViewById(R.id.textViewAdminHomeTripID);
            driverID = itemView.findViewById(R.id.textViewAdminHomeDID);
            btnRemoveTrip = itemView.findViewById(R.id.btnRemoveTrip);
            trip = new Trip();

            btnRemoveTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @SuppressLint("SetTextI18n")
        private void setData(Trip trip) {
            this.trip = trip;
            tripID.setText("Trip ID: " + trip.getTripID());
            driverID.setText("Driver ID: " + trip.getDriverID());
        }

    }
}
