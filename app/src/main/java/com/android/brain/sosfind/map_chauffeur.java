package com.android.brain.sosfind;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.android.brain.sosfind.Common.Common;
import com.android.brain.sosfind.Models.FSMReponse;
import com.android.brain.sosfind.Models.Notification;
import com.android.brain.sosfind.Models.Sender;
import com.android.brain.sosfind.Models.Token;
import com.android.brain.sosfind.Remote.IFCMService;
import com.android.brain.sosfind.Remote.IGoogleAPI;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class map_chauffeur extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mtoolbar;
    double latPassager, longPassager;
    double latChauff, longChauff;
    private String customerId;
    private DatabaseReference base;
    private Circle passengermarker;
    private Marker driveMarker;
    private Polyline direction;
    private IFCMService fcservice;
    private IGoogleAPI googleService;
    private GeoFire geoFire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_chauffeur);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mtoolbar = (Toolbar) findViewById(R.id.bar_map);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Map Agent SOS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fcservice = Common.getFCMService();
        googleService = Common.getGoogleAPI();

        base = FirebaseDatabase.getInstance().getReference().child("Chauffeurs");

        if (getIntent() != null) {
            latPassager = getIntent().getDoubleExtra("lat", -0.1);
            longPassager = getIntent().getDoubleExtra("lng", -0.1);
            customerId = getIntent().getStringExtra("customerId");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();
    }

    private void sendArriveNotification(String customerId) {
        Token token = new Token(customerId);
        Notification notification = new Notification("Arrived", "Vous êtes prié de vous presenter");
        Sender sender = new Sender(token.getToken(), notification);
        fcservice.sendMessage(sender).enqueue(new Callback<FSMReponse>() {
            @Override
            public void onResponse(Call<FSMReponse> call, Response<FSMReponse> response) {
                if (response.body().success != 1) {
                    Toast.makeText(map_chauffeur.this, "Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FSMReponse> call, Throwable t) {

            }
        });

    }

    private void getLocation() {
        try {
            LatLng drive = new LatLng(latPassager, longPassager);
            driveMarker = mMap.addMarker(new MarkerOptions().position(drive)
                    .title("Civil à secourir")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_civil)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(drive, 16));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
