package com.example.readysteadyeat.ui.restaurant.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.data.models.Guest;
import com.example.readysteadyeat.data.models.Restaurant;
import com.example.readysteadyeat.ui.restaurant.BottomMenuRestaurantActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SignUpActivity extends AppCompatActivity {

    ImageView imgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;

    private EditText txtRestaurantName, txtEmail, txtRestaurantState, txtRestaturantCity, txtRestaurantStreet, txtRestaturantHouseNumber, txtIBAN, txtPassword, txtRepeatPassword;
    Button SignUpButton;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference sotrageRererence;
    private FirebaseDatabase mFirebaseDb;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String urlImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_rest);

        imgUserPhoto = findViewById(R.id.imgvUserProfile);
        SignUpButton = (Button) findViewById(R.id.btnSignUnRest);

        txtRestaurantName = (TextInputEditText) findViewById(R.id.txtRestaurantName);
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtRestaurantState = (TextInputEditText) findViewById(R.id.txtRestaurantState);
        txtRestaturantCity = (TextInputEditText) findViewById(R.id.txtRestaturantCity);
        txtRestaurantStreet = (TextInputEditText) findViewById(R.id.txtRestaurantStreet);
        txtRestaturantHouseNumber = (TextInputEditText) findViewById(R.id.txtRestaturantHouseNumber);
        txtIBAN = (TextInputEditText) findViewById(R.id.txtIBAN);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);
        txtRepeatPassword = (TextInputEditText) findViewById(R.id.txtRepeatPassword);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
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

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = txtRestaurantName.getText().toString();
                final String email = txtEmail.getText().toString();
                final String state = txtRestaurantState.getText().toString();
                final String city = txtRestaturantCity.getText().toString();
                final String street = txtRestaurantStreet.getText().toString();
                final String houseNumber = txtRestaturantHouseNumber.getText().toString();
                final String iban = txtIBAN.getText().toString();
                final String password = txtPassword.getText().toString();
                final String repeatPassword = txtRepeatPassword.getText().toString();

                final String imgUrl;

                if(name.isEmpty() || email.isEmpty() || state.isEmpty() || city.isEmpty() || street.isEmpty() || houseNumber.isEmpty() || iban.isEmpty()
                        || password.isEmpty() || repeatPassword.isEmpty()) {
                    showMessage("Please Verify all fields");
                }
                else if(!password.equals(repeatPassword)){
                    showMessage("Passwords don't match");
                }
                else
                {
                    CreateUserAccount(email, password);
                    Restaurant newUser = new Restaurant(mAuth.getCurrentUser().getUid(), name, email, state, city, street, houseNumber, iban, false, "");

                    myRef = mFirebaseDb.getInstance().getReference("User").child("Restaurant");
                    myRef.child(newUser.userId).setValue(newUser);

                    updateUserInfo(newUser, pickedImgUri);
                }
            }
        });
    }

    private void CreateUserAccount(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            showMessage("Account created");
                        }
                        else
                        {
                            showMessage("account creation failed " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateUserInfo(final Restaurant newUser, Uri pickedImgUri) {
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
                        myRef.child(newUser.userId).child("imgUrl").setValue(imageReference);

                        mAuth.getCurrentUser().updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            showMessage("Register Complete");
                                            updateUI();

                                        }
                                    }
                                });
                    }
                });
            }
        });

    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), BottomMenuRestaurantActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void checkAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(SignUpActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            pickedImgUri = data.getData();
            Picasso.get().load(pickedImgUri).into(imgUserPhoto);
        }
    }

}