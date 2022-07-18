package com.example.junilo;

import static android.content.ContentValues.TAG;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText phoneNumber;
    FirebaseAuth phoneAuth;
    PinView otpCode;
    Button sendButton;
    Database database;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId, code, phoneDB;
    int taskSuccessful = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Intent startIntent = getIntent();
        phoneNumber = (EditText) findViewById(R.id.editTextPhone2);
        database = new Database(this);
        ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        sendButton = (Button) findViewById(R.id.button11);
        otpCode = (PinView) findViewById(R.id.pinView);

        phoneAuth = FirebaseAuth.getInstance();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCode();
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
                Toast.makeText(PhoneVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                mVerificationId = verificationId;
            }
        };
    }

    public int checkDatabase() {
        phoneDB = (new StringBuilder().append(ccp.getSelectedCountryCodeWithPlus()).append(phoneNumber.getText().toString())).toString();
        Boolean checkPhone = database.checkPhoneNumber(phoneDB);
        if(checkPhone) {
            sendOtpCode(phoneDB);
            return 0;
        }
        return 1;
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
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,enteredCode);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        phoneAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    taskSuccessful=1;
                    finish();
                } else
                    Toast.makeText(PhoneVerification.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendCode() {
        if(checkDatabase() != 0){
            AlertDialog.Builder phoneDialog = new AlertDialog.Builder(PhoneVerification.this);
            phoneDialog.setMessage("The phone number is not registered in the system");
            phoneDialog.setTitle("Alert");
            phoneDialog.setCancelable(false);
            phoneDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int nom) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = phoneDialog.create();
            dialog.show();
        }
    }

    public void recoveryPageTransition(View view) {
        Intent recoveryIntent = new Intent(this, PasswordRecovery.class);
        checkCode();
        if(taskSuccessful == 0) {
            recoveryIntent.putExtra("phone", phoneDB);
            startActivity(recoveryIntent);
        }
        else{
            AlertDialog.Builder regDialog = new AlertDialog.Builder(PhoneVerification.this);
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