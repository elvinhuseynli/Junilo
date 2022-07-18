package com.example.junilo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.chaos.view.PinView;
import java.util.Objects;

public class EmailVerification extends AppCompatActivity {

    EditText emailET;
    String email, subject="Verification message", message;
    Button sendButton;
    PinView code;
    Database database;
    JavaMailAPI javaMailAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailET = (EditText) findViewById(R.id.editTextTextEmailAddress2);
        sendButton = (Button) findViewById(R.id.button7);
        database = new Database(this);
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

    public int checkDatabase() {
        Boolean checkEmail = database.checkEmailAddress(email);
        if(checkEmail)
            return 0;
        return 1;
    }

    public void sendEmail(){
        if(checkDatabase() == 0) {
            javaMailAPI = new JavaMailAPI(this, email, subject, message);
            javaMailAPI.execute();
        }
        else{
            AlertDialog.Builder phoneDialog = new AlertDialog.Builder(EmailVerification.this);
            phoneDialog.setMessage("The email account is not registered in the system");
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