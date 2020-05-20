package com.artisans.qwikhomeservices.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.activities.home.serviceTypes.CommentsActivity;
import com.artisans.qwikhomeservices.databinding.ImageTypeBinding;
import com.artisans.qwikhomeservices.databinding.TextTypeBinding;
import com.artisans.qwikhomeservices.models.ActivityItemModel;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.DoubleClickListener;
import com.artisans.qwikhomeservices.utils.GetTimeAgo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;


public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String uid = MainActivity.uid;
    private ArrayList<ActivityItemModel> dataSet;
    private Context mContext;


    public MultiViewTypeAdapter(ArrayList<ActivityItemModel> data, Context context) {
        this.dataSet = data;
        this.mContext = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case ActivityItemModel.TEXT_TYPE:

                return new TextTypeViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.text_type, parent, false));

            case ActivityItemModel.IMAGE_TYPE:

                return new ImageTypeViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_type, parent, false));

        }
        return null;


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int listPosition) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        ActivityItemModel object = dataSet.get(listPosition);
        if (object != null) {

            switch (object.type) {
                case ActivityItemModel.TEXT_TYPE:

                    //bind data in xml
                    ((TextTypeViewHolder) holder).textTypeBinding.setTextType(object);
                    //show time
                    ((TextTypeViewHolder) holder).textTypeBinding.txtTime.setText(GetTimeAgo.getTimeAgo(object.getTimeStamp()));
                    //load users images into views
                    Glide.with(((TextTypeViewHolder) holder).textTypeBinding.getRoot().getContext())
                            .load(object.getUserPhoto())
                            .thumbnail(0.5f)
                            .error(((TextTypeViewHolder) holder).textTypeBinding.getRoot().getResources().getDrawable(R.drawable.photoe))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(((TextTypeViewHolder) holder).textTypeBinding.imgUserPhoto);

                    ((TextTypeViewHolder) holder).isLiked(object.getId(), ((TextTypeViewHolder) holder).imgBtnLike);

                    ((TextTypeViewHolder) holder).numOfLikes(((TextTypeViewHolder) holder).txtLikes, object.getId());

                    ((TextTypeViewHolder) holder).imgBtnLike.setOnClickListener(v -> {
                        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Likes")
                                .child(object.getId())
                                .child(uid);

                        if (((TextTypeViewHolder) holder).imgBtnLike.getTag().equals("like")) {

                            dbRef.setValue(true);

                        } else {

                            dbRef.removeValue();
                        }
                    });


                    break;
                case ActivityItemModel.IMAGE_TYPE:
                    //bind data in xml
                    ((ImageTypeViewHolder) holder).imageTypeBinding.setImageType(object);
                    ((ImageTypeViewHolder) holder).imageTypeBinding.txtTime.setText(GetTimeAgo.getTimeAgo(object.getTimeStamp()));
                    //load user photo
                    Glide.with(((ImageTypeViewHolder) holder).imageTypeBinding.getRoot().getContext())
                            .load(object.getUserPhoto())
                            .thumbnail(0.5f)
                            .error(((ImageTypeViewHolder) holder).imageTypeBinding.getRoot().getResources().getDrawable(R.drawable.photoe))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(((ImageTypeViewHolder) holder).imageTypeBinding.imgUserPhoto);

                    //load images
                    Glide.with(((ImageTypeViewHolder) holder).imageTypeBinding.getRoot().getContext())
                            .load(object.getItemImage())
                            .thumbnail(0.5f)
                            .apply(requestOptions)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    if (isFirstResource) {
                                        ((ImageTypeViewHolder) holder).imageTypeBinding.progressBar.setVisibility(View.INVISIBLE);

                                    }
                                    ((ImageTypeViewHolder) holder).imageTypeBinding.progressBar.setVisibility(View.VISIBLE);

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ImageTypeViewHolder) holder).imageTypeBinding.progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).transition(DrawableTransitionOptions.withCrossFade()).optionalCenterCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((((ImageTypeViewHolder) holder).imageTypeBinding.imgContentPhoto));

                    ((ImageTypeViewHolder) holder).isLiked(object.getId(), ((ImageTypeViewHolder) holder).imgBtnLike);

                    ((ImageTypeViewHolder) holder).numOfLikes(((ImageTypeViewHolder) holder).txtLikes, object.getId());

                    ((ImageTypeViewHolder) holder).numOfComments(((ImageTypeViewHolder) holder).txtComments, object.getId());

                    ((ImageTypeViewHolder) holder).txtComments.setOnClickListener(view -> {

                        Intent commentsIntent = new Intent(view.getContext(), CommentsActivity.class);
                        commentsIntent.putExtra("postId", object.getId());
                        commentsIntent.putExtra("itemImage", object.getItemImage());
                        commentsIntent.putExtra("itemDescription", object.getItemDescription());

                        view.getContext().startActivity(commentsIntent);


                    });

                    ((ImageTypeViewHolder) holder).imageView.setOnClickListener(new DoubleClickListener() {
                        @Override
                        public void onDoubleClick(View view) {
                            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("Likes")
                                    .child(object.getId())
                                    .child(uid);

                            if (((ImageTypeViewHolder) holder).imgBtnLike.getTag().equals("like")) {

                                dbRef.setValue(true);

                            } else {

                                dbRef.removeValue();
                            }
                        }
                    });


                    break;

            }
        }

    }

    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return ActivityItemModel.TEXT_TYPE;
            case 1:
                return ActivityItemModel.IMAGE_TYPE;
            case 2:
                return ActivityItemModel.AUDIO_TYPE;
            default:
                return -1;
        }


    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    //view holder for text
    static class TextTypeViewHolder extends RecyclerView.ViewHolder {
        TextTypeBinding textTypeBinding;
        ImageView imgBtnLike;
        TextView txtLikes;


        TextTypeViewHolder(@NonNull TextTypeBinding textTypeBinding) {
            super(textTypeBinding.getRoot());
            this.textTypeBinding = textTypeBinding;
            imgBtnLike = textTypeBinding.imgBtnLike;
            txtLikes = textTypeBinding.txtLikes;

        }


        /***
         * method to check if an item is liked or not
         * @param imgBtnLike image to switch between item liked by changing image item
         * @param postId the post id for the item in the view holder
         * **/
        private void isLiked(String postId, ImageView imgBtnLike) {
            String uid = MainActivity.uid;

            DatabaseReference likesDbRef = FirebaseDatabase.getInstance()
                    .getReference().child("Likes").child(postId);


            likesDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(uid).exists()) {

                        imgBtnLike.setImageResource(R.drawable.ic_favorite_red);
                        imgBtnLike.setTag("liked");
                    } else {
                        imgBtnLike.setImageResource(R.drawable.ic_favorite);
                        imgBtnLike.setTag("like");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        /***
         * method to display the number of likes
         * @param txtIsLiked TextView to hold the counter
         * @param postId the post id for the item in the view holder
         * **/
        private void numOfLikes(TextView txtIsLiked, String postId) {
            DatabaseReference likesDbRef = FirebaseDatabase.getInstance()
                    .getReference().child("Likes").child(postId);

            likesDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    txtIsLiked.setText(MessageFormat.format("{0} likes", dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

    //view holder for images
    static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        ImageTypeBinding imageTypeBinding;
        ImageView imageView, imgBtnLike;
        TextView txtLikes, txtComments;

        ImageTypeViewHolder(@NonNull ImageTypeBinding imageTypeBinding) {
            super(imageTypeBinding.getRoot());
            this.imageTypeBinding = imageTypeBinding;
            imageView = imageTypeBinding.imgContentPhoto;
            txtLikes = imageTypeBinding.txtLikes;
            imgBtnLike = imageTypeBinding.imgBtnLike;
            txtComments = imageTypeBinding.txtComments;


        }

        /***
         * method to check if an item is liked or not
         * @param imgBtnLike image to switch between item liked by changing image item
         * @param postId the post id for the item in the view holder
         * **/
        private void isLiked(String postId, ImageView imgBtnLike) {
            String uid = MainActivity.uid;

            DatabaseReference likesDbRef = FirebaseDatabase.getInstance()
                    .getReference().child("Likes").child(postId);


            likesDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(uid).exists()) {

                        imgBtnLike.setImageResource(R.drawable.ic_favorite_red);
                        imgBtnLike.setTag("liked");
                    } else {
                        imgBtnLike.setImageResource(R.drawable.ic_favorite);
                        imgBtnLike.setTag("like");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        /***
         * method to display the number of likes
         * @param txtIsLiked TextView to hold the counter
         * @param postId the post id for the item in the view holder
         * **/
        private void numOfLikes(TextView txtIsLiked, String postId) {
            DatabaseReference likesDbRef = FirebaseDatabase.getInstance()
                    .getReference().child("Likes").child(postId);

            likesDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    txtIsLiked.setText(MessageFormat.format("{0} likes", dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        private void numOfComments(TextView txtComments, String postId) {
            DatabaseReference likesDbRef = FirebaseDatabase.getInstance()
                    .getReference().child("Comments").child(postId);

            likesDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    txtComments.setText(MessageFormat.format("{0} Comments", dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }


}
