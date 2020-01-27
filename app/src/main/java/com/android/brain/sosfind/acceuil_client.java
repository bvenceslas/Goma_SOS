package com.android.brain.sosfind;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import com.android.brain.sosfind.Controllers.getCurrentLocation;
import com.android.brain.sosfind.Models.CChauffeur;
import com.android.brain.sosfind.Models.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

public class acceuil_client extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mtoolbar;
    private ImageView img, btn_cmd_taxi, btn_comment, btn_mvt_cl;
    private FirebaseAuth mAuth;

    private DatabaseReference base;
    private CChauffeur chauffeur;
    private Timer timer;
    MyTimerTask myTimerTask;
    private getCurrentLocation loc;
    private Location location;
    double lat, longs;


    private void update_latitude_longitude(final CChauffeur ch) {
        try {
            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            String id_user = current_user.getUid();
            base = FirebaseDatabase.getInstance().getReference().child("Clients").child(id_user);
            base.child("status").setValue(ch.getStatus());
            base.child("latitude").setValue(ch.getLatiude());
            base.child("longitude").setValue(ch.getLontitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil_client);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar) findViewById(R.id.bar_Accueil_client);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Accueil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 30000);

        //Defines views
        img = (ImageView) findViewById(R.id.btn_logout_client);
        btn_cmd_taxi = (ImageView) findViewById(R.id.btn_cmd_taxi);

        //Actions Listener
        img.setOnClickListener(this);
        btn_cmd_taxi.setOnClickListener(this);


        updateFirebaseTken();
    }

    @Override
    public void onClick(View v) {
        try {

            switch (v.getId()) {
                case R.id.btn_logout_client:
                    mAuth.signOut();
                    startActivity(new Intent(acceuil_client.this, MainActivity.class));
                    break;
                case R.id.btn_cmd_taxi:
                    startActivity(new Intent(acceuil_client.this, MapsActivity.class));
                    break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loc = new getCurrentLocation(acceuil_client.this);
                    location = loc.getLocation();

                    if (location != null) {
                        lat = location.getLatitude();
                        longs = location.getLongitude();
                    } else {
                        lat = 0.0;
                        longs = 0.0;
                    }

                    chauffeur = new CChauffeur();
                    chauffeur.setLatiude("" + lat);
                    chauffeur.setLontitude("" + longs);
                    chauffeur.setStatus("true");
                    update_latitude_longitude(chauffeur);
                }
            });
        }
    }

    private void updateFirebaseTken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);
    }

}
