package yilmazturk.alper.myroadfriend_bag_742;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;

public class HomeFragment extends Fragment {

    TextView nameSurname, uni, days, checkIn, checkOut;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference database;
    Driver driver;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        firebaseUser = auth.getCurrentUser();
        String userID = firebaseUser.getUid();
        driver=new Driver();
        Log.d("firebase", "oncre");

        setDriverInfo(userID);

        Log.i("Driver", ""+driver.getUsername());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View homeFragment = inflater.inflate(R.layout.fragment_home, container, false);

        nameSurname = homeFragment.findViewById(R.id.nameSurname);
        uni = homeFragment.findViewById(R.id.university);
        days = homeFragment.findViewById(R.id.days);
        checkIn = homeFragment.findViewById(R.id.checkIn);
        checkOut = homeFragment.findViewById(R.id.checkOut);



        Log.d("firebase", "oncreVİEW");



        return homeFragment;

    }

    private void setDriverInfo(String userID){
        Log.i("Driver", "abc");
        database.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driver=snapshot.getValue(Driver.class);

                String strNameSurname = driver.getName() + " " + driver.getSurname();
                nameSurname.setText(strNameSurname);
                Log.d("firebase", strNameSurname+" "+driver.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Home Fragment", "loadPost:onCancelled", error.toException());
            }
        });
        Log.i("Driver", "abc"+driver.getUsername());
    }


}