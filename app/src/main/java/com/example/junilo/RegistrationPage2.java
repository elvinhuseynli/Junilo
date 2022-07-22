package com.example.junilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegistrationPage2 extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText phoneNumber;
    FirebaseAuth phoneAuth;
    PinView otpCode;
    Button sendButton;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId, code, phoneDB, name, username, password;
    int taskSuccessful = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page2);

        Intent startIntent = getIntent();
        phoneNumber = (EditText) findViewById(R.id.editTextPhone3);
        ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker2);
        sendButton = (Button) findViewById(R.id.button19);
        otpCode = (PinView) findViewById(R.id.pinView);

        Bundle extras = startIntent.getExtras();
        name = extras.getString("name");
        username = extras.getString("username");
        password = extras.getString("password");

        phoneAuth = FirebaseAuth.getInstance();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDatabase();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                code = credential.getSmsCode();
                if(code != null) {
                    otpCode.setText(code);
                    verifyCode(code);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(RegistrationPage2.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;
            }
        };
    }

    public void checkDatabase() {

        phoneDB = (new StringBuilder().append(ccp.getSelectedCountryCodeWithPlus()).append(phoneNumber.getText().toString())).toString();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Query query = database.collection("users")
                .whereEqualTo("phone", phoneDB);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size()==0){
                        sendOtpCode(phoneDB);
                    }
                    else{
                        AlertDialog.Builder regDialog = new AlertDialog.Builder(RegistrationPage2.this);
                        regDialog.setMessage("Phone number already exists");
                        regDialog.setTitle("Alert");
                        regDialog.setCancelable(false);
                        regDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int nom) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = regDialog.create();
                        dialog.show();
                    }
                }
            }
        });
    }

    public void sendOtpCode(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(phoneAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithCredential(credential);
    }

    public void checkCode() {
        String enteredCode = otpCode.getText().toString();
        if(enteredCode.isEmpty())
            taskSuccessful = 1;
        else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, enteredCode);
            signInWithCredential(credential);
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        phoneAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    taskSuccessful=1;
                    finish();
                } else
                    Toast.makeText(RegistrationPage2.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginPageTransition(View view) {
        Intent loginIntent = new Intent(this, RegistrationPage3.class);
        checkCode();
        if(taskSuccessful == 0) {
            loginIntent.putExtra("phone", phoneDB);
            loginIntent.putExtra("name", name);
            loginIntent.putExtra("username", username);
            loginIntent.putExtra("password", password);
            startActivity(loginIntent);
        }
        else{
            AlertDialog.Builder regDialog = new AlertDialog.Builder(RegistrationPage2.this);
            regDialog.setMessage("Invalid Login. Try again");
            regDialog.setTitle("Alert");
            regDialog.setCancelable(false);
            regDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int nom) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = regDialog.create();
            dialog.show();
        }
    }
}