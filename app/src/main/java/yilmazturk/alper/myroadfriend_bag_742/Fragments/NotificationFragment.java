package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.content.Context;
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

import yilmazturk.alper.myroadfriend_bag_742.Adapters.NotificationAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.Chat;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;


public class NotificationFragment extends Fragment {

    private ArrayList<User> senderList;
    private ArrayList<String> strSenderImageList;
    private ArrayList<String> dateAndTimeList;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private LinearLayoutManager linearLayoutManager;
    Context context;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        senderList = new ArrayList<>();
        strSenderImageList = new ArrayList<>();
        dateAndTimeList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        context = getContext();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View notificationFragment = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = notificationFragment.findViewById(R.id.recyclerViewNotification);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getNotification();

        return notificationFragment;
    }

    private void getNotification() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderList.clear();
                dateAndTimeList.clear();
                DataSnapshot chatSnapshot = snapshot.child("Chats");
                for (DataSnapshot cds : chatSnapshot.getChildren()) {
                    Chat chat = cds.getValue(Chat.class);

                    if (chat.getReceiverID().equals(firebaseUser.getUid())) {

                        DataSnapshot senderSnapshot = snapshot.child("Users").child(chat.getSenderID());
                        User sender = senderSnapshot.getValue(User.class);
                        senderList.add(sender);

                        if (senderSnapshot.hasChild("image")) {
                            String strImage = senderSnapshot.child("image").getValue().toString();
                            strSenderImageList.add(strImage);
                        } else {
                            strSenderImageList.add(null);
                        }

                        dateAndTimeList.add(chat.getDate() + " " + chat.getTime());
                    }
                }
                notificationAdapter = new NotificationAdapter(senderList, strSenderImageList, dateAndTimeList, context);
                recyclerView.setAdapter(notificationAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}