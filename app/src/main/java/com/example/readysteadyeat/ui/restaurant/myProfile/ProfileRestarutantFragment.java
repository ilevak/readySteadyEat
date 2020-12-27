package com.example.readysteadyeat.ui.restaurant.myProfile;

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
import com.example.readysteadyeat.data.models.Restaurant;
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

public class ProfileRestarutantFragment extends Fragment {

    private ImageView imgvUserProfile;
    private TextInputEditText txtRestaurantName, txtCity, txtAdress,
            txtState, txtHouseNumber, txtIBAN,
            txtEmail;

    private MaterialButton btnEditProfile, btnResetPasswordRest, btnLogOutRest;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage storage;

    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;

    private OnFragmentInteractionListener mListener;
    public boolean click = false;
    public ProfileRestarutantFragment() {
    }

    public static ProfileRestarutantFragment newInstance(String param1, String param2) {
        ProfileRestarutantFragment fragment = new ProfileRestarutantFragment();
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
        View view= inflater.inflate(R.layout.fragment_profile_restarutant, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtAdress = view.findViewById(R.id.txtRestaurantStreet);
        txtCity = view.findViewById(R.id.txtRestaturantCity);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtHouseNumber = view.findViewById(R.id.txtRestaturantHouseNumber);
        txtIBAN = view.findViewById(R.id.txtIBAN);
        txtRestaurantName = view.findViewById(R.id.txtRestaurantName);
        txtState = view.findViewById(R.id.txtRestaurantState);
        btnEditProfile = (MaterialButton) view.findViewById(R.id.btnEditProfileRestaurant);
        btnResetPasswordRest = view.findViewById(R.id.btnResetPassword);
        btnLogOutRest = view.findViewById(R.id.btnLogOutRest);
        imgvUserProfile = (ImageView) view.findViewById(R.id.imgvUserProfile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        DatabaseReference databseReference = firebaseDatabase.getReference("User").child("Restaurant").child(firebaseAuth.getCurrentUser().getUid());

        btnEditProfile.setText("Edit profile");
        txtAdress.setEnabled(false);
        txtCity.setEnabled(false);
        txtEmail.setEnabled(false);
        txtHouseNumber.setEnabled(false);
        txtIBAN.setEnabled(false);
        txtRestaurantName.setEnabled(false);
        imgvUserProfile.setEnabled(false);
        txtState.setEnabled(false);

        btnEditProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!click) {
                    btnEditProfile.setText("Save changes");
                    txtAdress.setEnabled(true);
                    txtCity.setEnabled(true);
                    txtEmail.setEnabled(true);
                    txtHouseNumber.setEnabled(true);
                    txtIBAN.setEnabled(true);
                    txtRestaurantName.setEnabled(true);
                    imgvUserProfile.setFocusable(true);
                    txtState.setEnabled(true);
                    imgvUserProfile.setEnabled(true);
                    click=true;
                }
                else if(click)
                {
                    btnEditProfile.setText("Edit profile");
                    txtRestaurantName.setEnabled(false);
                    txtEmail.setEnabled(false);
                    txtState.setEnabled(false);
                    txtCity.setEnabled(false);
                    txtAdress.setEnabled(false);
                    txtHouseNumber.setEnabled(false);
                    txtIBAN.setEnabled(false);
                    imgvUserProfile.setEnabled(false);
                    click=false;

                    final String restName = txtRestaurantName.getText().toString();
                    final String email = txtEmail.getText().toString();
                    final String state = txtState.getText().toString();
                    final String city = txtCity.getText().toString();
                    final String street = txtAdress.getText().toString();
                    final String houseNumber = txtHouseNumber.getText().toString();
                    final String iban = txtIBAN.getText().toString();

                    updateUserInfo(restName, email, state, city, street, houseNumber, iban, pickedImgUri);
                }
            }
        });

        databseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Restaurant restaurantInfo = dataSnapshot.getValue(Restaurant.class);
                txtRestaurantName.setText(restaurantInfo.getName());
                txtEmail.setText(restaurantInfo.getEmail());
                txtState.setText(restaurantInfo.getEmail());
                txtCity.setText(restaurantInfo.getCity());
                txtAdress.setText(restaurantInfo.getStreet());
                txtHouseNumber.setText(restaurantInfo.getHouseNumber());
                txtIBAN.setText(restaurantInfo.getIban());
                Picasso.get().load(restaurantInfo.getImgUrl()).into(imgvUserProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Activity activity = getActivity();
                Toast.makeText(activity, databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });

        imgvUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestPermission();
                }
                else
                {
                    openGallery();
                    openGallery();
                }
            }
        });

        btnResetPasswordRest.setOnClickListener(new View.OnClickListener() {
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

        btnLogOutRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSignOut();
                updateUI();
            }
        });
    }

    private void userSignOut(){
        FirebaseAuth.getInstance().signOut();
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getActivity(), StartActivity.class);
        startActivity(homeActivity);
    }


    private void updateUserInfo(final String restName, final String email, final String state, final String city, final String street, final String houseNumber, final String iban, Uri pickedImgUri) {

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
                        myRef = firebaseDatabase.getInstance().getReference("User").child("Restaurant");

                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("name").setValue(restName);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("email").setValue(email);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("state").setValue(state);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("city").setValue(city);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("street").setValue(street);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("houseNumber").setValue(houseNumber);
                        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("iban").setValue(iban);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null && data.getData() != null){
            pickedImgUri = data.getData();
            Picasso.get().load(pickedImgUri).into(imgvUserProfile);
        }
    }
}


