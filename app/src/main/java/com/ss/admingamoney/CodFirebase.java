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

public class CodFirebase extends AppCompatActivity {
    private String ERoomid1, Epassword1, Savecurrentdate1, Savecurrenttime1;
    private Button Submitbtn;
    private EditText InputRoomid, InputPassword;
    private DatabaseReference codref;
    private String ProductRandomkey1;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_firebase);

        Submitbtn = findViewById(R.id.Submitbtn2);
        codref = FirebaseDatabase.getInstance().getReference("Cod ID");
        InputRoomid = findViewById(R.id.Cod1);
        InputPassword = findViewById(R.id.password2);
        Submitbtn = findViewById(R.id.Submitbtn2);
        loadingBar = new ProgressDialog(this);
        Submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }

    private void ValidateProductData() {


        ERoomid1 = InputRoomid.getText().toString();
        Epassword1 = InputPassword.getText().toString();

        if (ERoomid1 == null) {
            Toast.makeText(this, "Roomid is mandatory....", Toast.LENGTH_SHORT).show();

        } else if (Epassword1 == null) {
            Toast.makeText(this, "Password is mandatory....", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        Savecurrentdate1 = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        Savecurrenttime1 = currentTime.format(calendar.getTime());
        ProductRandomkey1 = Savecurrentdate1 + Savecurrenttime1;


        SaveProductInfotodatabase();

    }

    private void SaveProductInfotodatabase() {
        HashMap<String, Object> ProductMap = new HashMap<>();
        ProductMap.put("Roomid", ERoomid1);
        ProductMap.put("Password", Epassword1);
        ProductMap.put("pid", ProductRandomkey1);

        codref.child(ProductRandomkey1).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent a = new Intent(CodFirebase.this, CodFirebase.class);
                            startActivity(a);
                            loadingBar.dismiss();
                            Toast.makeText(CodFirebase.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(CodFirebase.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

}



