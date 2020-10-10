package com.ss.admingamoney;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Pubgfirebase extends AppCompatActivity {
    EditText RoomID, Password, Pricepaid, TournamentID;
    Button Submitbttn;
    ProgressDialog loadingbar;
    DatabaseReference reference;
    String roomid, password, tournamentid, pricepaid, Childname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubgfirebase);

        RoomID = findViewById(R.id.Roomid1);
        Password = findViewById(R.id.password4);
        Pricepaid = findViewById(R.id.pricejoined);
        TournamentID = findViewById(R.id.tournamentid1);
        Submitbttn = findViewById(R.id.Submitbtn4);
        loadingbar= new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference("RoomID");

        Submitbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreProductinfo();
            }
        });

    }

    private void StoreProductinfo() {
        roomid = RoomID.getText().toString();
        password= Password.getText().toString();
        tournamentid = TournamentID.getText().toString();
        pricepaid = Pricepaid.getText().toString();
        Childname = tournamentid+ " " + pricepaid;

        if (roomid == null){
            RoomID.setError("Enter Room ID");
        }else if (password == null){
            Password.setError("Enter Password");
        }else if (tournamentid == null){
            TournamentID.setError("Enter Tournament ID");
        }

        HashMap<String, Object> ProdutMap = new HashMap<>();
        ProdutMap.put("roomid",roomid);
        ProdutMap.put("password",password);
        ProdutMap.put("tournamentid",tournamentid);

        reference.child(Childname).updateChildren(ProdutMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            loadingbar.dismiss();
                            Toast.makeText(Pubgfirebase.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            loadingbar.dismiss();
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(Pubgfirebase.this,"Error"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}


