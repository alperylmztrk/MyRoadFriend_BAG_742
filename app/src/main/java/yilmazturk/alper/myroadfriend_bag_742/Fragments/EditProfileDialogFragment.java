package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;


public class EditProfileDialogFragment extends DialogFragment {

    private static final String TAG = "EditProfileDialogFragment";

    private ImageView proPhotoImgV;
    private TextView changeProPhotoTxt;
    private EditText nameEdtTxt, surnameEdtTxt, usernameEdtTxt, emailEdtTxt;
    private Button btnChangePass, btnCancel, btnSave;
    private FirebaseUser firebaseUser;
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        proPhotoImgV = profileEditDialog.findViewById(R.id.imageViewUserEditPro);
        changeProPhotoTxt = profileEditDialog.findViewById(R.id.textViewChangePP);
        nameEdtTxt = profileEditDialog.findViewById(R.id.editTextTextNameEditPro);
        surnameEdtTxt = profileEditDialog.findViewById(R.id.editTextTextSurnameEditPro);
        usernameEdtTxt = profileEditDialog.findViewById(R.id.editTextTextUsernameEditPro);
        emailEdtTxt = profileEditDialog.findViewById(R.id.editTextTextEmailEditPro);
        btnChangePass = profileEditDialog.findViewById(R.id.btnChangePassEditPro);
        btnCancel = profileEditDialog.findViewById(R.id.btnCancelEditPro);
        btnSave = profileEditDialog.findViewById(R.id.btnSaveEditPro);

        builder.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setTextOfEditTexts();

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialogFragment changePasswordDialogFragment = new ChangePasswordDialogFragment(userID);
                changePasswordDialogFragment.show(getActivity().getSupportFragmentManager(), "ChangePassword");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                getDialog().dismiss();
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


        DatabaseReference finalDatabase = database;
        firebaseUser.updateEmail(emailEdtTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {

            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finalDatabase.child("email").setValue(emailEdtTxt.getText().toString());
                    Log.i(TAG, "User email address is updated");
                } else {
                    Log.e(TAG, "" + task.getException().toString());
                }
            }
        });

    }

}