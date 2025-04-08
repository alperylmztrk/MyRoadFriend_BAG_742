package yilmazturk.alper.myroadfriend_bag_742;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    FirebaseUser user;
    SharedPreferences sharedPref;
    Boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseApp.getApps(this).isEmpty()){
            FirebaseApp.initializeApp(this);
        }

        setContentView(R.layout.activity_welcome);

        btnLogin = findViewById(R.id.btnLoginMain);
        btnRegister = findViewById(R.id.btnRegisterMain);

        sharedPref = WelcomeActivity.this.getSharedPreferences("MyRoadFriend", Context.MODE_PRIVATE);
        isAdmin = sharedPref.getBoolean("isAdmin", false);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();
        //check the user is admin
        if (isAdmin) {
            startActivity(new Intent(WelcomeActivity.this, AdminActivity.class));
            finish();
        } else if (user != null) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
    }
}