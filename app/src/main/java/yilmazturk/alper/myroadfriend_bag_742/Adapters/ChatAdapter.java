package yilmazturk.alper.myroadfriend_bag_742.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import yilmazturk.alper.myroadfriend_bag_742.Model.Chat;
import yilmazturk.alper.myroadfriend_bag_742.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private List<Chat> chatList;
    FirebaseUser firebaseUser;

    LayoutInflater inflater;

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());

        if (viewType == MSG_TYPE_RIGHT) {
            View itemView = inflater.inflate(R.layout.message_item_right, parent, false);
            return new ViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.message_item_left, parent, false);
            return new ViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.setMessage(chatList.get(position));
        if (position == chatList.size() - 1) {
            if (chatList.get(position).isSeen()) {
                holder.seen.setText("Seen");
            } else {
                holder.seen.setText("Delivered");
            }
        } else {
            holder.seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView showMessage, seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.showMessage);
            seen = itemView.findViewById(R.id.txtSeen);
        }

        private void setMessage(Chat chat) {
            showMessage.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSenderID().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
