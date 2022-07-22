package com.example.junilo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    User receiverUser;
    List<ChatMessage> chatMessageList;
    ChatAdapter chatAdapter;
    String userId, conversationId = null;
    AppCompatImageView image;
    EditText inputMessage;
    RecyclerView recyclerView;
    PreferenceManager preferenceManager;
    FirebaseFirestore database;
    ImageView backButton;
    TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent startIntent = getIntent();
        Bundle extras = startIntent.getExtras();
        receiverUser = (User) extras.getSerializable("user");

        backButton = (ImageView) findViewById(R.id.imageView11);
        database = FirebaseFirestore.getInstance();
        userId = extras.getString("userId");
        nameView = (TextView) findViewById(R.id.textView20);
        nameView.setText(receiverUser.name);
        image = (AppCompatImageView) findViewById(R.id.appCompatImageView);
        inputMessage = (EditText) findViewById(R.id.editTextTextPersonName4);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        setListener();
        initializeView();
        messageListener();
    }

    public void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put("senderId", userId);
        message.put("receiverId", receiverUser.id);
        message.put("message", inputMessage.getText().toString());
        message.put("timestamp", new Date());
        database.collection("chat").add(message);
        if(conversationId != null) {
            updateConversation(inputMessage.getText().toString());
        }
        else{
            HashMap<String, Object> mConversation = new HashMap<>();
            mConversation.put("senderId", userId);
            mConversation.put("receiverId", receiverUser.id);
            mConversation.put("senderName", preferenceManager.getString("name"));
            mConversation.put("receiverName", receiverUser.name);
            mConversation.put("lastMessage", inputMessage.getText().toString());
            mConversation.put("timestamp", new Date());
            addConversation(mConversation);
        }
        inputMessage.setText(null);
    }

    public void messageListener() {
        database.collection("chat")
                .whereEqualTo("senderId", userId)
                .whereEqualTo("receiverId", receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection("chat")
                .whereEqualTo("senderId", receiverUser.id)
                .whereEqualTo("receiverId", userId)
                .addSnapshotListener(eventListener);
    }

    EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error!=null) {
            return;
        }
        if(value!=null) {
            int count = chatMessageList.size();
            for(DocumentChange documentChange: value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString("senderId");
                    chatMessage.receiverId = documentChange.getDocument().getString("receiverId");
                    chatMessage.message = documentChange.getDocument().getString("message");
                    chatMessage.dateObject = documentChange.getDocument().getDate("timestamp");
                    chatMessage.dateTime = getDateTime(documentChange.getDocument().getDate("timestamp"));
                    chatMessageList.add(chatMessage);
                }
            }
            Collections.sort(chatMessageList, (obj1, obj2) ->obj1.dateObject.compareTo(obj2.dateObject));
            if(count == 0) {
                chatAdapter.notifyDataSetChanged();
            }else {
                chatAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());
                recyclerView.smoothScrollToPosition(chatMessageList.size()-1);
            }
            recyclerView.setVisibility(View.VISIBLE);
        }
        if(conversationId == null) {
            checkConversation();
        }
    };

    public void setListener() {
        backButton.setOnClickListener(v->{onBackPressed();});
        image.setOnClickListener(v->{sendMessage();});
    }

    public void initializeView() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessageList, userId);
        recyclerView.setAdapter(chatAdapter);
    }

    public String getDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    public void addConversation(HashMap<String, Object> conversation) {
        database.collection("conversation")
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    public void updateConversation(String message) {
        DocumentReference documentReference = database.collection("conversation")
                .document(conversationId);
        documentReference.update("lastMessage", message, "timestamp", new Date());
    }

    public void checkConversation() {
        if(chatMessageList.size()!=0) {
            checkConversationRemotely(userId, receiverUser.id);
            checkConversationRemotely(receiverUser.id, userId);
        }
    }

    public void checkConversationRemotely(String senderId, String receiverId) {
        database.collection("conversation")
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", receiverId)
                .get().addOnCompleteListener(conversationOnCompleteListener);
    }

    public OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };
}