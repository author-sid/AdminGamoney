package com.ss.admingamoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CSGO_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    private String Eprice, Edescription, Eprize, Etime, Savecurrentdate, Savecurrenttime;
    private android.widget.ImageView inputEventImage;
    private Button AddNewEventButton;
    private EditText inputEventPrice, InputEventDescription, InputEventTime;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String ProductRandomKey, downloadimageurl;
    private StorageReference EventsImagesRef;
    private DatabaseReference EventsRef;
    private ProgressDialog loadingBar;
    private Button csgoroomid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csgo);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        csgoroomid = findViewById(R.id.csgoroomid);


        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        csgoroomid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Roomid3intent = new Intent(CSGO_Activity.this, Csgofirebase.class);
                startActivity(Roomid3intent);
                finish();

            }
        });


        inputEventImage = findViewById(R.id.CSGO_image);
        EventsImagesRef = FirebaseStorage.getInstance().getReference().child("CSGO Images");
        EventsRef = FirebaseDatabase.getInstance().getReference("CSGO Tournaments");
        AddNewEventButton = findViewById(R.id.add_Event);
        inputEventPrice = findViewById(R.id.CSGO_price);
        InputEventDescription = findViewById(R.id.CSGO_description);
        InputEventTime = findViewById(R.id.CSGO_time);
        loadingBar = new ProgressDialog(this);
        inputEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        AddNewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {

            ImageUri = data.getData();
            inputEventImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData() {
        Edescription = InputEventDescription.getText().toString();
        Eprice = inputEventPrice.getText().toString();
        Etime = InputEventTime.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Event Image is mandotary........", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Edescription)) {
            Toast.makeText(this, "Please Write Event Description", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(Eprice)) {
            Toast.makeText(this, "Please Write Event price", Toast.LENGTH_SHORT).show();

        }  else if (TextUtils.isEmpty(Etime)) {

            Toast.makeText(this, "Please enter Event time", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInfomation();

        }

    }

    private void StoreProductInfomation() {
        loadingBar.setTitle("Adding new Product");
        loadingBar.setMessage("Please Wait  while we are checking the credentials");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        Savecurrentdate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        Savecurrenttime = currentTime.format(calendar.getTime());
        ProductRandomKey = Savecurrentdate + Savecurrenttime;
        final StorageReference filepath = EventsImagesRef.child(ImageUri.getLastPathSegment() + ProductRandomKey + ".jpg");

        final UploadTask upload = filepath.putFile(ImageUri);


        upload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(CSGO_Activity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CSGO_Activity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = upload.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadimageurl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadimageurl = task.getResult().toString();
                            Toast.makeText(CSGO_Activity.this, "got the Product image url successfully", Toast.LENGTH_SHORT).show();


                            SaveProductInfotodatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfotodatabase() {


        HashMap<String, Object> ProductMap = new HashMap<>();
        ProductMap.put("pid", ProductRandomKey);
        ProductMap.put("description", Edescription);
        ProductMap.put("image", downloadimageurl);
        ProductMap.put("price", Eprice);
        ProductMap.put("time", Etime);
        EventsRef.child(ProductRandomKey).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent a = new Intent(CSGO_Activity.this, CSGO_Activity.class);
                            startActivity(a);
                            loadingBar.dismiss();
                            Toast.makeText(CSGO_Activity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(CSGO_Activity.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {


            case R.id.pubg:
                Intent intent2 = new Intent(CSGO_Activity.this, MainActivity.class);
                startActivity(intent2);
                break;

            case R.id.cod:
                Intent intent3 = new Intent(CSGO_Activity.this, CodActivity.class);
                startActivity(intent3);

                break;


            case R.id.csgo:

                break;

            case R.id.freefire:
                Intent intent4 = new Intent(CSGO_Activity.this, FreefireActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }
}