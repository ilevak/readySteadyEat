package com.example.readysteadyeat.ui.guest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.ui.guest.Orders.OrdersGuestFragment;
import com.example.readysteadyeat.ui.guest.myProfile.ProfileGuestFragment;
import com.example.readysteadyeat.ui.guest.restaurants.RestaurantFragmentList;
import com.example.readysteadyeat.ui.guest.restaurants.RestaurantFragmentList;
import com.example.readysteadyeat.ui.restaurant.orders.OrdersRestaurantFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomMenuGuestActivity extends AppCompatActivity  {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_menu_guest);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Fragment fragment =new OrdersGuestFragment();

        bottomNavigationView.setSelectedItemId(R.id.navigation_orders_guest);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        toolbar=(Toolbar)findViewById(R.id.toolbarBottomGuest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Orders");

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment=null;
            toolbar=(Toolbar)findViewById(R.id.toolbarBottomGuest);
            switch(menuItem.getItemId()){
                case R.id.navigation_profile_guest:
                //getSupportFragmentManager().beginTransac-tion().replace(R.id.container, profileGuestFragment).commit();:
                    selectedFragment=new ProfileGuestFragment();
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("Profile");

                break;

                case R.id.navigation_orders_guest:
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, ordersGuestFragment).commit();
                    selectedFragment=new OrdersGuestFragment();
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("Orders");
                break;

                case R.id.navigation_list_guest:
                //getSupportFragmentManager().beginTransaction().replace(R.id.container, restaurantListViewFragment).commit();
                    selectedFragment=new RestaurantFragmentList();
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("Restaurants");
                break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragment ).commit();
            return true;
        }
    };

}
