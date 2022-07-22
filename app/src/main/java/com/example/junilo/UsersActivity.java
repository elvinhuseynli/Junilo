package com.example.junilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener{

    String userId;
    ImageView image;
    TextView error;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Intent startIntent = getIntent();
        Bundle extras = startIntent.getExtras();
        userId = extras.getString("userId");
        image = (ImageView) findViewById(R.id.imageView6);
        error = (TextView) findViewById(R.id.errorMessage);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        setListener();
        getUsers();
    }

    public void setListener() {
        image.setOnClickListener(view->{onBackPressed();});
    }

    public void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().size()!=0) {
                                List<User> users = new ArrayList<>();
                                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                                    if(userId.equals(queryDocumentSnapshot.getId())) {
                                        continue;
                                    }
                                    else{
                                        User user = new User();
                                        user.name = queryDocumentSnapshot.getString("name");
                                        user.email = queryDocumentSnapshot.getString("email");
                                        user.fcmToken = queryDocumentSnapshot.getString("fcmToken");
                                        user.id = queryDocumentSnapshot.getId();
                                        users.add(user);
                                    }
                                }
                                if(users.size()>0) {
                                    UsersAdapter usersAdapter = new UsersAdapter(users, UsersActivity.this);
                                    recyclerView.setAdapter(usersAdapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                                else{
                                    error.setText("No users available to show");
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onUserClicked(User user) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("user", user);
        chatIntent.putExtra("userId", userId);
        startActivity(chatIntent);
    }
}