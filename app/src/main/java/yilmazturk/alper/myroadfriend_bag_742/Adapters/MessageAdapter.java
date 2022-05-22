package yilmazturk.alper.myroadfriend_bag_742.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yilmazturk.alper.myroadfriend_bag_742.ChatActivity;
import yilmazturk.alper.myroadfriend_bag_742.Model.Chat;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<User> userList;
    private List<String> userIDList;
    LayoutInflater inflater;

    public MessageAdapter(List<User> userList, List<String> userIDList) {
        this.userList = userList;
        this.userIDList = userIDList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_view_message, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(userList.get(position), userIDList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameAndSurname, lastMessageText, date, time, unseenMessage;
        LinearLayout rootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameAndSurname = itemView.findViewById(R.id.nameSurnameMsg);
            lastMessageText = itemView.findViewById(R.id.textViewLastMsgText);
            date = itemView.findViewById(R.id.textViewMsgDate);
            time = itemView.findViewById(R.id.textViewMsgTime);
            unseenMessage = itemView.findViewById(R.id.unseenMessage);
            rootLayout = itemView.findViewById(R.id.rootLayout);

        }

        @SuppressLint("SetTextI18n")
        private void setData(User user, String userID) {
            List<String> unseenMsgKeyList = new ArrayList<>();
            nameAndSurname.setText(user.getName() + " " + user.getSurname());

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatActivity.setReceiver(user);
                    view.getContext().startActivity(new Intent(view.getContext(), ChatActivity.class));
                }
            });

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Chats");
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot cds : snapshot.getChildren()) {
                        Chat chat = cds.getValue(Chat.class);

                        if (chat.getReceiverID().equals(firebaseUser.getUid()) && chat.getSenderID().equals(userID) ||
                                chat.getReceiverID().equals(userID) && chat.getSenderID().equals(firebaseUser.getUid())) {
                            lastMessageText.setText(chat.getMessage());
                            date.setText(chat.getDate());
                            time.setText(chat.getTime());
                        }
                        if (chat.getReceiverID().equals(firebaseUser.getUid()) && chat.getSenderID().equals(userID)) {
                            if (!chat.isSeen()) {

                                if (!unseenMsgKeyList.contains(cds.getKey())) {
                                    unseenMsgKeyList.add(cds.getKey());
                                }
                            }
                        }
                    }

                    if (!unseenMsgKeyList.isEmpty()) {
                        unseenMessage.setText(String.valueOf(unseenMsgKeyList.size()));
                    } else {
                        unseenMessage.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
