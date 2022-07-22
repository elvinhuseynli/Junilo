package com.example.junilo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<ChatMessage> chatMessages;
    String senderId;
    int VIEW_TYPE_SENT = 1, VIEW_TYPE_RECEIVED=2;

    public ChatAdapter(List<ChatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_container_message, parent, false);
        View contactView1 = inflater.inflate(R.layout.item_container_received_message, parent, false);

        if(viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder sentMessageViewHolder = new SentMessageViewHolder(contactView);
            return sentMessageViewHolder;
        }
        else{
            ReceivedMessageViewHolder receivedMessageViewHolder = new ReceivedMessageViewHolder(contactView1);
            return receivedMessageViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }
        else{
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        }
        return VIEW_TYPE_RECEIVED;
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {

        TextView message, date;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.textDate);
        }

        public void setData(ChatMessage chatMessage) {
            message.setText(chatMessage.message);
            date.setText(chatMessage.dateTime);
        }
    }


    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        TextView message, date;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message1);
            date = (TextView) itemView.findViewById(R.id.textDate1);
        }

        public void setData(ChatMessage chatMessage) {
            message.setText(chatMessage.message);
            date.setText(chatMessage.dateTime);
        }
    }
}
