package yilmazturk.alper.myroadfriend_bag_742;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChangePasswordDialogFragment extends DialogFragment {

    private static final String TAG = "ChangePasswordDialogFragment";

    private EditText oldPassEdtTxt, newPassEdtTxt, confirmPasseEdtTxt;
    private Button btnCancel, btnChange;
    private FirebaseUser firebaseUser;
    private String userID;

    public ChangePasswordDialogFragment() {
        // Required empty public constructor
    }

    public ChangePasswordDialogFragment(String userID) {
        this.userID = userID;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Change Password");
        LayoutInflater inflater = getLayoutInflater();
        View changePasswordDialog = inflater.inflate(R.layout.fragment_change_password, null);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        oldPassEdtTxt = changePasswordDialog.findViewById(R.id.editTextTextOldPass);
        newPassEdtTxt = changePasswordDialog.findViewById(R.id.editTextTextNewPass);
        confirmPasseEdtTxt = changePasswordDialog.findViewById(R.id.editTextTextConfirmPass);
        btnCancel = changePasswordDialog.findViewById(R.id.btnCancelChangePass);
        btnChange = changePasswordDialog.findViewById(R.id.btnChangeChangePass);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().cancel();
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPassEdtTxt.getText().toString();
                String newPass = newPassEdtTxt.getText().toString();
                String confirmPass = confirmPasseEdtTxt.getText().toString();
                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                    Toast.makeText(getActivity(), "Fill the all fields...", Toast.LENGTH_LONG).show();
                } else {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), oldPass);
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "User re-authenticated.");
                                        if (newPass.equals(confirmPass)) {
                                            if (newPass.length() >= 6) {
                                                updatePassword(newPass);
                                                getDialog().cancel();
                                            } else {
                                                Toast.makeText(getActivity(), "Password must be at least 6 characters ", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Log.i(TAG, "Old Password is wrong");
                                    }

                                }
                            });
                }
            }
        });

        builder.setView(changePasswordDialog);

        return builder.create();
    }

    private void updatePassword(String newPass) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database = database.child("Users").child(userID);
        database.child("password").setValue(newPass);

        firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User password updated.");
                }
            }
        });

    }
}