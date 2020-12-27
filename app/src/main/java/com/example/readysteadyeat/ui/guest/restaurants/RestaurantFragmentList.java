package com.example.readysteadyeat.ui.guest.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.data.models.Restaurant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantFragmentList extends Fragment {

    private View RestaurantsView;
    private RecyclerView myRestaurantsList;
    private DatabaseReference RestaurantsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private SearchView searchRestaurants;


    public RestaurantFragmentList(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RestaurantsView= inflater.inflate(R.layout.fragment_restaurant_list_view, container, false);
        searchRestaurants = (SearchView) RestaurantsView.findViewById(R.id.searchRestaurants);


        myRestaurantsList = (RecyclerView) RestaurantsView.findViewById(R.id.restaurants_list);
        myRestaurantsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        //currentUserID = mAuth.getCurrentUser().getUid();
        RestaurantsRef = FirebaseDatabase.getInstance().getReference().child("User").child("Restaurant");

        return RestaurantsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        searchRestaurants.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateRecycleView(s);
                return true;
            }
        });

        searchRestaurants.setQuery("", true);
    }

    public void populateRecycleView(final String s){
        FirebaseRecyclerOptions<Restaurant> options = new FirebaseRecyclerOptions.Builder<Restaurant>().setQuery(RestaurantsRef, Restaurant.class).build();

        FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RestaurantViewHolder holder, int position, @NonNull Restaurant model) {
                final String IDs = getRef(position).getKey();
                RestaurantsRef.child(IDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(s.equals("")){
                            if(dataSnapshot.hasChild("imgUrl")){
                                String profileImage = dataSnapshot.child("imgUrl").getValue().toString();
                                String restaurantName = dataSnapshot.child("name").getValue().toString();
                                String restaurantStreet = dataSnapshot.child("street").getValue().toString();
                                String restaurantHouseNumber = dataSnapshot.child("houseNumber").getValue().toString();

                                holder.restaurantName.setText(restaurantName);
                                holder.restaurantStreet.setText(restaurantStreet);
                                holder.restaurantHouseNumber.setText(restaurantHouseNumber);
                                Picasso.get().load(profileImage).placeholder(R.drawable.common_google_signin_btn_icon_dark).into(holder.profileImage);

                            }
                            else{
                                String restaurantName = dataSnapshot.child("name").getValue().toString();
                                String restaurantStreet = dataSnapshot.child("street").getValue().toString();
                                String restaurantHouseNumber = dataSnapshot.child("houseNumber").getValue().toString();

                                holder.restaurantName.setText(restaurantName);
                                holder.restaurantStreet.setText(restaurantStreet);
                                holder.restaurantHouseNumber.setText(restaurantHouseNumber);
                            }
                        }
                        else{
                            if(dataSnapshot.child("name").getValue().toString().contains(s)){
                                if(dataSnapshot.hasChild("imgUrl")){
                                    String profileImage = dataSnapshot.child("imgUrl").getValue().toString();
                                    String restaurantName = dataSnapshot.child("name").getValue().toString();
                                    String restaurantStreet = dataSnapshot.child("street").getValue().toString();
                                    String restaurantHouseNumber = dataSnapshot.child("houseNumber").getValue().toString();

                                    holder.restaurantName.setText(restaurantName);
                                    holder.restaurantStreet.setText(restaurantStreet);
                                    holder.restaurantHouseNumber.setText(restaurantHouseNumber);
                                    Picasso.get().load(profileImage).placeholder(R.drawable.common_google_signin_btn_icon_dark).into(holder.profileImage);

                                }
                                else{
                                    String restaurantName = dataSnapshot.child("name").getValue().toString();
                                    String restaurantStreet = dataSnapshot.child("street").getValue().toString();
                                    String restaurantHouseNumber = dataSnapshot.child("houseNumber").getValue().toString();

                                    holder.restaurantName.setText(restaurantName);
                                    holder.restaurantStreet.setText(restaurantStreet);
                                    holder.restaurantHouseNumber.setText(restaurantHouseNumber);
                                }
                            }
                            else{
                                //final RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(0,0);
                                //holder.itemView.setLayoutParams(params);
                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                params.height = 0;
                                holder.itemView.setLayoutParams(params);
                                holder.itemView.setVisibility(View.GONE);
                            }
                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profileIntent = new Intent(getContext(), RestaurantProfileActivity.class);
                                profileIntent.putExtra("restaurant_id", IDs);
                                profileIntent.putExtra("restaurant_name", dataSnapshot.child("name").getValue().toString());
                                profileIntent.putExtra("restaurant_street", dataSnapshot.child("street").getValue().toString());
                                profileIntent.putExtra("restaurant_houseNumber", dataSnapshot.child("houseNumber").getValue().toString());
                                profileIntent.putExtra("restaurant_city", dataSnapshot.child("city").getValue().toString());
                                profileIntent.putExtra("restaurant_state", dataSnapshot.child("state").getValue().toString());
                                profileIntent.putExtra("restaurant_email", dataSnapshot.child("email").getValue().toString());
                                if(dataSnapshot.hasChild("imgUrl")){
                                    profileIntent.putExtra("restaurant_imgUrl", dataSnapshot.child("imgUrl").getValue().toString());
                                }
                                else{
                                    profileIntent.putExtra("restaurant_imgUrl", "");
                                }

                                startActivity(profileIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }

            @NonNull
            @Override
            public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_display_layout, parent, false);
                RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
                return viewHolder;
            }

        };
        myRestaurantsList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        myRestaurantsList.setAdapter(adapter);


        adapter.startListening();

    }
}

