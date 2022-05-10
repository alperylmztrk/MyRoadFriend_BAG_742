package yilmazturk.alper.myroadfriend_bag_742;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import yilmazturk.alper.myroadfriend_bag_742.Model.User;

public class MessageActivity extends AppCompatActivity {

   private static User receiver;
   private ImageView userImg;
   private TextView nameSurname,username;

    public static void setReceiver(User receiver1) {
        receiver = receiver1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userImg=findViewById(R.id.proPhotoMsg);
        nameSurname=findViewById(R.id.nameSurnameMsg);
        username=findViewById(R.id.usernameMsg);

        setUserInfo();


    }

    private void setUserInfo(){
        String strNameSurname=receiver.getName()+" "+receiver.getSurname();
        nameSurname.setText(strNameSurname);
        username.setText(receiver.getUsername());
    }

}