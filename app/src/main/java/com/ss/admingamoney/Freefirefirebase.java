package com.ss.admingamoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Freefirefirebase extends AppCompatActivity {
    private String ERoomid4, Epassword4, Savecurrentdate4, Savecurrenttime4;
    private Button Submitbtn4;
    private EditText InputRoomid, InputPassword;
    private DatabaseReference freefireref;
    private String ProductRandomkey4;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freefirefirebase);
        Submitbtn4 = findViewById(R.id.Submitbtn4);
        freefireref = FirebaseDatabase.getInstance().getReference("Freefire ID");
        InputRoomid = findViewById(R.id.Freefire1);
        InputPassword = findViewById(R.id.password4);
        Submitbtn4 = findViewById(R.id.Submitbtn4);
        loadingBar = new ProgressDialog(this);
        Submitbtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }

    private void ValidateProductData() {

        ERoomid4 = InputRoomid.getText().toString();
        Epassword4 = InputPassword.getText().toString();

        if (ERoomid4 == null) {
            Toast.makeText(this, "Roomid is mandatory....", Toast.LENGTH_SHORT).show();

        } else if (Epassword4 == null) {
            Toast.makeText(this, "Password is mandatory....", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        Savecurrentdate4 = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        Savecurrenttime4 = currentTime.format(calendar.getTime());
        ProductRandomkey4 = Savecurrentdate4 + Savecurrenttime4;


        SaveProductInfotodatabase();
    }

    private void SaveProductInfotodatabase() {

        HashMap<String, Object> ProductMap = new HashMap<>();
        ProductMap.put("Roomid", ERoomid4);
        ProductMap.put("Password", Epassword4);
        ProductMap.put("pid", ProductRandomkey4);

        freefireref.child(ProductRandomkey4).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent a = new Intent(Freefirefirebase.this, Freefirefirebase.class);
                            startActivity(a);
                            loadingBar.dismiss();
                            Toast.makeText(Freefirefirebase.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(Freefirefirebase.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }


}


