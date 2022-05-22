package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    TextView textViewLogin, textViewGoRegister;
    FirebaseAuth authLogin;
    Boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authLogin = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextTextEmailAddressLogin);
        editTextPassword = findViewById(R.id.editTextTextPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        textViewLogin = findViewById(R.id.txtLogin);
        textViewGoRegister = findViewById(R.id.textViewGoRegister);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strEmail = editTextEmail.getText().toString();
                String strPassword = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                    Toast.makeText(LoginActivity.this, "Fill the all fields...", Toast.LENGTH_LONG).show();
                } else {
                    login(strEmail, strPassword);
                }
            }
        });

        textViewGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    private void login(String email, String password) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        authLogin.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = authLogin.getCurrentUser();
                            Log.i("LoginTask", "Login Successful");

                            database.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ads : snapshot.getChildren()) {
                                        String adminEmail = ads.child("email").getValue().toString();
                                        if (firebaseUser.getEmail().equals(adminEmail)) {
                                            isAdmin = true;
                                            //specify and save that the user is an admin
                                            SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("MyRoadFriend", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putBoolean("isAdmin", isAdmin);
                                            editor.apply();

                                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                            finish();
                                            break;
                                        }
                                    }
                                    // The user is not an admin
                                    if (!isAdmin) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}