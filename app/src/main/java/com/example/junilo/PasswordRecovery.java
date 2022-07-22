package com.example.junilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PasswordRecovery extends AppCompatActivity {

    String phone, email, password1, password2;
    EditText pw1, pw2;
    Boolean pw1Visible=false, pw2Visible=false;
    Button confirmButton;
    int errorExists = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        Intent startIntent = getIntent();
        phone = startIntent.getStringExtra("phone");
        email = startIntent.getStringExtra("email");

        pw1 = (EditText) findViewById(R.id.editTextTextPassword4);
        pw2 = (EditText) findViewById(R.id.editTextTextPassword5);

        confirmButton = (Button) findViewById(R.id.button13);

        pw1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= pw1.getRight()-pw1.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = pw1.getSelectionEnd();
                        if(pw1Visible) {
                            pw1.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                            pw1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            pw1Visible = false;
                        }
                        else {
                            pw1.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                            pw1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            pw1Visible = true;
                        }
                        pw1.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        pw2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= pw2.getRight()-pw2.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = pw2.getSelectionEnd();
                        if(pw2Visible) {
                            pw2.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                            pw2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            pw2Visible = false;
                        }
                        else {
                            pw2.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                            pw2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            pw2Visible = true;
                        }
                        pw2.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public int checkValidity() {
        password1 = pw1.getText().toString();
        password2 = pw2.getText().toString();
        if(password2.length() < 8 || password2.length() >20) {
            pw2.setError("Password length should be between 8 and 20");
            errorExists = 1;
        }
        else if(!password2.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$")) {
            pw2.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter");
            errorExists = 1;
        }
        if(password1.length() < 8 || password1.length() >20) {
            pw1.setError("Password length should be between 8 and 20");
            errorExists = 1;
        }
        else if(!password1.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$")) {
            pw1.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter");
            errorExists = 1;
        }
        if(!password1.equals(password2)){
            AlertDialog.Builder regDialog = new AlertDialog.Builder(PasswordRecovery.this);
            regDialog.setMessage("Passwords don't match");
            regDialog.setTitle("Error");
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
            errorExists = 1;
        }
        return errorExists;
    }

    public void recoverPasswordEmail() {
        if(checkValidity() == 0) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            Query query = database.collection("users")
                    .whereEqualTo("email", email);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().size()!=0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String id = documentSnapshot.getId();
                            String pass = documentSnapshot.getString("password");
                            if (!pass.equals(password1)) {
                                database.collection("users")
                                        .document(id).update("password", password1);
                                Intent loginIntent = new Intent(PasswordRecovery.this, LoginPage.class);
                                startActivity(loginIntent);
                            }
                            else {
                                AlertDialog.Builder regDialog = new AlertDialog.Builder(PasswordRecovery.this);
                                regDialog.setMessage("Your new password cannot be same as old one");
                                regDialog.setTitle("Error");
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
                        else{
                            //Ignore
                        }
                    }
                }
            });
        }
    }

    public void recoverPasswordPhone() {
        if(checkValidity() == 0) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            Query query = database.collection("users")
                    .whereEqualTo("phone", phone);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().size()!=0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String id = documentSnapshot.getId();
                            String pass = documentSnapshot.getString("password");
                            if (!pass.equals(password1)) {
                                database.collection("users")
                                        .document(id).update("password", password1);
                                Intent loginIntent = new Intent(PasswordRecovery.this, LoginPage.class);
                                startActivity(loginIntent);
                            }
                            else {
                                AlertDialog.Builder regDialog = new AlertDialog.Builder(PasswordRecovery.this);
                                regDialog.setMessage("Your new password cannot be same as old one");
                                regDialog.setTitle("Error");
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
                        else{
                            //Ignore
                        }
                    }
                }
            });
        }
    }

    public void loginPageTransition(View view) {
        if(email!=null) {
            recoverPasswordEmail();
        }
        else {
            recoverPasswordPhone();
        }
    }
}