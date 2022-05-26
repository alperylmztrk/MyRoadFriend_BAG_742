package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import yilmazturk.alper.myroadfriend_bag_742.Model.Admin;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;
import yilmazturk.alper.myroadfriend_bag_742.WelcomeActivity;

public class ProfileFragment extends Fragment {

    CircleImageView proPhoto;
    Button btnEdit, btnLogout;
    TextView nameTxt, surnameTxt, usernameTxt, emailTxt;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference database;
    User user;
    SharedPreferences sharedPref;
    Boolean isAdmin;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        user = new User();
        sharedPref = getActivity().getSharedPreferences("MyRoadFriend", Context.MODE_PRIVATE);
        isAdmin = sharedPref.getBoolean("isAdmin", false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        proPhoto=profileFragment.findViewById(R.id.proImage);
        nameTxt = profileFragment.findViewById(R.id.proName);
        surnameTxt = profileFragment.findViewById(R.id.proSurname);
        usernameTxt = profileFragment.findViewById(R.id.proUsername);
        emailTxt = profileFragment.findViewById(R.id.proEmail);
        btnEdit = profileFragment.findViewById(R.id.btnProEdit);
        btnLogout = profileFragment.findViewById(R.id.btnLogout);

        String userID = firebaseUser.getUid();

        if (isAdmin) {
            RelativeLayout relLayUsername = profileFragment.findViewById(R.id.rltvLayUsernamePro);
            View usernameLine = profileFragment.findViewById(R.id.usernameLinePro);
            relLayUsername.setVisibility(View.GONE);
            usernameLine.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            setAdminProfile();

        } else {

            setUserProfile(userID);
        }

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

    private void setAdminProfile() {

        database.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ads : snapshot.getChildren()) {
                    String adminEmail = ads.child("email").getValue().toString();
                    if (firebaseUser.getEmail().equals(adminEmail)) {

                        //specify and save that the user is an admin
                        Admin admin = ads.getValue(Admin.class);

                        nameTxt.setText(admin.getName());
                        surnameTxt.setText(admin.getSurname());
                        emailTxt.setText(admin.getEmail());

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setUserProfile(String userID) {

        database.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                nameTxt.setText(user.getName());
                surnameTxt.setText(user.getSurname());
                usernameTxt.setText(user.getUsername());
                emailTxt.setText(user.getEmail());
                if (snapshot.hasChild("image")) {
                    String strImage = snapshot.child("image").getValue().toString();
                    Glide.with(getActivity()).load(strImage).into(proPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void logout() {

        auth.signOut();
        if (isAdmin) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isAdmin", false);
            editor.apply();
        }
        startActivity(new Intent(getActivity(), WelcomeActivity.class));
        getActivity().finish();
    }

}