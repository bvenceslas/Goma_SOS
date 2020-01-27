package com.android.brain.sosfind;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private DatabaseReference base;
    private ChildEventListener mChildEventListener;
    private Marker marker;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ChildEventListener mChildEventListener;
        base = FirebaseDatabase.getInstance().getReference().child("Chauffeurs");


        mtoolbar = (Toolbar) findViewById(R.id.bar_map);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Map Civil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //base.push().setValue(marker);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        base.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    final String key = s.getKey();
                    base.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

//                            if(dataSnapshot.child("status").getValue().toString().equals("true"))
//                            {
                            String name = dataSnapshot.child("noms").getValue().toString();
                            double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                            double longs = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                            LatLng location = new LatLng(lat, longs);

                            marker = mMap.addMarker(new MarkerOptions().position(location).title(name).snippet(key)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_sos)));
                            marker.setTag(0);
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
                            rotateMaker(marker, -360, mMap);
                            //}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Integer clickCount = (Integer) marker.getTag();
        if (clickCount != null) {

            final String destination_id = marker.getSnippet();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Demande de Secours");
            alertDialog.setMessage("Voulez-vous demander un secours?");
            alertDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(MapsActivity.this, activity_commande.class)
                            .putExtra("id_destinateur", destination_id));
                }
            });
            alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();


            /*clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + marker.getSnippet() + " times.",
                    Toast.LENGTH_SHORT).show();*/
        }

        return false;
    }

    private void rotateMaker(final Marker mcurrent, final float i, GoogleMap mMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = mcurrent.getRotation();
        final long duration = 1500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                float rot = t * i + (1 - t) * startRotation;
                mcurrent.setRotation(rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

}
