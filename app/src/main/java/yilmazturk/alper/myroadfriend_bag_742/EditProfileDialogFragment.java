package yilmazturk.alper.myroadfriend_bag_742;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import yilmazturk.alper.myroadfriend_bag_742.Model.User;


public class EditProfileDialogFragment extends DialogFragment {

    private static final String TAG = "EditProfileDialogFragment";

    private ImageView proPhotoImgV;
    private TextView changeProPhotoTxt;
    private EditText nameEdtTxt, surnameEdtTxt, usernameEdtTxt, emailEdtTxt;
    private Button btnChangePass, btnCancel, btnSave;


    private User user;
    private String userID;


    public EditProfileDialogFragment() {
        // Required empty public constructor
    }

    public EditProfileDialogFragment(User user, String userID) {
        this.user = user;
        this.userID = userID;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");
        LayoutInflater inflater = getLayoutInflater();
        View profileEditDialog = inflater.inflate(R.layout.fragment_edit_profile, null);


        proPhotoImgV = profileEditDialog.findViewById(R.id.imageViewUser);
        changeProPhotoTxt = profileEditDialog.findViewById(R.id.textViewChangePP);
        nameEdtTxt = profileEditDialog.findViewById(R.id.editTextTextNameEditPro);
        surnameEdtTxt = profileEditDialog.findViewById(R.id.editTextTextSurnameEditPro);
        usernameEdtTxt = profileEditDialog.findViewById(R.id.editTextTextUsernameEditPro);
        emailEdtTxt = profileEditDialog.findViewById(R.id.editTextTextEmailEditPro);
        btnChangePass = profileEditDialog.findViewById(R.id.btnProChangePass);
        btnCancel = profileEditDialog.findViewById(R.id.btnCancelEditPro);
        btnSave = profileEditDialog.findViewById(R.id.btnSaveEditPro);

        builder.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setTextOfEditTexts();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().cancel();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                getDialog().cancel();
                Log.i("btnsave", "");
            }
        });


        builder.setView(profileEditDialog);

        return builder.create();

    }

    private void setTextOfEditTexts() {

        nameEdtTxt.setText(user.getName());
        surnameEdtTxt.setText(user.getSurname());
        usernameEdtTxt.setText(user.getUsername());
        emailEdtTxt.setText(user.getEmail());
    }

    private void updateProfile() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database = database.child("Users").child(userID);
        database.child("name").setValue(nameEdtTxt.getText().toString());
        database.child("surname").setValue(surnameEdtTxt.getText().toString());
        database.child("username").setValue(usernameEdtTxt.getText().toString());
        database.child("email").setValue(emailEdtTxt.getText().toString());

    }


}