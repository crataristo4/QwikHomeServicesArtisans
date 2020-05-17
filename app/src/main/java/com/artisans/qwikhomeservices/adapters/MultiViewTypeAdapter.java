package com.artisans.qwikhomeservices.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.ImageTypeBinding;
import com.artisans.qwikhomeservices.databinding.TextTypeBinding;
import com.artisans.qwikhomeservices.models.ActivityItemModel;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.GetTimeAgo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;


public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

        TextTypeViewHolder(@NonNull TextTypeBinding textTypeBinding) {
            super(textTypeBinding.getRoot());
            this.textTypeBinding = textTypeBinding;

        }

    }

    //view holder for images
    static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        ImageTypeBinding imageTypeBinding;

        ImageTypeViewHolder(@NonNull ImageTypeBinding imageTypeBinding) {
            super(imageTypeBinding.getRoot());
            this.imageTypeBinding = imageTypeBinding;


        }

    }


}
