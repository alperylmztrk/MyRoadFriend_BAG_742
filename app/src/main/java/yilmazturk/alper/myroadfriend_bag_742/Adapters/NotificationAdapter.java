package yilmazturk.alper.myroadfriend_bag_742.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<User> senderList;
    private ArrayList<String> dateAndTimeList;

    LayoutInflater inflater;

    public NotificationAdapter(ArrayList<User> senderList, ArrayList<String> dateAndTimeList) {
        this.senderList = senderList;
        this.dateAndTimeList = dateAndTimeList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_view_notification, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        holder.setData(senderList.get(position), dateAndTimeList.get(position));
    }

    @Override
    public int getItemCount() {
        return senderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView proPhoto;
        TextView senderNameAndSurname, txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderNameAndSurname = itemView.findViewById(R.id.textViewNotiNameSurname);
            txtTime = itemView.findViewById(R.id.textViewNotiTime);
        }

        @SuppressLint("SetTextI18n")
        private void setData(User user, String dateAndTime) {
            senderNameAndSurname.setText(user.getName() + " " + user.getSurname());
            calculatePassedTime(dateAndTime);
        }

        @SuppressLint("SetTextI18n")
        private void calculatePassedTime(String dateTime) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                Date chatDate = simpleDateFormat.parse(dateTime);
                Date currentDate = simpleDateFormat.parse(simpleDateFormat.format(new Date()));

                // Calucalte time difference in milliseconds
                long timeDifference = currentDate.getTime() - chatDate.getTime();
                // Calucalte time difference in years
                long yearDifference = (timeDifference / (1000L * 60 * 60 * 24 * 365));
                // Calucalte time difference in days
                long dayDifference = (timeDifference / (1000 * 60 * 60 * 24)) % 365;
                // Calucalte time difference in hours
                long hourDifference = (timeDifference / (1000 * 60 * 60)) % 24;
                // Calucalte time difference in minutes
                long minuteDifference = (timeDifference / (1000 * 60)) % 60;

                if (yearDifference == 0) {
                    if (dayDifference == 0) {
                        if (hourDifference == 0) {
                            txtTime.setText(minuteDifference + "m");
                        } else {
                            txtTime.setText(hourDifference + "h");
                        }
                    } else txtTime.setText(dayDifference + "d");
                } else {
                    txtTime.setText(yearDifference + "y");
                }

            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }

        }
    }
}
