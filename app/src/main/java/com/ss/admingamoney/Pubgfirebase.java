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

public class Pubgfirebase extends AppCompatActivity {
    private String ERoomid, Epassword, Savecurrentdate, Savecurrenttime;
    private Button Submitbtn;
    private EditText InputRoomid, InputPassword;
    private DatabaseReference Pubgref;
    private String ProductRandomkey;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubgfirebase);

        Submitbtn = findViewById(R.id.Submitbtn4);
        Pubgref = FirebaseDatabase.getInstance().getReference("Pubg ID");
        InputRoomid = findViewById(R.id.Freefire1);
        InputPassword = findViewById(R.id.password4);
        Submitbtn = findViewById(R.id.Submitbtn4);
        loadingBar = new ProgressDialog(this);
        Submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }


    private void ValidateProductData() {

        ERoomid = InputRoomid.getText().toString();
        Epassword = InputPassword.getText().toString();

        if (ERoomid == null) {
            Toast.makeText(this, "Roomid is mandatory....", Toast.LENGTH_SHORT).show();

        } else if (Epassword == null) {
            Toast.makeText(this, "Password is mandatory....", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        Savecurrentdate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        Savecurrenttime = currentTime.format(calendar.getTime());
        ProductRandomkey = Savecurrentdate + Savecurrenttime;


        SaveProductInfotodatabase();


    }

    private void SaveProductInfotodatabase() {

        HashMap<String, Object> ProductMap = new HashMap<>();
        ProductMap.put("Roomid", ERoomid);
        ProductMap.put("Password", Epassword);
        ProductMap.put("pid", ProductRandomkey);

        Pubgref.child(ProductRandomkey).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent a = new Intent(Pubgfirebase.this, Pubgfirebase.class);
                            startActivity(a);
                            loadingBar.dismiss();
                            Toast.makeText(Pubgfirebase.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(Pubgfirebase.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }


}


