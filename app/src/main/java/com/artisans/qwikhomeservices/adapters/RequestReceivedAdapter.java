package com.artisans.qwikhomeservices.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.ChatActivity;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.AcceptOrRejectBtSheet;
import com.artisans.qwikhomeservices.databinding.LayoutRequestReceivedBinding;
import com.artisans.qwikhomeservices.models.RequestModel;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RequestReceivedAdapter extends FirebaseRecyclerAdapter<RequestModel, RequestReceivedAdapter.RequestReceivedAdapterViewHolder> {
    private FragmentManager fragmentManager;

    public RequestReceivedAdapter(@NonNull FirebaseRecyclerOptions<RequestModel> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestReceivedAdapterViewHolder requestReceivedAdapterViewHolder,
                                    int i, @NonNull RequestModel requestModel) {

        requestReceivedAdapterViewHolder.layoutRequestReceivedBinding.setRequest(requestModel);
        requestReceivedAdapterViewHolder.showWorkDoneStatus(requestModel.isWorkDone());
        requestReceivedAdapterViewHolder.showResponse(requestModel.getResponse());
        requestReceivedAdapterViewHolder.showRating(requestModel.getRating());

        //check if request is accepted and allow service user to chat with user
        if (requestModel.getResponse().equals("Request Accepted")) {

            requestReceivedAdapterViewHolder.btnChat.setVisibility(View.VISIBLE);
            requestReceivedAdapterViewHolder.btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(requestReceivedAdapterViewHolder
                            .layoutRequestReceivedBinding
                            .getRoot()
                            .getContext(), ChatActivity.class);
                    //pass users data
                    String adapterPosition = getRef(i).getKey();
                    chatIntent.putExtra("senderName", requestModel.getSenderName());
                    chatIntent.putExtra("senderPhoto", requestModel.getSenderPhoto());
                    chatIntent.putExtra("senderID", requestModel.getSenderId());
                    chatIntent.putExtra("senderReason", requestModel.getReason());
                    chatIntent.putExtra("adapterPosition", adapterPosition);
                    chatIntent.putExtra("servicePersonName", requestModel.getServicePersonName());
                    chatIntent.putExtra("servicePersonPhoto", requestModel.getSenderPhoto());


                    requestReceivedAdapterViewHolder
                            .layoutRequestReceivedBinding
                            .getRoot()
                            .getContext()
                            .startActivity(chatIntent);
                }
            });
        } else {
            requestReceivedAdapterViewHolder.btnChat.setVisibility(View.GONE);

        }


        final String getAdapterPosition = getRef(i).getKey();
        requestReceivedAdapterViewHolder.btnView.setOnClickListener(v -> {
            AcceptOrRejectBtSheet acceptOrRejectBtSheet = new AcceptOrRejectBtSheet();
            Bundle bundle = new Bundle();
            bundle.putString(MyConstants.ADAPTER_POSITION, getAdapterPosition);
            bundle.putString(MyConstants.FULL_NAME, requestModel.getSenderName());
            bundle.putString(MyConstants.USER_IMAGE_URL, requestModel.getSenderPhoto());
            bundle.putString(MyConstants.ITEM_NAME, requestModel.getItemName());
            bundle.putString(MyConstants.ITEM_PHOTO, requestModel.getItemImage());
            bundle.putString(MyConstants.ITEM_PRICE, requestModel.getPrice());
            bundle.putString(MyConstants.ITEM_REASON, requestModel.getReason());
            acceptOrRejectBtSheet.setArguments(bundle);
            acceptOrRejectBtSheet.show(fragmentManager, "show");
        });

    }

    @NonNull
    @Override
    public RequestReceivedAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRequestReceivedBinding layoutRequestReceivedBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.layout_request_received, parent, false);

        return new RequestReceivedAdapterViewHolder(layoutRequestReceivedBinding);

    }

    //an inner class to hold the views to be inflated
    public static class RequestReceivedAdapterViewHolder extends RecyclerView.ViewHolder {

        private ImageButton btnView, btnChat;
        private RatingBar ratingBar;
        private TextView txtWorkDone, txtResponse;

        private LayoutRequestReceivedBinding layoutRequestReceivedBinding;

        public RequestReceivedAdapterViewHolder(@NonNull LayoutRequestReceivedBinding layoutRequestReceivedBinding) {
            super(layoutRequestReceivedBinding.getRoot());
            this.layoutRequestReceivedBinding = layoutRequestReceivedBinding;
            btnView = layoutRequestReceivedBinding.btnView;
            btnChat = layoutRequestReceivedBinding.btnChat;
            ratingBar = layoutRequestReceivedBinding.ratedResults;
            txtWorkDone = layoutRequestReceivedBinding.txtConfirmWorkDone;
            txtResponse = layoutRequestReceivedBinding.txtResponse;
        }

        //display the rating
        void showRating(float rating) {
            if (!String.valueOf(rating).isEmpty() && rating > 0) {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(rating);


            } else if (rating == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
            }

        }

        void showWorkDoneStatus(String isWorkDone) {
            if (isWorkDone.equals("YES")) {
                // btnChat.setVisibility(View.VISIBLE);
                txtWorkDone.setTextColor(layoutRequestReceivedBinding.getRoot().getResources().getColor(R.color.colorGreen));
                txtWorkDone.setText(R.string.wkDone);
                txtWorkDone.setVisibility(View.VISIBLE);


            } else if (isWorkDone.equals("NO")) {
                txtWorkDone.setText(R.string.wkNtDone);
                txtWorkDone.setVisibility(View.VISIBLE);
                txtWorkDone.setTextColor(layoutRequestReceivedBinding.getRoot().getResources().getColor(R.color.colorRed));
                //btnChat.setVisibility(View.GONE);

            }

        }

        //display the response details
        void showResponse(String response) {

            //allow service person to chat user when request is accepted
            if (response.equals("Request Accepted")) {
                btnChat.setVisibility(View.VISIBLE);
                txtResponse.setTextColor(layoutRequestReceivedBinding.getRoot().getResources().getColor(R.color.colorGreen));
                txtResponse.setText(response);


            } else if (response.equals("Request Rejected")) {
                btnChat.setVisibility(View.GONE);
                txtWorkDone.setText(R.string.wkNtDone);
                txtWorkDone.setVisibility(View.VISIBLE);
                txtWorkDone.setTextColor(layoutRequestReceivedBinding.getRoot().getResources().getColor(R.color.colorRed));
                txtResponse.setTextColor(layoutRequestReceivedBinding.getRoot().getResources().getColor(R.color.colorRed));
                txtResponse.setText(response);

            }


        }



    }
}
