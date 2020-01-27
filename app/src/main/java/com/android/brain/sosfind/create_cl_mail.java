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

import com.android.brain.sosfind.Controllers.getCurrentLocation;
import com.android.brain.sosfind.Models.CChauffeur;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class create_cl_mail extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mtoolbar;
    private EditText etMail, etPass, etVehicule, etNoms;
    private FirebaseAuth mAuth;
    private Button btn_inscrire;
    private ProgressDialog mprogress;
    private DatabaseReference base;
    private CChauffeur chauffeur;

    private getCurrentLocation loc;
    private Location location;
    double lat, longs;
    GeoFire geoFire;

    // Procedures
    private void register_clients(final CChauffeur ch) {
        try {
            mAuth.createUserWithEmailAndPassword(ch.getMail(), ch.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {


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
                                            geoFire = new GeoFire(base);
                                            geoFire.setLocation("position",
                                                    new GeoLocation(Double.parseDouble(ch.getLatiude()),
                                                            Double.parseDouble(ch.getLontitude())));
                                            mprogress.dismiss();
                                            Toast.makeText(create_cl_mail.this, "Compte créé", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(create_cl_mail.this, login_cl_m.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            finish();
                                        }
                                    }
                                });

                            } else {
                                mprogress.hide();
                                Toast.makeText(create_cl_mail.this, "Echec de création du compte",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cl_mail);

        //Instance Firebase
        mAuth = FirebaseAuth.getInstance();

        mprogress = new ProgressDialog(this);

        //Bar
        mtoolbar = (Toolbar) findViewById(R.id.bar_inscription_chauff);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Inscription du client");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Controls
        etMail = (EditText) findViewById(R.id.edit_user_chauff);
        etPass = (EditText) findViewById(R.id.edit_insc_password_chauff);
        etVehicule = (EditText) findViewById(R.id.edit_mat_voiture);
        etNoms = (EditText) findViewById(R.id.edit_noms_chauffeur);
        btn_inscrire = (Button) findViewById(R.id.btn_inscr_chauff);

        //Events
        btn_inscrire.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_inscr_chauff:

                    loc = new getCurrentLocation(this);
                    location = loc.getLocation();

                    if (location != null) {
                        lat = location.getLatitude();
                        longs = location.getLongitude();
                    } else {
                        lat = 0.0;
                        longs = 0.0;
                    }

                    if (!TextUtils.isEmpty(etMail.getText().toString()) && !TextUtils.isEmpty(etPass.getText().toString())
                            && !TextUtils.isEmpty(etNoms.getText().toString()) && !TextUtils.isEmpty(etVehicule.getText().toString())) {
                        chauffeur = new CChauffeur();
                        chauffeur.setMail(etMail.getText().toString());
                        chauffeur.setPassword(etPass.getText().toString());
                        chauffeur.setNom(etNoms.getText().toString());
                        chauffeur.setLatiude("" + lat);
                        chauffeur.setLontitude("" + longs);

                        mprogress.setTitle("Création du compte");
                        mprogress.setMessage("Sauvegarde en cours!!!");
                        mprogress.setCanceledOnTouchOutside(false);
                        mprogress.show();
                        register_clients(chauffeur);
                    } else {
                        Toast.makeText(create_cl_mail.this, "Complete all field", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
