package com.artisans.qwikhomeservices.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView handyManPhoto;
    private TextView txtName, txtContent;
    private TextInputLayout edtComment;
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
            servicePersonPhoto = getIntent().getStringExtra("servicePersonPhoto");//itemImage
            getAdapterPosition = getIntent().getStringExtra("adapterPosition");//adapter position of the item
            servicePersonName = getIntent().getStringExtra("servicePersonName");//name of handyMan
            reason = getIntent().getStringExtra("senderReason");//content of the report
            senderName = getIntent().getStringExtra("senderName");//name of sender
            senderPhoto = getIntent().getStringExtra("senderPhoto");//sender photo
            userId = getIntent().getStringExtra("senderID");//sender id



        }


        handyManPhoto = findViewById(R.id.imgHandyManPhoto);
        txtName = findViewById(R.id.txtHandyManName);
        txtContent = findViewById(R.id.txtShowReason);
        edtComment = findViewById(R.id.edtChatMsg);

        txtName.setText(senderName);
        txtContent.setText(reason);
        Glide.with(this).load(senderPhoto).into(handyManPhoto);


        chatsDbRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(getAdapterPosition);


    }

    private void setUpRecycler() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewChats);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messageList);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        DatabaseReference postChatsDbRef = chatsDbRef.child("Chats");
        chatsDbRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = postChatsDbRef.orderByChild("timeStamp");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Message message = ds.getValue(Message.class);
                        messageList.add(message);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       /* FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>().
                setQuery(query, Chat.class).build();

        adapter = new ChatAdapter(options);*/



    }

    private void addChat() {

        String postChat = edtComment.getEditText().getText().toString();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM HH:mm");

        String dateTime = simpleDateFormat.format(calendar.getTime());

        if (!postChat.isEmpty()) {
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

            chatsDbRef.child("Chats").child(chatId).setValue(chats).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    makeToast("Successfully sent");
                    edtComment.getEditText().setText("");
                }

                // setUpRecycler();


            }).addOnFailureListener(e -> {
                makeToast("Error: " + e.getMessage());
                //edtComment.getEditText().setText("");
            });
        } else {
            edtComment.setError("Cannot send empty message");
            //  makeToast("Comment cannot be empty");
        }

    }


    void makeToast(String s) {
        Toast toast = Toast.makeText(ChatActivity.this, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
