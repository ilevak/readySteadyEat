package com.example.readysteadyeat.ui.guest.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.data.models.Restaurant;
import com.example.readysteadyeat.ui.guest.BottomMenuGuestActivity;
import com.example.readysteadyeat.ui.restaurant.BottomMenuRestaurantActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button btnLogIn;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        txtEmail = findViewById(R.id.txtUserEmail);
        txtPassword = findViewById(R.id.txtUserPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        mAuth = FirebaseAuth.getInstance();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    showMessage("Please Verify All Field");
                }
                else
                {
                    signIn(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
         mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()) {
                     createUserSession();
                     final String logedInUser = mAuth.getCurrentUser().getUid();

                     DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("User").child("Restaurant");
                     rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                 Restaurant restaurant = snapshot.getValue(Restaurant.class);
                                 if(restaurant.userId.equals(logedInUser)) {
                                    updateUI(1);
                                    return;
                                 }
                                 else {
                                     updateUI(0);
                                 }
                             }
                         }
                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });
                 }
                 else
                 {
                     showMessage(task.getException().getMessage());
                 }
             }
         });
    }

    private void createUserSession() {
        FirebaseUser user  = mAuth.getCurrentUser();
    }



    private void updateUI(int Number) {
        //dohvatiti prema id type usera
        if (Number == 0){
            Intent homeActivity = new Intent(getApplicationContext(), BottomMenuGuestActivity.class);
            startActivity(homeActivity);
            finish();
        }
        else
        {
            Intent homeActivity = new Intent(getApplicationContext(), BottomMenuRestaurantActivity.class);
            startActivity(homeActivity);
            finish();
        }

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user  = mAuth.getCurrentUser();

        if(user != null) {
            //updateUI();
        }
    }
}
