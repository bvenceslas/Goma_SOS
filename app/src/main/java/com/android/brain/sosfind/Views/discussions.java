package com.android.brain.sosfind.Views;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.brain.sosfind.Common.Common;
import com.android.brain.sosfind.Models.FSMReponse;
import com.android.brain.sosfind.Models.Notification;
import com.android.brain.sosfind.Models.Sender;
import com.android.brain.sosfind.Models.Token;
import com.android.brain.sosfind.R;
import com.android.brain.sosfind.Remote.IFCMService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class discussions extends AppCompatActivity implements View.OnClickListener {

    private Button btn_send_msg, btn_cancel;
    private EditText etmsg;
    String driverID, mesg, signal;
    IFCMService mService; //send message
    FirebaseAuth mAut;
    TextView etReponse;
    private MediaPlayer mediaPlayer;
    private Toolbar mtoolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussions);


        btn_send_msg = (Button) findViewById(R.id.btn_msg_send);
        btn_cancel = (Button) findViewById(R.id.btn_msg_cancel);
        etmsg = (EditText) findViewById(R.id.id_msg);
        etReponse = (TextView) findViewById(R.id.id_reponse);

        mtoolbar = (Toolbar) findViewById(R.id.bar_discussions);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Discussions Space");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAut = FirebaseAuth.getInstance();

        mService = Common.getFCMService();

        if (getIntent() != null) {
            driverID = getIntent().getStringExtra("destinateur");
            mesg = getIntent().getStringExtra("message");
            signal = getIntent().getStringExtra("signal");
            etReponse.setText(mesg);

            if (signal.equals("R")) {
                mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.ringtonecmd);
                mediaPlayer.start();
            }
        }

        btn_send_msg.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_msg_send:
                sendMessage();
                Toast.makeText(discussions.this, driverID, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_msg_cancel:
                finish();
                break;
        }
    }

    private void sendMessage() {
        try {
            final String iduser_exp = mAut.getCurrentUser().getUid();
            if (!TextUtils.isEmpty(etmsg.getText().toString())) {

                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                tokens.orderByKey().equalTo(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Token token = postSnapshot.getValue(Token.class);
                            Notification data = new Notification("prix", etmsg.getText().toString() + ":::" + iduser_exp);
                            Sender content = new Sender(token.getToken(), data);

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FSMReponse>() {
                                        @Override
                                        public void onResponse(Call<FSMReponse> call, Response<FSMReponse> response) {
                                            if (response.body().success == 1) {
                                                showMessage("Confirmation", "Votre message a été envoyé");
                                            } else {
                                                showMessage("Echech", "Erreur");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FSMReponse> call, Throwable t) {
                                            showMessage("Failure", "" + t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                etmsg.setError("Ecrivez un texte");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.cancel();
            }
        });
        builder.show();
    }

}
