package com.android.brain.sosfind;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.android.brain.sosfind.Controllers.DabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnClient, btnChauffeur;
    private DabaseHelper sqldb;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        btnChauffeur = (Button) findViewById(R.id.btn_chauf);
        btnClient = (Button) findViewById(R.id.btn_client);

        btnClient.setOnClickListener(this);
        btnChauffeur.setOnClickListener(this);

        sqldb = new DabaseHelper(this);

        this.insert_initial();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendToStart();
    }

    private void sendToStart() {
        int conectedUser = 0;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Cursor res = sqldb.getData();
            while (res.moveToNext()) {
                conectedUser = res.getInt(1);
            }
            if (conectedUser == 1) {
                startActivity(new Intent(MainActivity.this, accueil_chauffeur.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else if (conectedUser == 2) {
                startActivity(new Intent(MainActivity.this, acceuil_client.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else {
                if (currentUser != null && conectedUser == 0) {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_chauf:
                    startActivity(new Intent(MainActivity.this, login_chauff.class));
                    break;
                case R.id.btn_client:
                    startActivity(new Intent(MainActivity.this, login_cl_m.class));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insert_initial() {
        try {
            Cursor res = sqldb.getData();
            if (res.getCount() == 0) {
                boolean isInserted = sqldb.insertData();
                if (isInserted)
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
