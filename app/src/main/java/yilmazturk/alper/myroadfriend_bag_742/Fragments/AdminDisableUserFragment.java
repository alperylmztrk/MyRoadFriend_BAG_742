package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import yilmazturk.alper.myroadfriend_bag_742.Adapters.AdminDisabledUserAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Adapters.AdminHomeUserAdapter;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;


public class AdminDisableUserFragment extends Fragment {

    private RecyclerView recyclerViewDisUser;
    private AdminDisabledUserAdapter adminDisabledUserAdapter;
    private ArrayList<User> disUserList;
    private ArrayList<String> disUserIDList;
    private static Context context;
    static DatabaseReference database;
    private LinearLayoutManager linearLayoutManager;

    public AdminDisableUserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        database = FirebaseDatabase.getInstance().getReference();
        disUserList = new ArrayList<>();
        disUserIDList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View adminDisabledUserFragment = inflater.inflate(R.layout.fragment_admin_disable_user, container, false);

        recyclerViewDisUser = adminDisabledUserFragment.findViewById(R.id.recyclerViewAdminDisabledUsers);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewDisUser.setLayoutManager(linearLayoutManager);

        showDisabledUsers();

        return adminDisabledUserFragment;
    }

    private void showDisabledUsers() {
        database.child("Disabled Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uds : snapshot.getChildren()) {

                    User disUser = uds.getValue(User.class);
                    disUserIDList.add(uds.getKey());
                    disUserList.add(disUser);

                }
                adminDisabledUserAdapter = new AdminDisabledUserAdapter(disUserIDList, disUserList);
                recyclerViewDisUser.setAdapter(adminDisabledUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void enableUser(String enableUserID) {

        database.child("Disabled Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uds : snapshot.getChildren()) {

                    if (uds.getKey().equals(enableUserID)) {
                        User user = uds.getValue(User.class);
                        database.child("Users").child(uds.getKey()).setValue(user);
                        uds.getRef().removeValue();
                        Toast.makeText(context, "User " + user.getName() + " is enabled", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}