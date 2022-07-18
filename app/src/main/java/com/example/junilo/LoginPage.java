package com.example.junilo;

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

public class LoginPage extends AppCompatActivity {

    EditText username, password;
    Boolean passwordVisible = false;
    Database database;
    String usernameDB;
    int errorExists = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Intent startIntent = getIntent();

        database = new Database(this);

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

    public int checkDatabase() {
        usernameDB = username.getText().toString();
        String passwordDB = password.getText().toString();
        System.out.println(usernameDB);
        System.out.println(passwordDB);

        Boolean checkUser = database.checkUsernamePassword(usernameDB, passwordDB);
        if(checkUser)
            return 0;
        else if(checkValidity() == 0){
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
        return 1;
    }

    public void forgotPassword(View view) {
        Intent forgotPassword = new Intent(this, ForgotPassword.class);
        startActivity(forgotPassword);
    }

    public void mainPageTransition(View view) {
        Intent mainIntent = new Intent(this, MainPage.class);
        mainIntent.putExtra("username", usernameDB);
        mainIntent.putExtra("name", database.getName(usernameDB));
        errorExists = 0;
        if(checkValidity()==0 && checkDatabase()==0)
            startActivity(mainIntent);
    }
}