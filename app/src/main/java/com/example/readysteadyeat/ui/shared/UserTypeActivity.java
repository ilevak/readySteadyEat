package com.example.readysteadyeat.ui.shared;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.readysteadyeat.ui.guest.auth.SignUpActivity;
import com.example.readysteadyeat.R;

public class UserTypeActivity extends AppCompatActivity {
    private Button btnGuestChoice;
    private Button btnRestaurantChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_choice);

        btnGuestChoice = (Button) findViewById(R.id.btnGuestChoice);
        btnRestaurantChoice = (Button) findViewById(R.id.btnRestaurantChoice);

        btnGuestChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openActivitySignUpGuest();
            }
        });

        btnRestaurantChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openActivitySignUpRestaurant();
            }
        });
    }

    public void openActivitySignUpGuest(){
        Intent homeIntetnt = new Intent(UserTypeActivity.this, SignUpActivity.class);
        startActivity(homeIntetnt);
    }

    public void openActivitySignUpRestaurant(){
        Intent homeIntetnt = new Intent(UserTypeActivity.this, com.example.readysteadyeat.ui.restaurant.auth.SignUpActivity.class);
        startActivity(homeIntetnt);
    }
}
