package com.ss.admingamoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FreefireActivity extends AppCompatActivity {
    private String Eprice, Edescription, Eprize, Etime, Savecurrentdate, Savecurrenttime;
    private android.widget.ImageView inputEventImage;
    private Button AddNewEventButton;
    private EditText inputEventPrice, InputEventDescription, InputEventPrize, InputEventTime;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String ProductRandomKey, downloadimageurl;
    private StorageReference EventsImagesRef;
    private DatabaseReference EventsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freefire_);

        inputEventImage = findViewById(R.id.Freefire_image);
        EventsImagesRef = FirebaseStorage.getInstance().getReference().child("Freefire Images");
        EventsRef = FirebaseDatabase.getInstance().getReference("Freefire Tournaments");
        AddNewEventButton = findViewById(R.id.add_Event);
        inputEventPrice = findViewById(R.id.Freefire_price);
        InputEventDescription = findViewById(R.id.Freefire_description);
        InputEventPrize = findViewById(R.id.Freefire_prize);
        InputEventTime = findViewById(R.id.Freefire_time);
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
        Eprize = InputEventPrize.getText().toString();
        Etime = InputEventTime.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Event Image is mandotary........", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Edescription)) {
            Toast.makeText(this, "Please Write Event Description", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(Eprice)) {
            Toast.makeText(this, "Please Write Event price", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(Eprize)) {
            Toast.makeText(this, "Please Write Event price", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Etime)) {

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
                Toast.makeText(FreefireActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(FreefireActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(FreefireActivity.this, "got the Product image url successfully", Toast.LENGTH_SHORT).show();


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
        ProductMap.put("prize", Eprize);
        ProductMap.put("time", Etime);
        EventsRef.child(ProductRandomKey).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent a = new Intent(FreefireActivity.this, FreefireActivity.class);
                            startActivity(a);
                            loadingBar.dismiss();
                            Toast.makeText(FreefireActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(FreefireActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}