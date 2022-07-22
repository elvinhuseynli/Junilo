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
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginPage extends AppCompatActivity {

    EditText username, password;
    Boolean passwordVisible = false;
    PreferenceManager preferenceManager;
    String usernameDB, passwordDB;
    int errorExists = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Intent startIntent = getIntent();

        preferenceManager = new PreferenceManager(getApplicationContext());
//        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
//            Intent mainIntent = new Intent(LoginPage.this, MainPage.class);
//            startActivity(mainIntent);
//        }
        username = (EditText) findViewById(R.id.editTextTextPersonName3);
        password = (EditText) findViewById(R.id.editTextTextPassword2);

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = password.getSelectionEnd();
                        if(passwordVisible) {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public int checkValidity() {
        if(username.getText().toString().length() == 0) {
            username.setError("Field cannot be left blank");
            errorExists = 1;
        }
        if(password.getText().toString().length() == 0) {
            password.setError("Field cannot be left blank");
            errorExists = 1;
        }
        return errorExists;
    }

    public void checkDatabase() {
        usernameDB = username.getText().toString();
        passwordDB = password.getText().toString();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Query query = database.collection("users")
                .whereEqualTo("username", usernameDB)
                .whereEqualTo("password", passwordDB);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().size()!=0) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                preferenceManager.putBoolean("isSignedIn", true);
                                preferenceManager.putString("name", documentSnapshot.getString("name"));
                                preferenceManager.putString("userId", documentSnapshot.getId());
                                Intent mainIntent = new Intent(LoginPage.this, MainPage.class);
                                mainIntent.putExtra("userId", documentSnapshot.getId());
                                mainIntent.putExtra("email", documentSnapshot.getString("email"));
                                mainIntent.putExtra("name", documentSnapshot.getString("name"));
                                startActivity(mainIntent);
                            }
                            else{
                                AlertDialog.Builder regDialog = new AlertDialog.Builder(LoginPage.this);
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
                });
    }

    public void forgotPassword(View view) {
        Intent forgotPassword = new Intent(this, ForgotPassword.class);
        startActivity(forgotPassword);
    }

    public void registrationPageTransition(View view) {
        Intent regIntent = new Intent(this, RegistrationPage.class);
        startActivity(regIntent);
    }

    public void mainPageTransition(View view) {
        errorExists = 0;
        if(checkValidity()==0)
            checkDatabase();
    }
}