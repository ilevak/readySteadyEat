package com.example.readysteadyeat.ui.guest.restaurants;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readysteadyeat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {
    TextView restaurantName, restaurantStreet, restaurantHouseNumber;
    CircleImageView profileImage;
    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        restaurantName = itemView.findViewById(R.id.restaurant_name);
        restaurantStreet = itemView.findViewById(R.id.restaurant_street);
        restaurantHouseNumber = itemView.findViewById(R.id.restaurant_house_number);
        profileImage = itemView.findViewById(R.id.restaurant_image);
    }
}

