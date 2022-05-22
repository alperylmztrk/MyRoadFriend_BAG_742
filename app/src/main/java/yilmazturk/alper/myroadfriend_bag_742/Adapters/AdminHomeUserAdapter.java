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

import yilmazturk.alper.myroadfriend_bag_742.Fragments.AdminDisableUserFragment;
import yilmazturk.alper.myroadfriend_bag_742.Fragments.AdminHomeFragment;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class AdminHomeUserAdapter extends RecyclerView.Adapter<AdminHomeUserAdapter.ViewHolder> {

    private ArrayList<String> userIDList;
    private ArrayList<User> userList;

    LayoutInflater inflater;

    public AdminHomeUserAdapter(ArrayList<String> userIDList, ArrayList<User> userList) {
        this.userIDList = userIDList;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_view_admin_home_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(userIDList.get(position), userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userID, nameSurname, userType;
        Button btnDisableUser;
        User user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userID = itemView.findViewById(R.id.textViewAdminHomeUID);
            nameSurname = itemView.findViewById(R.id.textViewAdminUsrNameSurname);
            userType = itemView.findViewById(R.id.textViewAdminUsrType);
            btnDisableUser = itemView.findViewById(R.id.btnDisableUser);
            user = new User();

            btnDisableUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminHomeFragment.disableUser(userID.getText().toString());
                }
            });

        }

        @SuppressLint("SetTextI18n")
        private void setData(String userID, User user) {
            this.user = user;
            this.userID.setText(userID);
            nameSurname.setText(user.getName() + " " + user.getSurname());
            userType.setText(user.getUserType());
        }

    }
}
