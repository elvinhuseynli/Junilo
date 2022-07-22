package com.example.junilo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.ConversationViewHolder>{

    List<ChatMessage> chatMessages;
    ConversationListener conversationListener;

    public RecentsAdapter(List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_container_recents, parent, false);
        ConversationViewHolder conversationViewHolder = new ConversationViewHolder(contactView);
        return conversationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        TextView name, lastMessage;
        ConstraintLayout layout;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.userName);
            lastMessage = (TextView) itemView.findViewById(R.id.recent_message);
            layout = (ConstraintLayout) itemView.findViewById(R.id.layout1);
        }

        public void setData(ChatMessage chatMessage) {
            name.setText(chatMessage.conversationName);
            lastMessage.setText(chatMessage.message);
            layout.setOnClickListener(v->{
                User user = new User();
                user.id = chatMessage.conversationId;
                user.name = chatMessage.conversationName;
                conversationListener.onConversationClicked(user);
            });
        }
    }
}
