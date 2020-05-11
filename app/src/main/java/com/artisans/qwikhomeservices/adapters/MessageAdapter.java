package com.artisans.qwikhomeservices.adapters;

import android.content.Context;
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
    public static final int ITEM_TYPE_SENT = 0;
    public static final int ITEM_TYPE_RECEIVED = 1;
    public static String UID;
    private List<Message> messageList;
    private Context context;

    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_SENT:
                return new MessageFromSenderViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_message_sent, parent, false));

            case ITEM_TYPE_RECEIVED:
                return new MessageToReceiverViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_message_received, parent, false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        UID = MainActivity.uid;

        if (messageList.get(position).getSenderId().equals(UID)) {
            return ITEM_TYPE_SENT;
        } else {
            return ITEM_TYPE_RECEIVED;
        }
    }

    static class MessageFromSenderViewHolder extends RecyclerView.ViewHolder {

        private LayoutMessageSentBinding layoutMessageSentBinding;

        public MessageFromSenderViewHolder(@NonNull LayoutMessageSentBinding itemView) {
            super(itemView.getRoot());
            this.layoutMessageSentBinding = itemView;
        }
    }

    static class MessageToReceiverViewHolder extends RecyclerView.ViewHolder {

        private LayoutMessageReceivedBinding layoutMessageReceivedBinding;

        public MessageToReceiverViewHolder(@NonNull LayoutMessageReceivedBinding layoutMessageReceivedBinding) {
            super(layoutMessageReceivedBinding.getRoot());
            this.layoutMessageReceivedBinding = layoutMessageReceivedBinding;
        }
    }
}
