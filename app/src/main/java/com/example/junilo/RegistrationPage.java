package com.example.junilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;
import java.util.Calendar;
import java.util.HashMap;

public class RegistrationPage extends AppCompatActivity {

    EditText name, username, password, password2;
    CheckBox license;
    public Boolean passwordVisible = false;
    int errorExists = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        Intent startIntent = getIntent();

        name = (EditText) findViewById(R.id.editTextTextPersonName);
        username = (EditText) findViewById(R.id.editTextTextPersonName2);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        password2 = (EditText) findViewById(R.id.editTextTextPassword3);
        license = (CheckBox) findViewById(R.id.checkBox);

        setListeners(password);
        setListeners(password2);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setListeners(EditText password) {
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

    public void checkDatabase() {
        String usernameDB = username.getText().toString();
        String passwordDB = password.getText().toString();
        String nameDB = name.getText().toString();
        String passwordDB2 = password2.getText().toString();

        if(passwordDB.equals(passwordDB2)) {

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            Query query = database.collection("users")
                    .whereEqualTo("username", usernameDB);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                            if(task.getResult().size()==0){
                                Intent nextIntent = new Intent(RegistrationPage.this, RegistrationPage2.class);
                                nextIntent.putExtra("name",nameDB);
                                nextIntent.putExtra("username",usernameDB);
                                nextIntent.putExtra("password", passwordDB);
                                startActivity(nextIntent);
                            }
                            else{
                                AlertDialog.Builder regDialog = new AlertDialog.Builder(RegistrationPage.this);
                                regDialog.setMessage("Username already exists");
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
        else {
            Toast.makeText(RegistrationPage.this, "Passwords don't match", Toast.LENGTH_LONG).show();
        }
    }

    public int checkValidityOfComponents() {
        if(name.getText().length() == 0) {
            name.setError("Field cannot be left blank");
            errorExists = 1;
        }
        else if(!name.getText().toString().matches(getString(R.string.name))) {
            name.setError("Name should contain only letters");
            errorExists = 1;
        }
        if(username.getText().length() == 0) {
            username.setError("Field cannot be left blank");
            errorExists = 1;
        }
        if(password.getText().toString().length() < 8 || password.getText().toString().length() >20) {
            password.setError("Password length should be between 8 and 20");
            errorExists = 1;
        }
        else if(!password.getText().toString().matches(getString(R.string.password))) {
            password.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter");
            errorExists = 1;
        }
        if(password2.getText().toString().length() < 8 || password2.getText().toString().length() >20) {
            password2.setError("Password length should be between 8 and 20");
            errorExists = 1;
        }
        else if(!password2.getText().toString().matches(getString(R.string.password2))) {
            password2.setError("Password should contain this:\n-At least one lowercase letter\n-At least one digit\n-At least one capital letter");
            errorExists = 1;
        }
        if(!license.isChecked()) {
            license.setError("You should agree our terms");
            errorExists = 1;
        }
        return errorExists;
    }

    public void nextPageTransition(View view) {
        errorExists = 0;
        if(checkValidityOfComponents() == 0)
            checkDatabase();
    }

    public void loginPageTransition(View view) {
        Intent loginIntent = new Intent(this, LoginPage.class);
        startActivity(loginIntent);
    }
}