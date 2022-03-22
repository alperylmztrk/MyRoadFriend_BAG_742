package yilmazturk.alper.myroadfriend_bag_742;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Passenger;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    EditText editTextName, editTextSurname, editTextUsername, editTextEmail, editTextPassword;
    RadioGroup radioGroup;
    RadioButton radioBtn;
    Button btnSignUp;
    TextView textViewGoLogin;
    FirebaseAuth auth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.editTextTextNameRegister);
        editTextSurname = findViewById(R.id.editTextTextSurnameRegister);
        editTextUsername = findViewById(R.id.editTextTextUsernameRegister);
        editTextEmail = findViewById(R.id.editTextTextEmailAddressRegister);
        editTextPassword = findViewById(R.id.editTextTextPasswordRegister);
        btnSignUp = findViewById(R.id.btnSignUp);
        textViewGoLogin = findViewById(R.id.textViewGoLogin);
        radioGroup = findViewById(R.id.radioGroupRegister);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String strName = editTextName.getText().toString();
                String strSurname = editTextSurname.getText().toString();
                String strUsername = editTextUsername.getText().toString();
                String strEmail = editTextEmail.getText().toString();
                String strPassword = editTextPassword.getText().toString();
                int selectedRadioBtnID = radioGroup.getCheckedRadioButtonId();
                radioBtn = findViewById(selectedRadioBtnID);
                String strUserType = radioBtn.getText().toString();

                createAccount(strName, strSurname, strUsername, strEmail, strPassword, strUserType);

            }
        });


        textViewGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void createAccount(String name, String surname, String username, String email, String password, String userType) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailAndPassword:success");
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            database = FirebaseDatabase.getInstance().getReference();
                            Driver driver=new Driver(name, surname, username, email, password, userType);
                            Passenger passenger=new Passenger(name, surname, username, email, password, userType);
                            if (userType.equals("Driver")){
                                database.child("Users").child(userID).setValue(driver);
                            }else{
                                database.child("Users").child(userID).setValue(passenger);
                            }
                            //new Intent(RegisterActivity.this,PostTripActivity.class).putExtra("driver", (Serializable) driver);

                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                        } else {
                            Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}