package com.example.junilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Intent startIntent = getIntent();
    }

    public void phoneVerification(View view) {
        Intent phoneIntent = new Intent(this, PhoneVerification.class);
        startActivity(phoneIntent);
    }

    public void emailVerification(View view) {
        Intent emailIntent = new Intent(this, EmailVerification.class);
        startActivity(emailIntent);
    }
}