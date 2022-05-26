package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import yilmazturk.alper.myroadfriend_bag_742.Adapters.ChatAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.Chat;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;

public class ChatActivity extends AppCompatActivity {

    private static User receiver;

    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    List<Chat> chatList;
    private ImageView btnBack;
    private CircleImageView userImage;
    private TextView nameSurname, username;
    private EditText messageEditTxt;
    private ImageButton btnSend;
    private String receiverID;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference database;
    private LinearLayoutManager linearLayoutManager;
    ValueEventListener seenListener;

    public static void setReceiver(User receiver1) {
        receiver = receiver1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnBack = findViewById(R.id.backBtn);
        userImage = findViewById(R.id.proPhotoChat);

        nameSurname = findViewById(R.id.nameSurnameChat);
        username = findViewById(R.id.usernameChat);
        messageEditTxt = findViewById(R.id.editTextChatMsg);
        btnSend = findViewById(R.id.btnSendMsg);


        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();


        if (receiver != null) {
            setReceiverInfo();
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = messageEditTxt.getText().toString();

                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), receiverID, msg);
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }


                messageEditTxt.setText("");

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setReceiverInfo() {

        String strNameSurname = receiver.getName() + " " + receiver.getSurname();
        nameSurname.setText(strNameSurname);
        username.setText(receiver.getUsername());

        database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uds : snapshot.getChildren()) {
                    User user = uds.getValue(User.class);
                    if (user.getEmail().equals(receiver.getEmail())) {
                        receiverID = uds.getKey();
                        if (uds.hasChild("image")) {
                            String strImage = uds.child("image").getValue().toString();
                            Glide.with(ChatActivity.this).load(strImage).into(userImage);
                        }
                        break;
                    }
                }

                getMessages(firebaseUser.getUid(), receiverID);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage();
    }

    private void seenMessage() {
        seenListener = database.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot cds : snapshot.getChildren()) {
                    Chat chat = cds.getValue(Chat.class);
                    if (chat.getReceiverID().equals(firebaseUser.getUid()) && chat.getSenderID().equals(receiverID)) {
                        chat.setSeen(true);
                        cds.getRef().setValue(chat);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String senderID, String receiverID, String message) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();

        database.child("Chats").push().setValue(new Chat(senderID, receiverID, message, simpleDateFormat.format(date), simpleTimeFormat.format(date), false));

    }

    private void getMessages(String senderID, String receiverID) {
        chatList = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference("Chats");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot cds : snapshot.getChildren()) {
                    Chat chat = cds.getValue(Chat.class);
                    if (chat.getReceiverID().equals(senderID) && chat.getSenderID().equals(receiverID) ||
                            chat.getReceiverID().equals(receiverID) && chat.getSenderID().equals(senderID)) {

                        chatList.add(chat);

                    }
                    chatAdapter = new ChatAdapter(chatList);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.removeEventListener(seenListener);
    }

}