package com.example.junilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainPage extends AppCompatActivity implements ConversationListener{

    PreferenceManager preferenceManager;
    TextView name, email;
    ImageView image;
    List<ChatMessage> conversations;
    RecentsAdapter recentsAdapter;
    FirebaseFirestore database;
    RecyclerView recyclerView;
    Button addButton;
    String curToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        name = (TextView) findViewById(R.id.textView10);
        Intent startIntent = getIntent();
        Bundle extras = startIntent.getExtras();
        curToken = extras.getString("userId");
        name.setText(extras.getString("name"));
        email = (TextView) findViewById(R.id.textView21);
        email.setText(extras.getString("email"));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView3);
        addButton = (Button) findViewById(R.id.addButton);
        image = (ImageView) findViewById(R.id.imageView);
        getToken();
        initialize();
        setListener();
        listenConversation();
    }

    public void initialize() {
        conversations = new ArrayList<>();
        recentsAdapter = new RecentsAdapter(conversations, this);
        recyclerView.setAdapter(recentsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    public void setListener() {
        image.setOnClickListener(view -> {signOut();});
        addButton.setOnClickListener(view-> {usersPage();});
    }

    @Override
    public void onBackPressed() {
        if (true) {
            return;
        }
        super.onBackPressed();
    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Ignore
                        }
                        String token = task.getResult();
                        updateToken(token);
                    }
                });
    }

    public void listenConversation() {
        database.collection("conversation")
                .whereEqualTo("senderId", curToken)
                .addSnapshotListener(eventListener);

        database.collection("conversation")
                .whereEqualTo("receiverId", curToken)
                .addSnapshotListener(eventListener);
    }

    EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            for(DocumentChange documentChange: value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString("senderId");
                    String receiverId = documentChange.getDocument().getString("receiverId");
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if(curToken.equals(senderId)) {
                        chatMessage.conversationName = documentChange.getDocument().getString("receiverName");
                        chatMessage.conversationId = documentChange.getDocument().getString("receiverId");
                    }
                    else {
                        chatMessage.conversationName = documentChange.getDocument().getString("senderName");
                        chatMessage.conversationId = documentChange.getDocument().getString("senderId");
                    }
                    chatMessage.message = documentChange.getDocument().getString("lastMessage");
                    chatMessage.dateObject = documentChange.getDocument().getDate("timestamp");
                    conversations.add(chatMessage);
                }else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i=0; i<conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString("senderId");
                        String receiverId = documentChange.getDocument().getString("receiverId");
                        if(conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)) {
                            conversations.get(i).message = documentChange.getDocument().getString("lastMessage");
                            conversations.get(i).dateObject = documentChange.getDocument().getDate("timestamp");
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            recentsAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            recyclerView.setVisibility(View.VISIBLE);

        }
    };

    public void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(curToken);
        documentReference.update(Constants.KEY_FCM_TOKEN, token);
    }

    public void usersPage() {
        Intent userIntent = new Intent(this, UsersActivity.class);
        userIntent.putExtra("userId", curToken);
        startActivity(userIntent);
    }

    public void signOut() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection("users")
                .document(curToken);
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    Intent loginIntent = new Intent(MainPage.this, LoginPage.class);
                    startActivity(loginIntent);
                    finish();
                });
    }

    @Override
    public void onConversationClicked(User user) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("user", user);
        chatIntent.putExtra("userId", curToken);
        startActivity(chatIntent);
    }
}