package com.artisans.qwikhomeservices.activities.home.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.ActivityDesignStyleBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.GetTimeAgo;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddDesignOrStyleActivity extends AppCompatActivity {

    private static final String TAG = "JobTypesActivity";
    private ActivityDesignStyleBinding activityDesignStyleBinding;
    private AppCompatImageView styleItemPhoto;
    private TextInputLayout txtStyleName, txtPrice;
    private StorageReference mStorageReference;
    private Uri uri;
    private DatabaseReference serviceTypeDbRef, activityDbRef;
    private String price;
    private String uid, style, getImageUploadUri, accountType, userImage, userName;
    private long mBackPressed;
    private CollectionReference dbReference;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState != null) {
            Objects.requireNonNull(txtStyleName.getEditText()).setText(savedInstanceState.getString(MyConstants.STYLE));
            Objects.requireNonNull(txtPrice.getEditText()).setText(savedInstanceState.getString(MyConstants.PRICE));
            Glide.with(AddDesignOrStyleActivity.this)
                    .load((Uri) savedInstanceState.getParcelable(MyConstants.IMAGE_URL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(styleItemPhoto);

        }

        super.onCreate(savedInstanceState);

        activityDesignStyleBinding = DataBindingUtil.setContentView(this, R.layout.activity_design_style);
        Toolbar toolbar = activityDesignStyleBinding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mStorageReference = FirebaseStorage.getInstance().getReference("photos");
        dbReference = FirebaseFirestore.getInstance().collection("Activity");
        id = dbReference.document().getId();

        serviceTypeDbRef = FirebaseDatabase.getInstance()
                .getReference("Styles");

        activityDbRef = FirebaseDatabase.getInstance()
                .getReference("Activity");


        intViews();

    }

    private void intViews() {

        activityDesignStyleBinding.txtDes.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blinking_text));

        txtPrice = activityDesignStyleBinding.textInputLayoutPrice;
        txtStyleName = activityDesignStyleBinding.txtInputLayoutStyle;
        styleItemPhoto = activityDesignStyleBinding.imgStylePhoto;

        uid = MainActivity.uid;

        activityDesignStyleBinding.imgStylePhoto.setOnClickListener(v -> DisplayViewUI.openGallery(this));

        activityDesignStyleBinding.btnAdd.setOnClickListener(this::validateInputs);


    }

    private void validateInputs(View v) {
        style = Objects.requireNonNull(txtStyleName.getEditText()).getText().toString();
        price = String.valueOf(Objects.requireNonNull(txtPrice.getEditText()).getText());

        if (style.trim().isEmpty()) {
            txtStyleName.setErrorEnabled(true);
            txtStyleName.setError("must include a style");
        } else {
            txtStyleName.setErrorEnabled(false);
        }
        if (price.trim().isEmpty()) {
            txtPrice.setErrorEnabled(true);
            txtPrice.setError("invalid price");
        } else {
            txtPrice.setErrorEnabled(false);
        }

        if (uri == null) {
            DisplayViewUI.displayToast(this, "Please select a photo to upload");

        }


        if (!style.trim().isEmpty() && uri != null
                && !price.trim().isEmpty()) {
            uploadFile();

        }
    }

    private void uploadFile() {
        if (uri != null) {
            ProgressDialog progressDialog = DisplayViewUI.displayProgress(this, "adding item please wait...");
            progressDialog.show();

            // file path for the itemImage
            final StorageReference fileReference = mStorageReference.child(uid + "." + uri.getLastPathSegment());

            fileReference.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    DisplayViewUI.displayToast(AddDesignOrStyleActivity.this, Objects.requireNonNull(task.getException()).getMessage());

                }
                return fileReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Uri downLoadUri = task.getResult();
                    assert downLoadUri != null;
                    getImageUploadUri = downLoadUri.toString();

                    String documentId = UUID.randomUUID().toString();

                    Map<String, Object> itemsMap = new HashMap<>();
                    itemsMap.put("price", price);
                    itemsMap.put("itemDescription", style);
                    itemsMap.put("itemImage", getImageUploadUri);
                    itemsMap.put("userPhoto", userImage);
                    itemsMap.put("userName", userName);
                    itemsMap.put("timeStamp", GetTimeAgo.getTimeInMillis());
                    itemsMap.put("accountType", accountType);


                    String randomUID = serviceTypeDbRef.push().getKey();
                    assert randomUID != null;

                    //fire store cloud store
                    dbReference.add(itemsMap).addOnCompleteListener(task2 -> {

                        if (task2.isSuccessful()) {
                            serviceTypeDbRef.child(uid).child(randomUID).setValue(itemsMap);

                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, "Successful");

                            startActivity(new Intent(AddDesignOrStyleActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();


                        } else {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, Objects.requireNonNull(task2.getException()).getMessage());

                        }

                    });

                    /*  StylesItemModel itemModel = new StylesItemModel(price,
                            style,
                            getImageUploadUri,
                            userImage,
                            userName,
                            ServerValue.TIMESTAMP,
                            accountType);
                    String randomUID = serviceTypeDbRef.push().getKey();

                    assert randomUID != null;
                    serviceTypeDbRef.child(uid).child(randomUID).setValue(itemModel).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {

                            //create an activity node for viewers
                            activityDbRef.child(randomUID).setValue(itemModel);

                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, "Successful");

                            startActivity(new Intent(AddDesignOrStyleActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();


                        } else {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, Objects.requireNonNull(task1.getException()).getMessage());

                        }

                    });
*/
                } else {
                    progressDialog.dismiss();
                    DisplayViewUI.displayToast(this, Objects.requireNonNull(task.getException()).getMessage());

                }

            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                uri = result.getUri();

                Glide.with(AddDesignOrStyleActivity.this)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(styleItemPhoto);

                // uploadFile();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                DisplayViewUI.displayToast(this, error);
            }
        }

    }

    @Override
    public void onBackPressed() {
        int INTERVAL = 2000;
        if (mBackPressed + INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();

            return;
        } else {
            DisplayViewUI.displayToast(this, "Press back again to exit");

        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(MyConstants.STYLE, style);
        outState.putString(MyConstants.PRICE, String.valueOf(price));
        outState.putParcelable(MyConstants.IMAGE_URL, uri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Objects.requireNonNull(txtStyleName.getEditText()).setText(savedInstanceState.getString(MyConstants.STYLE));
        Objects.requireNonNull(txtPrice.getEditText()).setText(savedInstanceState.getString(MyConstants.PRICE));
        Glide.with(AddDesignOrStyleActivity.this)
                .load((Uri) savedInstanceState.getParcelable(MyConstants.IMAGE_URL))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(styleItemPhoto);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //get user details
        accountType = MainActivity.serviceType;
        userName = MainActivity.fullName;
        userImage = MainActivity.imageUrl;

    }

}
