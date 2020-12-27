package com.example.readysteadyeat.ui.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.ui.restaurant.menu.MenuRestaurantFragment;
import com.example.readysteadyeat.ui.restaurant.myProfile.ProfileRestarutantFragment;
import com.example.readysteadyeat.ui.restaurant.orders.OrdersRestaurantFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomMenuRestaurantActivity extends AppCompatActivity implements MenuRestaurantFragment.OnFragmentInteractionListener {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment=new OrdersRestaurantFragment();

        setContentView(R.layout.activity_bottom_menu_restaurant);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setSelectedItemId(R.id.navigation_orders_restaurants);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        toolbar=(Toolbar)findViewById(R.id.toolbarBottomRestaurant);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Orders");

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment=null;
                    toolbar=(Toolbar)findViewById(R.id.toolbarBottomRestaurant);
                    switch (menuItem.getItemId()){
                        case R.id.navigation_orders_restaurants:
                            selectedFragment=new OrdersRestaurantFragment();
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setTitle("Orders");
                            break;
                        case R.id.navigation_menu_restaurants:
                            selectedFragment=new MenuRestaurantFragment();
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setTitle("Menu editor");
                            break;
                        case R.id.navigation_profile_restaurant:
                            selectedFragment=new ProfileRestarutantFragment();
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setTitle("Profile");
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
