package com.artisans.qwikhomeservices.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.LayoutMessageReceivedBinding;
import com.artisans.qwikhomeservices.databinding.LayoutMessageSentBinding;
import com.artisans.qwikhomeservices.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static String UID;
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Message.ITEM_TYPE_SENT:
                return new MessageSent(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_message_sent, parent, false));

            case Message.ITEM_TYPE_RECEIVED:
                return new MessageReceived(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_message_received, parent, false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message messages = messageList.get(position);
        if (messages != null) {
            switch (messages.type) {
                case Message.ITEM_TYPE_SENT:
                    ((MessageSent) holder).layoutMessageSentBinding.setMessageSent(messages);

                    break;

                case Message.ITEM_TYPE_RECEIVED:
                    ((MessageReceived) holder).layoutMessageReceivedBinding.setMessageReceived(messages);

                    break;
            }
        }



    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        UID = MainActivity.uid;

        if (messageList.get(position).getSenderId().equals(UID)) {
            return Message.ITEM_TYPE_SENT;
        } else {
            return Message.ITEM_TYPE_RECEIVED;
        }
    }

    static class MessageSent extends RecyclerView.ViewHolder {

        private LayoutMessageSentBinding layoutMessageSentBinding;

        public MessageSent(@NonNull LayoutMessageSentBinding itemView) {
            super(itemView.getRoot());
            this.layoutMessageSentBinding = itemView;
        }
    }

    static class MessageReceived extends RecyclerView.ViewHolder {

        private LayoutMessageReceivedBinding layoutMessageReceivedBinding;

        public MessageReceived(@NonNull LayoutMessageReceivedBinding layoutMessageReceivedBinding) {
            super(layoutMessageReceivedBinding.getRoot());
            this.layoutMessageReceivedBinding = layoutMessageReceivedBinding;
        }
    }
}
