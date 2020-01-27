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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.concurrent.TimeUnit;

public class login_client extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mtoolbar;
    private FloatingActionButton btnFloat_client;
    private Button btn_login_client, buttonVerifcationCode;
    private FirebaseAuth mAuth;
    private ProgressDialog mprogress;
    private EditText etPhone, etverifcationCode;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = etPhone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhone.setError("Invalid phone number.", getDrawable(R.drawable.ic_phone_android));
            return false;
        }
        return true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(login_client.this, incription_client.class));
                            mprogress.dismiss();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                etverifcationCode.setError("Code invalide");
                                mprogress.hide();
                            }
                        }
                    }
                });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider
                .getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_client);

        mtoolbar = (Toolbar) findViewById(R.id.bar_login_client);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Connexion Passager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mprogress = new ProgressDialog(this);

        //link views
        buttonVerifcationCode = (Button) findViewById(R.id.btnCodevalidation);
        btn_login_client = (Button) findViewById(R.id.btnlogin_client);
        etPhone = (EditText) findViewById(R.id.phoneNumber);
        etverifcationCode = (EditText) findViewById(R.id.etcodeverify);

        disableViews(buttonVerifcationCode, etverifcationCode);

        //Actions events
        buttonVerifcationCode.setOnClickListener(this);
        btn_login_client.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    etPhone.setError("Numéro de téléphone invalide",
                            getDrawable(R.drawable.ic_phone_android));
                }
                mprogress.hide();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                disableViews(btn_login_client, etPhone);
                mprogress.dismiss();
                enableViews(buttonVerifcationCode, etverifcationCode);
            }
        };
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnCodevalidation:
                    String code = etverifcationCode.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        etverifcationCode.setError("Aucun code envoyé");
                        return;
                    }

                    mprogress.setTitle("Vérification code");
                    mprogress.setMessage("Vérification en cours!!!");
                    mprogress.setCanceledOnTouchOutside(false);
                    mprogress.show();

                    verifyPhoneNumberWithCode(mVerificationId, code);

                    break;
                case R.id.btnlogin_client:
                    if (!validatePhoneNumber()) {
                        return;
                    }
                    mprogress.setTitle("Création du compte");
                    mprogress.setMessage("Requette en cours!!!");
                    mprogress.setCanceledOnTouchOutside(false);
                    mprogress.show();
                    startPhoneNumberVerification(etPhone.getText().toString());
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }
}
