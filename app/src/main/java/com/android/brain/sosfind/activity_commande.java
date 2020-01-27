package com.android.brain.sosfind;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.brain.sosfind.Common.Common;
import com.android.brain.sosfind.Models.*;
import com.android.brain.sosfind.Remote.IFCMService;
import com.android.brain.sosfind.Views.discussions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class activity_commande extends AppCompatActivity {

    private Toolbar mtoolbar;
    private EditText et_id_destinateur, etDepart, etDestination, etDetails;
    private Button btn_valider_cmd;
    FirebaseAuth mAut;
    String lat, longs;
    IFCMService mService; //send alert;
    private DatabaseReference base;
    private Ccommande cmds;
    private String idcmd;
    private ProgressDialog mProgress;
    private FloatingActionButton btnFloat_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commande);

        idcmd = random();
        mAut = FirebaseAuth.getInstance();
        mService = Common.getFCMService();
        mProgress = new ProgressDialog(this);

        mtoolbar = (Toolbar) findViewById(R.id.bar_commande_taxis);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Demande de Secours");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        et_id_destinateur = (EditText) findViewById(R.id.edit_id_destinateur);
        disableViews(et_id_destinateur);
        et_id_destinateur.setText(getIntent().getStringExtra("id_destinateur"));

        etDepart = (EditText) findViewById(R.id.edit_depart);
        etDestination = (EditText) findViewById(R.id.edit_destination);
        etDetails = (EditText) findViewById(R.id.edit_details_commande);
        btn_valider_cmd = (Button) findViewById(R.id.btn_add_commande);
        btnFloat_send = (FloatingActionButton) findViewById(R.id.fab_activity_msg);

        etDepart.setVisibility(View.INVISIBLE);
        etDetails.setVisibility(View.INVISIBLE);
        etDestination.setVisibility(View.INVISIBLE);

        btn_valider_cmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                }
        });

        btnFloat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_commande.this, discussions.class);
                intent.putExtra("destinateur", et_id_destinateur.getText().toString());
                intent.putExtra("message", "");
                intent.putExtra("signal", "O");
                startActivity(intent);
                finish();
            }
        });

        execute_Cmd();
        this.setVisible(false);
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    boolean isSending;

    private Boolean Commander_un_taxi(String id_chauffeur) {
        isSending = true;
        try {
            final String user_id = mAut.getCurrentUser().getUid();
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Clients").child(user_id);
            mdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lat = dataSnapshot.child("latitude").getValue().toString();
                    longs = dataSnapshot.child("longitude").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
            tokens.orderByKey().equalTo(id_chauffeur).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Token token = postSnapshot.getValue(Token.class);
                        String json_lat_long = new Gson()
                                .toJson(new LatLng(Double.parseDouble(lat), Double.parseDouble(longs)));
                        String customerToken = FirebaseInstanceId.getInstance().getToken();
                        Notification data = new Notification(customerToken + "," + idcmd + "," + user_id, json_lat_long);
                        Sender content = new Sender(token.getToken(), data);

                        mService.sendMessage(content)
                                .enqueue(new Callback<FSMReponse>() {
                                    @Override
                                    public void onResponse(Call<FSMReponse> call, Response<FSMReponse> response) {
                                        if (response.body().success == 1) {
                                            isSending = true;
                                        } else {
                                            isSending = false;
                                            mProgress.hide();
                                            showMessage("Echec d'envoi", "Demande echouée");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FSMReponse> call, Throwable t) {
                                        mProgress.hide();
                                        Log.e("Error", t.getMessage());
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isSending;
    }

    private void add_mvt_client(Ccommande cmd) {

        try {
            base = FirebaseDatabase.getInstance().getReference().child("mvtClient")
                    .child(cmd.getIdPassager())
                    .child(cmd.getIdChauffeur())
                    .child(cmd.getIdCmd());

            HashMap<String, String> putcmd = new HashMap<>();
            putcmd.put("datecmd", cmd.getDatecmd());
            putcmd.put("depart", cmd.getDepart());
            putcmd.put("destination", cmd.getDestination());
            putcmd.put("details", cmd.getDetails());
            putcmd.put("montant", Double.toString(cmd.getMontant()));
            putcmd.put("succes", Boolean.toString(cmd.isSuccess()));
            base.setValue(putcmd);
            mProgress.dismiss();
        } catch (Exception ex) {
            mProgress.hide();
            ex.printStackTrace();
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            int randomLength = generator.nextInt(10);
            randomStringBuilder.append(randomLength);
        }
        return randomStringBuilder.toString();
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void execute_Cmd(){
        mProgress.setTitle("Demande");
        mProgress.setMessage("Demande en cours d'envoie!!!");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        if (Commander_un_taxi(et_id_destinateur.getText().toString())) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String datecmd = dateFormat.format(date);

            FirebaseUser user = mAut.getCurrentUser();

            cmds = new Ccommande();
            cmds.setIdPassager(user.getUid());
            cmds.setIdChauffeur(et_id_destinateur.getText().toString());
            cmds.setIdCmd(idcmd);
            cmds.setDatecmd(datecmd);
            cmds.setDepart(etDepart.getText().toString());
            cmds.setDestination(etDestination.getText().toString());
            cmds.setDetails(etDetails.getText().toString());
            cmds.setMontant(0);
            cmds.setSuccess(false);

            add_mvt_client(cmds);
            showMessage("Confirmation", "Requête envoyée aux services de secours");
        } else {
            mProgress.hide();
            Toast.makeText(activity_commande.this, "" + Boolean.toString(Commander_un_taxi(et_id_destinateur.getText().toString())), Toast.LENGTH_LONG)
                    .show();
        }
    }
}
