package com.example.junilo;

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

import com.hbb20.CountryCodePicker;
import java.util.Calendar;

public class RegistrationPage extends AppCompatActivity {

    EditText name, username, email, password, password2, phoneNumber;
    Calendar calendar;
    TextView dateView;
    CheckBox license;
    Database database;
    CountryCodePicker ccp;
    public Boolean passwordVisible = false, passwordVisible2 = false;
    int year, month, day, errorExists = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        Intent startIntent = getIntent();

        name = (EditText) findViewById(R.id.editTextTextPersonName);
        username = (EditText) findViewById(R.id.editTextTextPersonName2);
        email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        password2 = (EditText) findViewById(R.id.editTextTextPassword3);
        phoneNumber = (EditText) findViewById(R.id.editTextPhone);
        ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        license = (CheckBox) findViewById(R.id.checkBox);

        database = new Database(this);

        dateView = (TextView) findViewById(R.id.textView8);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

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

        password2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= password2.getRight()-password2.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = password2.getSelectionEnd();
                        if(passwordVisible2) {
                            password2.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                            password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible2 = false;
                        }
                        else {
                            password2.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                            password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible2 = true;
                        }
                        password2.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    public int checkDatabase() {
        String usernameDB = username.getText().toString();
        String passwordDB = password.getText().toString();
        String emailDB = email.getText().toString();
        String dobDB = day + "/" + month + "/" + year;
        String phoneDB = ccp.getSelectedCountryCodeWithPlus() + phoneNumber.getText().toString();
        String nameDB = name.getText().toString();
        String passwordDB2 = password2.getText().toString();

        if(passwordDB.equals(passwordDB2)) {
            Boolean checkUsername = database.checkUsername(usernameDB);
            Boolean checkEmail = database.checkEmailAddress(emailDB);
            Boolean checkPhone = database.checkPhoneNumber(phoneDB);
            if(!(checkUsername||checkEmail||checkPhone)){
                Boolean user = database.insertData(nameDB, usernameDB, emailDB, phoneDB, dobDB, passwordDB);
                if(user)
                    return 0;
            }
            else{
                AlertDialog.Builder regDialog = new AlertDialog.Builder(RegistrationPage.this);
                regDialog.setMessage("Username, email or phone number already exist");
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
                return 1;
            }
        }
        Toast.makeText(RegistrationPage.this, "Passwords don't match", Toast.LENGTH_LONG).show();
        return 1;
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
        if(!email.getText().toString().matches(getString(R.string.email))) {
            email.setError("Email format is invalid");
            errorExists = 1;
        }
        if(phoneNumber.getText().toString().length() == 0){
            phoneNumber.setError("Phone format is invalid");
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

    public void loginPageTransition(View view) {
        Intent loginIntent = new Intent(this, LoginPage.class);
        errorExists = 0;
        if(checkValidityOfComponents() == 0 && checkDatabase() == 0)
            startActivity(loginIntent);
    }
}