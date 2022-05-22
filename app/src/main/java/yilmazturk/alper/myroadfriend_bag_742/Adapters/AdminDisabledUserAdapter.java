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
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class AdminDisabledUserAdapter extends RecyclerView.Adapter<AdminDisabledUserAdapter.ViewHolder> {

    private ArrayList<String> disUserIDList;
    private ArrayList<User> disUserList;

    LayoutInflater inflater;

    public AdminDisabledUserAdapter(ArrayList<String> disUserIDList, ArrayList<User> disUserList) {
        this.disUserIDList = disUserIDList;
        this.disUserList = disUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_view_admin_disabled_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(disUserIDList.get(position), disUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return disUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userID, nameSurname;
        Button btnEnableUser;
        User disUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userID = itemView.findViewById(R.id.disUID);
            nameSurname = itemView.findViewById(R.id.disUnameSurname);
            btnEnableUser = itemView.findViewById(R.id.btnEnableUser);
            disUser = new User();

            btnEnableUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminDisableUserFragment.enableUser(userID.getText().toString());
                }
            });

        }

        @SuppressLint("SetTextI18n")
        private void setData(String userID, User user) {
            disUser = user;
            this.userID.setText(userID);
            nameSurname.setText(user.getName() + " " + user.getSurname());

        }

    }
}
