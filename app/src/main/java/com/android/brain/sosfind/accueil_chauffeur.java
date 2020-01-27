package com.android.brain.sosfind;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

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


public class accueil_chauffeur extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference base;
    private Timer timer;
    MyTimerTask myTimerTask;
    private CChauffeur chauffeur;
    private FirebaseAuth mAuth;

    private getCurrentLocation loc;
    private Location location;
    double lat, longs;


    private void update_latitude_longitude(final CChauffeur ch) {
        try {
            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            String id_user = current_user.getUid();
            base = FirebaseDatabase.getInstance().getReference().child("Chauffeurs").child(id_user);
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
        setContentView(R.layout.activity_accueil_chauffeur);

        mAuth = FirebaseAuth.getInstance();

        //declaration bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.bar_Accueil_chauffeur);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Accueil");



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/



        //declaration tool du drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateFirebaseTken();
    }

    private void updateFirebaseTken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.action_menu, menu);

        MenuItem menuSwitch = menu.findItem(R.id.mySwitch);
        menuSwitch.setActionView(R.layout.user_switch);
        final Switch sw = (Switch) menu.findItem(R.id.mySwitch).getActionView().findViewById(R.id.action_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 1000, 30000);
                } else {
                    timer.cancel();
                    loc = new getCurrentLocation(accueil_chauffeur.this);
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
                    chauffeur.setStatus("false");
                    update_latitude_longitude(chauffeur);
                }
            }
        });

        return true;
        /*getMenuInflater().inflate(R.menu.accueil_chauffeur, menu);
        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {


        } else if (id == R.id.btnDeconnexion_chauff) {
            mAuth.signOut();
            startActivity(new Intent(accueil_chauffeur.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loc = new getCurrentLocation(accueil_chauffeur.this);
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
}
