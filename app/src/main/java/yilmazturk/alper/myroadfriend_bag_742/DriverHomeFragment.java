package yilmazturk.alper.myroadfriend_bag_742;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;

public class DriverHomeFragment extends Fragment {

    LinearLayout linearLayoutDriverInfo;
    TextView nameSurname;
    Button btnViewProfile;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference database;


    public DriverHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        String userID = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance().getReference();


        showDriverInfo(userID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View driverHomeFragment = inflater.inflate(R.layout.fragment_driver_home, container, false);

        nameSurname=driverHomeFragment.findViewById(R.id.nameSurnameDHome);
        btnViewProfile=driverHomeFragment.findViewById(R.id.driverViewProfile);

        btnViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();

            }
        });

        return driverHomeFragment;
    }

    private void showDriverInfo(String userID) {
        Log.i("Driver", "abc");

        database.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Driver driver = snapshot.getValue(Driver.class);

                String strNameSurname = driver.getName() + " " + driver.getSurname();
                nameSurname.setText(strNameSurname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Home Fragment", "showDriverInfo:onCancelled", error.toException());
            }
        });

    }

    private void showTripInfo() {


    }


}