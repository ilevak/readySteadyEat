package com.example.readysteadyeat.ui.guest.auth;

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
import com.example.readysteadyeat.ui.guest.BottomMenuGuestActivity;
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

    Button SignUpButton;
    static ImageView imgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    //Za registraciju
    private EditText txtVFirstName, txtVLastName, txtVEmail, txtVPassword, txtVRepeatPassword, txtPhoneNumber;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference sotrageRererence;
    private FirebaseDatabase mFirebaseDb;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String urlImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_guest);

        imgUserPhoto = (ImageView) findViewById(R.id.imgvUserPictureGuest);
        //za registraciju
        SignUpButton = (Button) findViewById(R.id.btnSignUp);
        txtVFirstName = (TextInputEditText) findViewById(R.id.txtIFirstName);
        txtVLastName = (TextInputEditText) findViewById(R.id.txtILastName);
        txtVEmail = (TextInputEditText) findViewById(R.id.txtIEmail);
        txtVPassword = (TextInputEditText) findViewById(R.id.txtIPassword);
        txtVRepeatPassword = (TextInputEditText) findViewById(R.id.txtIRepeatPassword);
        txtPhoneNumber = (TextInputEditText) findViewById(R.id.txtIPhoneNumber);

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

                final String firstName = txtVFirstName.getText().toString();
                final String lastName = txtVLastName.getText().toString();
                final String email = txtVEmail.getText().toString();
                final String password = txtVPassword.getText().toString();
                final String repeatPassword = txtVRepeatPassword.getText().toString();
                final String phoneNumber = txtPhoneNumber.getText().toString();
                final String imgUrl;

                if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || phoneNumber.isEmpty()) {
                    showMessage("Please Verify all fields");
                }
                else if(!password.equals(repeatPassword)){
                    showMessage("Passwords don't match");
                }
                else
                {
                    Guest newUser = new Guest(mAuth.getCurrentUser().getUid(), firstName, lastName, email, phoneNumber, true, "");
                    CreateUserAccount(newUser, password);
                }
            }
        });
    }

    private void CreateUserAccount(final Guest newUser, final String password) {
        mAuth.createUserWithEmailAndPassword(newUser.email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            showMessage("Account created");

                            myRef = mFirebaseDb.getInstance().getReference("User").child("Guest");

                            myRef.child(newUser.userId).setValue(newUser);
                            updateUserInfo(newUser, pickedImgUri);
                        }
                        else
                        {
                            showMessage("account creation failed " + task.getException().getMessage());
                        }
                    }
                });
    }


    private void updateUserInfo(final Guest newUser, Uri pickedImgUri) {
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
        Intent homeActivity = new Intent(getApplicationContext(), BottomMenuGuestActivity.class);
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

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null && data.getData() != null){
            pickedImgUri = data.getData();
            Picasso.get().load(pickedImgUri).into(imgUserPhoto);
        }

    }
}
