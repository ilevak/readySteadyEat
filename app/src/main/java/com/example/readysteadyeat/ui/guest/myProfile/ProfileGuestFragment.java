package com.example.readysteadyeat.ui.guest.myProfile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.data.models.Guest;
import com.example.readysteadyeat.ui.shared.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileGuestFragment extends Fragment {

    private ImageView imgUserProfile;
    private TextInputEditText txtIFirstName, txtILastName,
            txtIEmail, txtIPhoneNumber;
    private MaterialButton btnEditProfileGuest, btnResetPasswordGuest, btnLogOutGuest;

    boolean click = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage storage;

    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;

    public ProfileGuestFragment() { }

    public static ProfileGuestFragment newInstance(String param1, String param2) {
        ProfileGuestFragment fragment = new ProfileGuestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_guest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtIFirstName = view.findViewById(R.id.txtIFirstName);
        txtILastName = view.findViewById(R.id.txtILastName);
        txtIEmail = view.findViewById(R.id.txtIEmail);
        txtIPhoneNumber = view.findViewById(R.id.txtIPhoneNumber);
        btnEditProfileGuest = view.findViewById(R.id.btnEditProfileGuest);
        btnResetPasswordGuest = view.findViewById(R.id.btnResetPassword);
        btnLogOutGuest = view.findViewById(R.id.btnLogOutGuest);
        imgUserProfile = (ImageView) view.findViewById(R.id.imgvUserProfileGuest);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        DatabaseReference databseReference = firebaseDatabase.getReference("User").child("Guest").child(firebaseAuth.getCurrentUser().getUid());

        btnEditProfileGuest.setText("Edit profile");
        txtIFirstName.setEnabled(false);
        txtILastName.setEnabled(false);
        txtIEmail.setEnabled(false);
        txtIPhoneNumber.setEnabled(false);
        imgUserProfile.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();


        btnEditProfileGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!click) {
                    btnEditProfileGuest.setText("Save changes");
                    txtIFirstName.setEnabled(true);
                    txtILastName.setEnabled(true);
                    txtIEmail.setEnabled(true);
                    txtIPhoneNumber.setEnabled(true);
                    imgUserProfile.setEnabled(true);
                    click = true;
                } else if (click) {
                    btnEditProfileGuest.setText("Edit profile");
                    txtIFirstName.setEnabled(false);
                    txtILastName.setEnabled(false);
                    txtIEmail.setEnabled(false);
                    txtIPhoneNumber.setEnabled(false);
                    imgUserProfile.setEnabled(false);
                    click = false;


                    final String firstName = txtIFirstName.getText().toString();
                    final String lastName = txtILastName.getText().toString();
                    final String email = txtIEmail.getText().toString();
                    final String phone = txtIPhoneNumber.getText().toString();
                    //pozvati metodu za updateProfila i prosljediti joj ovo sve gore
                    updateUserInfo(firstName, lastName, email, phone, pickedImgUri);
                }
            }
        });

        databseReference.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Guest guestInfo = dataSnapshot.getValue(Guest.class);
                txtIFirstName.setText(guestInfo.getFirstNsme());
                txtILastName.setText(guestInfo.getLastNsme());
                txtIEmail.setText(guestInfo.getEmail());
                txtIPhoneNumber.setText(guestInfo.getPhone());
                Picasso.get().load(guestInfo.getImgUrl()).into(imgUserProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Activity activity = getActivity();
                Toast.makeText(activity, databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        }));

        imgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });

        btnResetPasswordGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Activity activity = getActivity();
                        if(task.isSuccessful()){
                            Toast.makeText(activity, "Password send to your email", Toast.LENGTH_LONG).show();
                            updateUI();
                        }else{
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        btnLogOutGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSignOut();
                updateUI();
            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getActivity(), StartActivity.class);
        startActivity(homeActivity);
    }

    private void updateUserInfo(final String firstName, final String lastName, final String email, final String phone, final Uri pickedImgUri) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child(pickedImgUri.getLastPathSegment());
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                //.setDisplayName(newUser.firstNsme)
                                .setPhotoUri(uri)
                                .build();

                        String imageReference = uri.toString();
                        myRef = firebaseDatabase.getInstance().getReference("User").child("Guest");
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("firstName").setValue(firstName);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("lastName").setValue(lastName);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("email").setValue(email);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("phone").setValue(phone);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("imgUrl").setValue(imageReference);

                        firebaseAuth.getCurrentUser().updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            showMessage("Register Complete");
                                        }
                                    }
                                });
                    }
                });
            }
        });

    }

    private void showMessage(String text) {
        Activity activity = getActivity();
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    private void checkAndRequestPermission() {
        Activity activity = getActivity();
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        }
        else
        {
            openGallery();
        }
    }

    private void openGallery(){
        Intent galleryItent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryItent.setType("image/*");
        startActivityForResult(galleryItent, REQUESCODE);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null && data.getData() != null){
            pickedImgUri = data.getData();
            Picasso.get().load(pickedImgUri).into(imgUserProfile);
        }
    }

    //ODJAVA

    private void userSignOut(){
        FirebaseAuth.getInstance().signOut();
    }

}
