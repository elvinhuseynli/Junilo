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

import java.util.HashMap;
import java.util.Objects;

public class RegistrationPage3 extends AppCompatActivity {

    EditText emailET;
    String email, subject="Verification message", message, emailDB, name, username, phone, password;
    Button sendButton;
    PinView code;
    PreferenceManager preferenceManager;
    JavaMailAPI javaMailAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page3);

        Intent startIntent = getIntent();
        Bundle extras = startIntent.getExtras();
        name = extras.getString("name");
        username = extras.getString("username");
        phone = extras.getString("phone");
        password = extras.getString("password");

        preferenceManager = new PreferenceManager(getApplicationContext());

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
                    if(task.getResult().size()==0){
                        javaMailAPI = new JavaMailAPI(RegistrationPage3.this, email, subject, message);
                        javaMailAPI.execute();
                    }
                    else{
                        AlertDialog.Builder regDialog = new AlertDialog.Builder(RegistrationPage3.this);
                        regDialog.setMessage("Email address already exists");
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

    public void loginPageTransition(View view) {
        if(checkCode() == 0) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String, String> user = new HashMap<>();
            user.put("name", name);
            user.put("username", username);
            user.put("password", password);
            user.put("phone", phone);
            user.put("email", emailDB);
            database.collection("users").add(user)
                    .addOnSuccessListener(documentReference -> {
                        preferenceManager.putBoolean("isSignedIn", true);
                        preferenceManager.putString("name", name);
                        preferenceManager.putString("userId", documentReference.getId());
                        Intent loginIntent = new Intent(RegistrationPage3.this, LoginPage.class);
                        startActivity(loginIntent);
                    })
                    .addOnFailureListener(exception -> {
                        //Failure
                    });
        }
    }
}