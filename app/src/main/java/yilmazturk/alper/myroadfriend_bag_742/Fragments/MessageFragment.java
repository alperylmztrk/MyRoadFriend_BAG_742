package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yilmazturk.alper.myroadfriend_bag_742.Adapters.MessageAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.Chat;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;


public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<String> userIDList;
    private List<User> userList;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference database;
    private String userID;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        userIDList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        userID = firebaseUser.getUid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View messageFragment = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = messageFragment.findViewById(R.id.recyclerViewMessages);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        database.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDList.clear();

                for (DataSnapshot cds : snapshot.getChildren()) {
                    Chat chat = cds.getValue(Chat.class);

                    if (chat.getSenderID().equals(userID)) {
                        userIDList.add(chat.getReceiverID());
                    }
                    if (chat.getReceiverID().equals(userID)) {
                        userIDList.add(chat.getSenderID());
                    }
                }
                getMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return messageFragment;
    }

    private void getMessages() {

        userList = new ArrayList<>();
        ArrayList<String> msgUserIDList = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference("Users");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot uds : snapshot.getChildren()) {
                    User user = uds.getValue(User.class);

                    for (String id : userIDList) {
                        if (uds.getKey().equals(id)) {
                            if (!userList.isEmpty()) {
                                if (!userList.contains(user)) {
                                    userList.add(user);
                                    msgUserIDList.add(uds.getKey());
                                }
                            } else {
                                userList.add(user);
                                msgUserIDList.add(uds.getKey());
                            }
                        }
                    }
                }
                messageAdapter = new MessageAdapter(userList, msgUserIDList);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}