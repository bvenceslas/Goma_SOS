package com.android.brain.sosfind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.brain.sosfind.Controllers.DabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login_chauff extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mtoolbar;
    private FloatingActionButton btnFloat_client;
    private Button btn_connect_chauffeur;
    private ProgressDialog mprogress;
    private FirebaseAuth mAuth;
    private DabaseHelper sqldb;


    private EditText etusername, etpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_chauff);


        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar) findViewById(R.id.bar_login_chauff);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Login Agent SOS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mprogress = new ProgressDialog(this);

        sqldb = new DabaseHelper(this);

        //link views
        btnFloat_client = (FloatingActionButton) findViewById(R.id.fab_login_chauffeur);
        btn_connect_chauffeur = (Button) findViewById(R.id.btnlogin_chauff);

        //Actions events
        btnFloat_client.setOnClickListener(this);
        btn_connect_chauffeur.setOnClickListener(this);

        //link to EditViews
        etusername = (EditText) findViewById(R.id.editlogin_chauff);
        etpassword = (EditText) findViewById(R.id.editpassword_chauff);

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.fab_login_chauffeur:
                    startActivity(new Intent(login_chauff.this, inscription_chauffeur.class));
                    break;
                case R.id.btnlogin_chauff:
                    if (!TextUtils.isEmpty(etusername.getText().toString())
                            && !TextUtils.isEmpty(etpassword.getText().toString())) {
                        mprogress.setTitle("Connexion");
                        mprogress.setMessage("Connexon en cours");
                        mprogress.setCanceledOnTouchOutside(false);
                        mprogress.show();
                        login_chauffeur(etusername.getText().toString(), etpassword.getText().toString());
                    } else {
                        Toast.makeText(login_chauff.this, "Completer tous les champs svp!!!", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void login_chauffeur(String mail, String password) {
        try {
            mAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                boolean isUpdated = sqldb.updateData("1", 1);
                                if (isUpdated) {
                                    mprogress.dismiss();
                                    startActivity(new Intent(login_chauff.this, accueil_chauffeur.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();
                                }
                            } else {
                                mprogress.hide();
                                Toast.makeText(login_chauff.this, "Echec de connexion", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
