package yilmazturk.alper.myroadfriend_bag_742;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yilmazturk.alper.myroadfriend_bag_742.Model.User;

public class ProfileFragment extends Fragment {

    Button btnEdit, btnLogout;
    TextView nameTxt, surnameTxt, usernameTxt, emailTxt;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    User user;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        user = new User();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTxt = profileFragment.findViewById(R.id.proName);
        surnameTxt = profileFragment.findViewById(R.id.proSurname);
        usernameTxt = profileFragment.findViewById(R.id.proUsername);
        emailTxt = profileFragment.findViewById(R.id.proEmail);
        btnEdit = profileFragment.findViewById(R.id.btnProEdit);
        btnLogout = profileFragment.findViewById(R.id.btnLogout);

        String userID = firebaseUser.getUid();
        setUserProfile(userID);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileDialogFragment editProfileDialogFragment = new EditProfileDialogFragment(user, userID);
                editProfileDialogFragment.show(getActivity().getSupportFragmentManager(), "EditProfile");
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        return profileFragment;
    }

    private void setUserProfile(String userID) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                nameTxt.setText(user.getName());
                surnameTxt.setText(user.getSurname());
                usernameTxt.setText(user.getUsername());
                emailTxt.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void logout() {

        auth.signOut();
        startActivity(new Intent(getActivity(), WelcomeActivity.class));
        getActivity().finish();
    }

}