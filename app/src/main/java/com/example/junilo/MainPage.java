package com.example.junilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

public class MainPage extends AppCompatActivity {

    User user;
    String name, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Intent startIntent = getIntent();
        name = startIntent.getStringExtra("name");
        username = startIntent.getStringExtra("username");

        AppSettings appSettings= new AppSettings.AppSettingsBuilder()
                .subscribePresenceForAllUsers()
                .setRegion(Constants.REGION)
                .autoEstablishSocketConnection(true)
                .build();

        initializeCometChat(appSettings);
    }

    public void initializeCometChat(AppSettings appSettings) {

        CometChat.init(this, Constants.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                createAndLoginUser(username, name);
            }
            @Override
            public void onError(CometChatException e) {
                //Raise error
            }
        });
    }
}