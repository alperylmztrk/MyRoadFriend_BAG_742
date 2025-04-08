package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;
import yilmazturk.alper.myroadfriend_bag_742.Model.User;
import yilmazturk.alper.myroadfriend_bag_742.R;


public class EditProfileDialogFragment extends DialogFragment {

    private static final String TAG = "EditProfileDialogFragment";

    private CircleImageView proPhotoImgV;
    private TextView changeProPhotoTxt;
    private EditText nameEdtTxt, surnameEdtTxt, usernameEdtTxt, emailEdtTxt;
    private Button btnChangePass, btnCancel, btnSave;
    private FirebaseUser firebaseUser;
    private User user;
    private String userID;
    Context context;
    DatabaseReference database;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private Uri imageUri;
    private String myUri;
    private StorageTask uploadTask;
    private StorageReference storageProfilePickRef;

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

        database = FirebaseDatabase.getInstance().getReference();
        storageProfilePickRef = FirebaseStorage.getInstance().getReference().child("Profile Photo");

        builder.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setUserInfo();

        changeProPhotoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CropImage.activity().setAspectRatio(1, 1).start(getContext(),EditProfileDialogFragment.this);
            //    Intent intent = CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).getIntent(requireContext());
            //    launcher.launch(intent);
            }
        });

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
                uploadProfilePhoto();
                getDialog().dismiss();
            }
        });

        builder.setView(profileEditDialog);

        return builder.create();
    }

    private final ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                //    CropImage.ActivityResult cropResult = CropImage.getActivityResult(result.getData());

              //      imageUri = cropResult.getUri();

                //    proPhotoImgV.setImageURI(imageUri);
                } else {
                    Toast.makeText(getActivity(), "Error, Try again", Toast.LENGTH_SHORT).show();
                }
            }
    );


    private void uploadProfilePhoto() {

        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePickRef.child(userID + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();
                        database.child("image").setValue(myUri);
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserInfo() {
        nameEdtTxt.setText(user.getName());
        surnameEdtTxt.setText(user.getSurname());
        usernameEdtTxt.setText(user.getUsername());
        emailEdtTxt.setText(user.getEmail());

        database.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("image")) {
                    String strImage = snapshot.child("image").getValue().toString();
                    Glide.with(context).load(strImage).into(proPhotoImgV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateProfile() {

        database = database.child("Users").child(userID);
        database.child("name").setValue(nameEdtTxt.getText().toString());
        database.child("surname").setValue(surnameEdtTxt.getText().toString());
        database.child("username").setValue(usernameEdtTxt.getText().toString());

        firebaseUser.updateEmail(emailEdtTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {

            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    database.child("email").setValue(emailEdtTxt.getText().toString());
                    //Toast.makeText(context, "User email address is updated", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "User email address is updated");
                } else {
                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "" + task.getException().toString());
                }
            }
        });
    }

}