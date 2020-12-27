package com.example.readysteadyeat.ui.restaurant.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readysteadyeat.R;
import com.example.readysteadyeat.data.models.Category;
import com.example.readysteadyeat.data.models.Dish;
import com.example.readysteadyeat.ui.guest.BottomMenuGuestActivity;
import com.example.readysteadyeat.ui.guest.auth.LogInActivity;
import com.example.readysteadyeat.ui.guest.restaurants.RestaurantFragmentList;
import com.example.readysteadyeat.ui.restaurant.BottomMenuRestaurantActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManuRestaurantEditActivity extends AppCompatActivity {

    private Button btnSaveChanges, btnDeleteDish;
    private String dishId;
    private String dishName;
    private String dishCategory;
    private String dishDescription;
    private String dishDairyFree;
    private String dishGlutenFree;
    private String dishPrice;
    private String dishImgUrl;
    private String categoryName;
    CheckBox dairyFree;
    CheckBox glutenFree;
    Spinner spinnerCategory;
    ImageView restaurantImage;
    ArrayList<String> spinnerDataList;
    ArrayAdapter<String> adapter;
    ValueEventListener listener;
    EditText dish_name;
    EditText dish_description;
    EditText dish_price;
    String glutenFreeStr;
    String dairyFreeStr;
    CircleImageView imgvDish;

    String idCategory;

    private FirebaseAuth mAuth;

    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    //povuci oba bzutton
    //povuci sve iz boxova
    //dva onClick listenera
    //napraviti deltee
    //napravit update

    private DatabaseReference databaseReferenceDish;
    private DatabaseReference databaseReferenceCategory;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manu_rest_edit);

        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbarMenuEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dish edit");

        imgvDish = findViewById(R.id.imgvDish);
        spinnerCategory = findViewById(R.id.spnrCategory);
        dish_name = findViewById(R.id.txtDishName);
        dish_description = findViewById(R.id.txtDishDescription);
        dish_price = findViewById(R.id.txtPrice);
        glutenFree = findViewById(R.id.checkGluten);
        dairyFree = findViewById(R.id.checkboxDairy);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteDish = findViewById(R.id.btnDeleteDish);


        dishId = getIntent().getExtras().get("dish_id").toString();
        dishName = getIntent().getExtras().get("dish_name").toString();
        dishCategory = getIntent().getExtras().get("dish_category").toString();
        dishDescription = getIntent().getExtras().get("dish_description").toString();
        dishDairyFree = getIntent().getExtras().get("dish_dairy_free").toString();
        dishGlutenFree = getIntent().getExtras().get("dish_gluten_free").toString();
        dishPrice = getIntent().getExtras().get("dish_price").toString();
        dishImgUrl = getIntent().getExtras().get("dish_img_url").toString();

        populateItems();

        databaseReferenceCategory = FirebaseDatabase.getInstance().getReference("Category");
        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        spinnerCategory.setAdapter(adapter);
        retreiveData();
        //int selectionPosition = adapter.getPosition(dishCategory);
        //spinnerCategory.setSelection(5);

        btnDeleteDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReferenceDish = FirebaseDatabase.getInstance().getReference("Dish").child(dishId);
                databaseReferenceDish.removeValue();
                updateUI();
                Toast.makeText(getApplicationContext(), "Successfully deleted!", Toast.LENGTH_SHORT);
            }
        });

        imgvDish.setOnClickListener(new View.OnClickListener() {
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

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String imgUrl;
                final String categoryName = spinnerCategory.getSelectedItem().toString();
                final String name = dish_name.getText().toString();
                final String description = dish_description.getText().toString();
                final String price = dish_price.getText().toString();
                final boolean glutenFreeV =  glutenFree.isChecked();
                final boolean dairyFreeV = dairyFree.isChecked();

                if(categoryName.isEmpty() || name.isEmpty() || description.isEmpty() || price.isEmpty()) {
                    showMessage("Please Verify all fields");
                }
                else
                {
                    if(glutenFreeV) {
                        glutenFreeStr = "true";
                    }else {
                        glutenFreeStr = "false";
                    }
                    if(dairyFreeV) {
                        dairyFreeStr = "true";
                    }else {
                        dairyFreeStr = "false";
                    }

                    databaseReferenceCategory = FirebaseDatabase.getInstance().getReference("Category");
                    listener = databaseReferenceCategory.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot item : dataSnapshot.getChildren()){
                                Category category = item.getValue(Category.class);
                                if(categoryName.equals(category.name)){
                                    idCategory = category.idDish;
                                    Dish newDish = new Dish(idCategory, dairyFreeStr, description, glutenFreeStr, name, price, mAuth.getCurrentUser().getUid(), "");
                                    updateDishInfo(newDish, pickedImgUri, dishId);
                                    return;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void checkAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(ManuRestaurantEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ManuRestaurantEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(ManuRestaurantEditActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(ManuRestaurantEditActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        }
        else
        {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null && data.getData() != null){
            pickedImgUri = data.getData();
            Picasso.get().load(pickedImgUri).into(imgvDish);
        }

    }

    private void openGallery() {
        Intent galleryItent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryItent.setType("image/*");
        startActivityForResult(galleryItent, REQUESCODE);
    }

    private void updateDishInfo(final Dish newDish, final Uri pickedImgUri, final String dishId) {
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
                        databaseReferenceDish = FirebaseDatabase.getInstance().getReference("Dish");

                        newDish.setImgUrl(imageReference);

                        //databaseReferenceCategory.push().setValue(newDish);

                        databaseReferenceDish.child(dishId).setValue(newDish);
                        /*databaseReferenceCategory.child(key).child("description").setValue(newDish.description);
                        databaseReferenceCategory.child(key).child("dairyFree").setValue(newDish.dairyFree);
                        databaseReferenceCategory.child(key).child("glutenFree").setValue(newDish.glutenFree);
                        databaseReferenceCategory.child(key).child("restaurantId").setValue(newDish.restaurantId);
                        databaseReferenceCategory.child(key).child("name").setValue(newDish.name);
                        databaseReferenceCategory.child(key).child("price").setValue(newDish.price);
                        databaseReferenceCategory.child(key).child("imgUrl").setValue(imageReference);*/


                    }
                });
            }
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), BottomMenuRestaurantActivity.class);
        startActivity(homeActivity);
        finish();
    }

    public void retreiveData(){
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

    private void populateItems(){
        Picasso.get().load(dishImgUrl).placeholder(R.drawable.common_google_signin_btn_icon_dark).into(imgvDish);
        dish_name.setText(dishName);
        dish_description.setText(dishDescription);
        dish_price.setText(dishPrice);
        if(dishDairyFree.equals("true")){
            dairyFree.setChecked(true);
        }
        else{
            dairyFree.setChecked(false);
        }
        if(dishGlutenFree.equals("true")){
            glutenFree.setChecked(true);
        }
        else{
            glutenFree.setChecked(false);
        }
    }
}
