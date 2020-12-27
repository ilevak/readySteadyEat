package com.example.readysteadyeat.ui.restaurant.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.data.models.Category;
import com.example.readysteadyeat.data.models.Dish;
import com.example.readysteadyeat.data.models.Guest;
import com.example.readysteadyeat.data.models.Restaurant;
import com.example.readysteadyeat.data.models.restaurant.DishMenuListRestaurant;
import com.example.readysteadyeat.ui.guest.restaurants.RestaurantProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuRestaurantFragment extends Fragment {

    Spinner spinner;
    private View DishView;
    private RecyclerView dishList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private DatabaseReference databaseReferenceDish;
    private DatabaseReference databaseReferenceCategory;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    Button btnAddDish;

    public MenuRestaurantFragment() {

    }

    public static MenuRestaurantFragment newInstance(String param1, String param2) {
        MenuRestaurantFragment fragment = new MenuRestaurantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onStart() {
        super.onStart();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String categoryName = spinner.getSelectedItem().toString();

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Category");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Category category = snapshot.getValue(Category.class);
                            if(categoryName.equals(category.name)){
                                populateRecycleView(category.idDish);
                                return;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DishView = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
        dishList = (RecyclerView) DishView.findViewById(R.id.rcvDishList);
        dishList.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReferenceDish = FirebaseDatabase.getInstance().getReference().child("Dish");
        btnAddDish = DishView.findViewById(R.id.btnAddDish);
        return DishView;

    }

    public void populateRecycleView(final String s) {
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Dish>()
                        .setQuery(databaseReferenceDish, Dish.class)
                        .build();

        FirebaseRecyclerAdapter<Dish, DishsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Dish, DishsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DishsViewHolder holder, int position, @NonNull Dish model) {
                final String IDs = getRef(position).getKey();
                databaseReferenceDish.child(IDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if (dataSnapshot.child("restaurantId").getValue().equals(firebaseAuth.getCurrentUser().getUid()) && dataSnapshot.child("category").getValue().equals(s)) {

                                if (dataSnapshot.hasChild("imgUrl")) {

                                    String name = dataSnapshot.child("name").getValue().toString();
                                    String category = dataSnapshot.child("category").getValue().toString();
                                    String description = dataSnapshot.child("description").getValue().toString();
                                    String dairyFree = dataSnapshot.child("dairyFree").getValue().toString();
                                    String glutenFree = dataSnapshot.child("glutenFree").getValue().toString();
                                    String price = dataSnapshot.child("price").getValue().toString();
                                    String imgUrl = dataSnapshot.child("imgUrl").getValue().toString();


                                    holder.dishName.setText(name);

                                    databaseReferenceCategory = FirebaseDatabase.getInstance().getReference("Category").child(category).child("name");
                                    databaseReferenceCategory.addValueEventListener((new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            holder.dishCategory.setText(dataSnapshot.getValue().toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Activity activity = getActivity();
                                            Toast.makeText(activity, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));

                                    holder.dishDescription.setText(description);
                                    if (dairyFree.equals("true")) {
                                        holder.dairyFree.setText("Dairy free");
                                    }
                                    if (dairyFree.equals("true")) {
                                        holder.glutenFree.setText("Gluten free");
                                    }
                                    holder.price.setText(price + " " + "HRK");
                                    Picasso.get().load(imgUrl).placeholder(R.drawable.common_google_signin_btn_icon_dark).into(holder.dishImage);

                                } else {
                                    String name = dataSnapshot.child("name").getValue().toString();
                                    String category = dataSnapshot.child("category").getValue().toString();
                                    String description = dataSnapshot.child("description").getValue().toString();
                                    String dairyFree = dataSnapshot.child("dairyFree").getValue().toString();
                                    String glutenFree = dataSnapshot.child("glutenFree").getValue().toString();
                                    String price = dataSnapshot.child("price").getValue().toString();

                                    holder.dishName.setText(name);
                                    holder.dishCategory.setText(category);
                                    holder.dishDescription.setText(description);
                                    holder.dairyFree.setText(dairyFree);
                                    holder.glutenFree.setText(glutenFree);
                                    holder.price.setText(price);
                                }

                            }
                            else{
                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                params.height = 0;
                                holder.itemView.setLayoutParams(params);
                                holder.itemView.setVisibility(View.GONE);
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent profileIntent = new Intent(getContext(), ManuRestaurantEditActivity.class);
                                    profileIntent.putExtra("dish_id", IDs);
                                    profileIntent.putExtra("dish_name", dataSnapshot.child("name").getValue().toString());
                                    profileIntent.putExtra("dish_category", dataSnapshot.child("category").getValue().toString());
                                    profileIntent.putExtra("dish_description", dataSnapshot.child("description").getValue().toString());
                                    profileIntent.putExtra("dish_dairy_free", dataSnapshot.child("dairyFree").getValue().toString());
                                    profileIntent.putExtra("dish_gluten_free", dataSnapshot.child("dairyFree").getValue().toString());
                                    profileIntent.putExtra("dish_price", dataSnapshot.child("price").getValue().toString());
                                    if(dataSnapshot.hasChild("imgUrl")){
                                        profileIntent.putExtra("dish_img_url", dataSnapshot.child("imgUrl").getValue().toString());
                                    }
                                    else{
                                        profileIntent.putExtra("dish_img_url", "");
                                    }

                                    startActivity(profileIntent);
                                }
                            });
                        }


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
            }

            @NonNull
            @Override
            public DishsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_display_layout, viewGroup, false);
                DishsViewHolder viewHolder = new DishsViewHolder(view);
                return  viewHolder;
            }
        };

        dishList.setAdapter(adapter);
        adapter.startListening();

        btnAddDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getContext(), MenuRestaurantAddActivity.class);
                startActivity(profileIntent);
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner = (Spinner) DishView.findViewById(R.id.spnrCategory);
        databaseReferenceCategory = FirebaseDatabase.getInstance().getReference("Category");
        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        //databaseReferenceCategory = FirebaseDatabase.getInstance().getReference().child("Category");
        spinner.setAdapter(adapter);
        retreiveData();


    }

    public void retreiveData(){
        //String IDs = getRef(position).getKey();
        listener = databaseReferenceCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Category category = item.getValue(Category.class);
                    spinnerDataList.add(category.name);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
