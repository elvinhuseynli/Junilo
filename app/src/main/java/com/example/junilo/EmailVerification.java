package com.example.junilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class EmailVerification extends AppCompatActivity {

    EditText emailET;
    String email, subject="Verification message", message, emailDB;
    Button sendButton;
    PinView code;
    JavaMailAPI javaMailAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailET = (EditText) findViewById(R.id.editTextTextEmailAddress2);
        sendButton = (Button) findViewById(R.id.button7);
        code = (PinView) findViewById(R.id.pinView2);
        message = "Hope you are having a good day.\nBelow code is your verification code.\n" +
                "Don't share this password with anyone else.\n";

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailET.getText().toString();
                sendEmail();
            }
        });

        Intent startIntent = getIntent();
    }

    public int checkCode() {
        String otpCode = javaMailAPI.getOtpCode();
        String enteredCode = Objects.requireNonNull(code.getText()).toString();
        if(otpCode.equals(enteredCode))
            return 0;
        return 1;
    }

    public void sendEmail(){
        emailDB = emailET.getText().toString();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Query query = database.collection("users")
                .whereEqualTo("email", emailDB);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().size()!=0){
                        javaMailAPI = new JavaMailAPI(EmailVerification.this, email, subject, message);
                        javaMailAPI.execute();
                    }
                    else{
                        AlertDialog.Builder regDialog = new AlertDialog.Builder(EmailVerification.this);
                        regDialog.setMessage("Email address is not registered in the system");
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

    public void recoveryPageTransition(View view) {
        Intent recoveryIntent = new Intent(this, PasswordRecovery.class);
        if(checkCode() == 0) {
            recoveryIntent.putExtra("email", email);
            startActivity(recoveryIntent);
        }
        else{
            AlertDialog.Builder phoneDialog = new AlertDialog.Builder(EmailVerification.this);
            phoneDialog.setMessage("Invalid Login. Try again");
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
}