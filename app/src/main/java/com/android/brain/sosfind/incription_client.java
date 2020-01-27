package com.android.brain.sosfind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.brain.sosfind.Controllers.DabaseHelper;
import com.android.brain.sosfind.Controllers.getCurrentLocation;
import com.android.brain.sosfind.Models.CChauffeur;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class incription_client extends AppCompatActivity {

    private Toolbar mtoolbar;
    private EditText etNames;
    private Button btnConnect;
    private DatabaseReference base;
    private ProgressDialog mprogress;
    private CChauffeur client;
    private DabaseHelper sqldb;
    private getCurrentLocation loc;
    private Location location;
    double lat, longs;
    GeoFire geoFire;

    private void register_client(final CChauffeur ch) {
        try {

            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            String id_user = current_user.getUid();
            base = FirebaseDatabase.getInstance().getReference().child("Clients").child(id_user);
            HashMap<String, String> map_chauff = new HashMap<>();
            map_chauff.put("noms", ch.getNom());
            map_chauff.put("status", ch.getStatus());
            map_chauff.put("latitude", ch.getLatiude());
            map_chauff.put("longitude", ch.getLontitude());

            base.setValue(map_chauff).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        boolean isUpdated = sqldb.updateData("1", 2);
                        if (isUpdated) {
                            geoFire = new GeoFire(base);
                            geoFire.setLocation("position",
                                    new GeoLocation(Double.parseDouble(ch.getLatiude()),
                                            Double.parseDouble(ch.getLontitude())));
                            mprogress.dismiss();
                            Toast.makeText(incription_client.this, "Compte créé", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(incription_client.this, acceuil_client.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            mprogress.hide();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incription_client);

        loc = new getCurrentLocation(this);

        mtoolbar = (Toolbar) findViewById(R.id.bar_inscription_client);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Civil Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Create controls
        etNames = (EditText) findViewById(R.id.editNoms_client);
        btnConnect = (Button) findViewById(R.id.btn_inscr_client);

        mprogress = new ProgressDialog(this);
        sqldb = new DabaseHelper(this);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                location = loc.getLocation();
                if (location != null) {
                    lat = location.getLatitude();
                    longs = location.getLongitude();
                } else {
                    lat = 0.0;
                    longs = 0.0;
                }


                if (!TextUtils.isEmpty(etNames.getText().toString())) {

                    client = new CChauffeur();
                    client.setNom(etNames.getText().toString());
                    client.setStatus("true");
                    client.setLatiude("" + lat);
                    client.setLontitude("" + longs);

                    mprogress.setTitle("Fin de processus");
                    mprogress.setMessage("Connexion en cours!!!");
                    mprogress.setCanceledOnTouchOutside(false);
                    mprogress.show();
                    register_client(client);
                } else {
                    etNames.setError("Noms invalide");
                }
            }
        });
    }
}
