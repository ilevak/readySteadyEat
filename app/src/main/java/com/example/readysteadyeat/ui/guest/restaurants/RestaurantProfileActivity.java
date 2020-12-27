package com.example.readysteadyeat.ui.guest.restaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.readysteadyeat.R;
import com.squareup.picasso.Picasso;

public class RestaurantProfileActivity extends AppCompatActivity {

    private String restaurantId;
    private String restaurantName;
    private String restaurantStreet;
    private String restaurantHouseNumber;
    private String restaurantCity;
    private String restaurantState;
    private String restaurantEmail;
    private String restaurantImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        restaurantId = getIntent().getExtras().get("restaurant_id").toString();
        restaurantName = getIntent().getExtras().get("restaurant_name").toString();
        restaurantStreet = getIntent().getExtras().get("restaurant_street").toString();
        restaurantHouseNumber = getIntent().getExtras().get("restaurant_houseNumber").toString();
        restaurantCity = getIntent().getExtras().get("restaurant_city").toString();
        restaurantState = getIntent().getExtras().get("restaurant_state").toString();
        restaurantEmail = getIntent().getExtras().get("restaurant_email").toString();
        restaurantImgUrl = getIntent().getExtras().get("restaurant_imgUrl").toString();
        populateItems();
    }

    private void populateItems(){
        ImageView restaurant_imgUrl = findViewById(R.id.profileImageView);
        TextView restaurant_name = findViewById(R.id.restaurantNameTextView);
        TextView restaurant_street = findViewById(R.id.restaurantStreetTextView);
        TextView restaurant_houseNumber = findViewById(R.id.restaurantHouseNumberTextView);
        TextView restaurant_city = findViewById(R.id.restaurantCityTextView);
        TextView restaurant_state = findViewById(R.id.restaurantStateTextView);
        TextView restaurant_email = findViewById(R.id.restaurantEmailTextView);

        restaurant_name.setText(restaurantName);
        restaurant_street.setText(restaurantStreet);
        restaurant_houseNumber.setText(restaurantHouseNumber);
        restaurant_city.setText(restaurantCity);
        restaurant_state.setText(restaurantState);
        restaurant_email.setText(restaurantEmail);
        Picasso.get().load(restaurantImgUrl).placeholder(R.drawable.common_google_signin_btn_icon_dark).into(restaurant_imgUrl);
    }
}
