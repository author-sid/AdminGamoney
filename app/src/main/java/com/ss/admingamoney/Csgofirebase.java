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

public class Csgofirebase extends AppCompatActivity {
    private String ERoomid3, Epassword3, Savecurrentdate3, Savecurrenttime3;
    private Button Submitbtn3;
    private EditText InputRoomid, InputPassword;
    private DatabaseReference csgoref;
    private String ProductRandomkey3;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csgofirebase);
        Submitbtn3 = findViewById(R.id.Submitbtn4);
        csgoref = FirebaseDatabase.getInstance().getReference("Csgo ID");
        InputRoomid = findViewById(R.id.Freefire1);
        InputPassword = findViewById(R.id.password4);
        Submitbtn3 = findViewById(R.id.Submitbtn4);
        loadingBar = new ProgressDialog(this);
        Submitbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }

    private void ValidateProductData() {

        ERoomid3 = InputRoomid.getText().toString();
        Epassword3 = InputPassword.getText().toString();

        if (ERoomid3 == null) {
            Toast.makeText(this, "Roomid is mandatory....", Toast.LENGTH_SHORT).show();

        } else if (Epassword3 == null) {
            Toast.makeText(this, "Password is mandatory....", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        Savecurrentdate3 = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        Savecurrenttime3 = currentTime.format(calendar.getTime());
        ProductRandomkey3 = Savecurrentdate3 + Savecurrenttime3;


        SaveProductInfotodatabase();
    }

    private void SaveProductInfotodatabase() {

        HashMap<String, Object> ProductMap = new HashMap<>();
        ProductMap.put("Roomid", ERoomid3);
        ProductMap.put("Password", Epassword3);
        ProductMap.put("pid", ProductRandomkey3);

        csgoref.child(ProductRandomkey3).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent a = new Intent(Csgofirebase.this, Csgofirebase.class);
                            startActivity(a);
                            loadingBar.dismiss();
                            Toast.makeText(Csgofirebase.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(Csgofirebase.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }


}


