package com.ss.admingamoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RoomID extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    EditText RoomID, Password, Pricepaid, TournamentID;
    ProgressDialog loadingbar;
    Button Submitbttn;
    DatabaseReference reference;
    String roomid, password, tournamentid, pricepaid, Childname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_i_d);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.RoomID);

        RoomID = findViewById(R.id.roomID);
        Password = findViewById(R.id.Password);
        Pricepaid = findViewById(R.id.price);
        TournamentID = findViewById(R.id.tournamentid);
        Submitbttn = findViewById(R.id.submit);
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
                            Toast.makeText(RoomID.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            loadingbar.dismiss();
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(RoomID.this,"Error"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.pubg:
                Intent intent5 = new Intent(RoomID.this, MainActivity.class);
                startActivity(intent5);
                break;

            case R.id.cod:
                Intent intent2 = new Intent(RoomID.this, CodActivity.class);
                startActivity(intent2);
                break;

            case R.id.csgo:
                Intent intent3 = new Intent(RoomID.this, CSGO_Activity.class);
                startActivity(intent3);
                break;

            case R.id.freefire:
                Intent intent4 = new Intent(RoomID.this, FreefireActivity.class);
                startActivity(intent4);
                break;

            case R.id.RoomID:
                break;

        }
        return true;
    }
}