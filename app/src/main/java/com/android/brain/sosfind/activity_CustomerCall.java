package com.android.brain.sosfind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.brain.sosfind.Common.Common;
import com.android.brain.sosfind.Models.*;
import com.android.brain.sosfind.Remote.IFCMService;
import com.android.brain.sosfind.Remote.IGoogleAPI;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class activity_CustomerCall extends AppCompatActivity implements View.OnClickListener {

    private TextView txtAdress, txtDistance, txtTime;
    private MediaPlayer mediaPlayer;
    private FirebaseAuth mAut;
    double latitude, longitude;
    private Button btnAccept, btnCancel;
    IGoogleAPI mService;
    IFCMService mIfmService;
    String customerId;
    String idcmd, idPassager;
    double lat, lng;
    private DatabaseReference base;
    private ProgressDialog mProgress;
    private Ccommande cmds;

    private Timer timer;
    MyTimerTask myTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__customer_call);

        mAut = FirebaseAuth.getInstance();
        mService = Common.getGoogleAPI();
        mIfmService = Common.getFCMService();
        mProgress = new ProgressDialog(this);

        txtAdress = (TextView) findViewById(R.id.txtAdress);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnAccept = (Button) findViewById(R.id.btn_accept);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 5000);

        if (getIntent() != null) {
            lat = getIntent().getDoubleExtra("lat", -1.0);
            lng = getIntent().getDoubleExtra("longs", -1.0);
            customerId = getIntent().getStringExtra("customer");
            idcmd = getIntent().getStringExtra("idcmd");
            idPassager = getIntent().getStringExtra("idpassager");
            getDirecttion(lat, lng);
        }

        btnCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);

    }

    private void getDirecttion(final double lat, final double lng) {

        try {

            String user_id = mAut.getCurrentUser().getUid();
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Chauffeurs").child(user_id);
            mdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());

                    final LatLng currentPosition = new LatLng(latitude, longitude);

                    String requestAPI = null;
                    requestAPI = "https://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                            "destination=" + lat + "," + lng + "&" +
                            "key=AIzaSyCsDcqRjaZ1rgs91jmRqFpF2XJu53CPVdQ";
                    Log.d("EDMTDEV", requestAPI);


                    mService.getPath(requestAPI)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        JSONArray routes = jsonObject.getJSONArray("routes");
                                        JSONObject object = routes.getJSONObject(0);
                                        JSONArray legs = object.getJSONArray("legs");
                                        JSONObject legsObject = legs.getJSONObject(0);

                                        JSONObject distance = legsObject.getJSONObject("distance");
                                        txtDistance.setText(distance.getString("text"));

                                        JSONObject time = legsObject.getJSONObject("duration");
                                        txtTime.setText(time.getString("text"));

                                        String adress = legsObject.getString("end_address");
                                        txtAdress.setText(adress);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(activity_CustomerCall.this, t.getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                }
                            });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        timer.cancel();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        timer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mediaPlayer.start();
        /*timer.cancel();
        timer.schedule(myTimerTask, 1000, 5000);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                Intent intent = new Intent(activity_CustomerCall.this, map_chauffeur.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("customerId", customerId);
                set_data_to_mvt_chauffeur(); //to save details cmd
                startActivity(intent); //call map activity for tracking
                finish();
                break;
            case R.id.btn_cancel:
                if (!TextUtils.isEmpty(customerId))
                    canceBooking(customerId);
                break;
        }
    }

    private void canceBooking(String customerId) {
        Token token = new Token(customerId);
        Notification notification = new Notification("cancel_booking", "Le chauffeur viens d'annuler votre demande");
        Sender sender = new Sender(token.getToken(), notification);
        mIfmService.sendMessage(sender).enqueue(new Callback<FSMReponse>() {
            @Override
            public void onResponse(Call<FSMReponse> call, Response<FSMReponse> response) {
                if (response.body().success == 1) {
                    Toast.makeText(activity_CustomerCall.this, "Requête annulée", Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FSMReponse> call, Throwable t) {
            }
        });
    }

    private void add_mvt_chauffeurs(Ccommande cmd) {

        try {
            base = FirebaseDatabase.getInstance().getReference().child("mvtChauffeurs")
                    .child(cmd.getIdChauffeur())
                    .child(cmd.getIdPassager())
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

    private void set_data_to_mvt_chauffeur() {

        mProgress.setTitle("Acceptation");
        mProgress.setMessage("Liaison en cours!!!");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String datecmd = dateFormat.format(date);

        FirebaseUser user = mAut.getCurrentUser();

        cmds = new Ccommande();
        cmds.setIdChauffeur(user.getUid());
        cmds.setIdPassager(idPassager);
        cmds.setIdCmd(idcmd);
        cmds.setDatecmd(datecmd);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("mvtClient")
                .child(idPassager)
                .child(user.getUid())
                .child(idcmd);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cmds.setDepart(dataSnapshot.child("depart").getValue().toString());
                cmds.setDestination(dataSnapshot.child("destination").getValue().toString());
                cmds.setDetails(dataSnapshot.child("details").getValue().toString());
                cmds.setMontant(0);
                cmds.setSuccess(false);
                add_mvt_chauffeurs(cmds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.alarm1);
                    mediaPlayer.start();
                }
            });
        }
    }

}
