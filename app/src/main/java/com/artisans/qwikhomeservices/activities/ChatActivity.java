package com.artisans.qwikhomeservices.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.adapters.MessageAdapter;
import com.artisans.qwikhomeservices.models.Message;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {

    EmojiconEditText emojiconEditText;
    ImageView emojiButton;
    EmojIconActions emojIconActions;
    private CircleImageView handyManPhoto;
    private TextView txtName, txtContent;
    private TextInputLayout edtComment;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    // private ChatAdapter adapter;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private DatabaseReference chatsDbRef;
    private String userId, servicePersonName, servicePersonPhoto, senderName, senderPhoto, reason, getAdapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.chatToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btnReplyBack).setOnClickListener(v -> addChat());

        initViews();
        setUpRecycler();

    }

    private void initViews() {

        messageList = new ArrayList<>();

        Intent getDataIntent = getIntent();
        if (getDataIntent != null) {
            //get data from the view holder
            //servicePersonPhoto = getIntent().getStringExtra("servicePersonPhoto");//itemImage
            getAdapterPosition = getIntent().getStringExtra("adapterPosition");//adapter position of the item
            servicePersonName = getIntent().getStringExtra("servicePersonName");//name of handyMan
            //  reason = getIntent().getStringExtra("senderReason");//content of the report
            senderName = getIntent().getStringExtra("senderName");//name of sender
            senderPhoto = getIntent().getStringExtra("senderPhoto");//sender photo
            userId = getIntent().getStringExtra("senderID");//sender id



        }


        handyManPhoto = findViewById(R.id.imgHandyManPhoto);
        txtName = findViewById(R.id.txtHandyManName);

        ConstraintLayout activity_main = findViewById(R.id.activity_main);
        emojiButton = findViewById(R.id.emoticonButton);
        emojiconEditText = findViewById(R.id.emoticonEditTxt);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_main, emojiButton, emojiconEditText);
        emojIconActions.ShowEmojicon();

        txtName.setText(senderName);
        Glide.with(this).load(senderPhoto).into(handyManPhoto);

        chatsDbRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(getAdapterPosition);


    }

    private void setUpRecycler() {

        recyclerView = findViewById(R.id.recyclerViewChats);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);


        DatabaseReference postChatsDbRef = chatsDbRef.child("Chats");
        chatsDbRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = postChatsDbRef.orderByChild("messageDateTime");

        runOnUiThread(() -> query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // messageList.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Message message = ds.getValue(Message.class);
                        messageList.add(message);
                    }
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));



    }

    private void addChat() {

        String postChat = emojiconEditText.getText().toString();
            Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM HH:mm", Locale.ENGLISH);

            String dateTime = simpleDateFormat.format(calendar.getTime());

        if (!postChat.trim().isEmpty()) {
                HashMap<String, Object> chats = new HashMap<>();
                chats.put("message", postChat);
                chats.put("senderId", MainActivity.uid);
                chats.put("senderName", servicePersonName);
                chats.put("senderPhoto", MainActivity.imageUrl);
                chats.put("messageDateTime", dateTime);
                chats.put("receiverName", senderName);
                chats.put("receiverId", userId);

                String chatId = chatsDbRef.push().getKey();
                assert chatId != null;

            chatsDbRef.child("Chats").child(chatId).setValue(chats);
            emojiconEditText.getText().clear();

        } else if (postChat.trim().isEmpty()) {
            emojiconEditText.setError("Cannot send empty message");
                //  makeToast("Comment cannot be empty");
            }

    }



    @Override
    protected void onStart() {
        super.onStart();
        //  adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  adapter.stopListening();
    }

}
