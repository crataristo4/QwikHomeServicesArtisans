package com.artisans.qwikhomeservices.activities.home;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.about.SettingsActivity;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.WelcomeNoticeBottomSheet;
import com.artisans.qwikhomeservices.activities.welcome.SplashScreenActivity;
import com.artisans.qwikhomeservices.databinding.ActivityMainBinding;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    public static String serviceType, fullName, firstName, lastName, imageUrl, about, uid, servicePersonId;
    public static DatabaseReference serviceTypeDbRef, serviceAccountDbRef;
    public static FirebaseUser firebaseUser;
    public static FirebaseAuth mAuth;
    private static Object mContext;
    private ActivityMainBinding activityMainBinding;


    public static Context getAppContext() {
        return (Context) mContext;
    }

    public static void retrieveServiceType() {

        serviceTypeDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Services")
                .child("ServiceType")
                .child(uid);
        serviceTypeDbRef.keepSynced(true);

        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> updateToken = new HashMap<>();
        updateToken.put("deviceToken", deviceToken);
        serviceTypeDbRef.updateChildren(updateToken);


        serviceTypeDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                serviceType = (String) dataSnapshot.child("accountType").getValue();
                fullName = (String) dataSnapshot.child("fullName").getValue();
                imageUrl = (String) dataSnapshot.child("image").getValue();
                firstName = (String) dataSnapshot.child("firstName").getValue();
                lastName = (String) dataSnapshot.child("lastName").getValue();
                about = (String) dataSnapshot.child("about").getValue();
                servicePersonId = (String) dataSnapshot.child("servicePersonId").getValue();


                Log.i(TAG, "onDataChange: " + serviceType + " " + fullName + " " + about + " " + imageUrl + " id::" + servicePersonId);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DisplayViewUI.displayToast(MainActivity.this, databaseError.getMessage());
            }
        });


    }

    public static void retrieveSingleUserDetails(TextView txtName, TextView txtServiceType, CircleImageView photo) {
        serviceAccountDbRef = FirebaseDatabase.getInstance()
                .getReference().child("Services")
                .child(serviceType)
                .child(uid);
        serviceAccountDbRef.keepSynced(true);

        serviceAccountDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fullName = (String) dataSnapshot.child("fullName").getValue();
                serviceType = (String) dataSnapshot.child("accountType").getValue();
                imageUrl = (String) dataSnapshot.child("image").getValue();
                firstName = (String) dataSnapshot.child("firstName").getValue();
                lastName = (String) dataSnapshot.child("lastName").getValue();

                txtName.setText(fullName);
                txtServiceType.setText(about);
                if (imageUrl == null) {

                    Glide.with(getAppContext())
                            .load(getAppContext().getResources().getDrawable(R.drawable.photoe))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photo);
                } else {
                    Glide.with(getAppContext())
                            .load(MainActivity.imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photo);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void retrieveSingleUserDetails(AppCompatImageView photo) {
        serviceAccountDbRef = FirebaseDatabase.getInstance()
                .getReference().child("Services")
                .child(serviceType)
                .child(uid);
        serviceAccountDbRef.keepSynced(true);

        serviceAccountDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                imageUrl = (String) dataSnapshot.child("image").getValue();

                if (Objects.requireNonNull(imageUrl).isEmpty()) {

                    Glide.with(getAppContext())
                            .load(getAppContext().getResources().getDrawable(R.drawable.photoe))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photo);
                } else {
                    Glide.with(getAppContext())
                            .load(MainActivity.imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void retrieveSingleUserDetails(String position, TextView txtName, TextView txtAbout, ImageView photo) {

        serviceAccountDbRef = FirebaseDatabase.getInstance()
                .getReference().child("Services")
                .child(serviceType)
                .child(position);
        serviceAccountDbRef.keepSynced(true);

        serviceAccountDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fullName = (String) dataSnapshot.child("fullName").getValue();
                about = (String) dataSnapshot.child("about").getValue();
                imageUrl = (String) dataSnapshot.child("image").getValue();
                firstName = (String) dataSnapshot.child("firstName").getValue();
                lastName = (String) dataSnapshot.child("lastName").getValue();

                txtName.setText(fullName);
                txtAbout.setText(about);
                if (imageUrl.isEmpty()) {

                    Glide.with(getAppContext())
                            .load(getAppContext().getResources().getDrawable(R.drawable.photoe))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photo);
                } else {
                    Glide.with(getAppContext())
                            .load(MainActivity.imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photo);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static void retrieveSingleUserDetails() {

        serviceAccountDbRef = FirebaseDatabase.getInstance()
                .getReference().child("Services")
                .child(serviceType)
                .child(uid);
        serviceAccountDbRef.keepSynced(true);

        serviceAccountDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fullName = (String) dataSnapshot.child("fullName").getValue();
                about = (String) dataSnapshot.child("about").getValue();
                imageUrl = (String) dataSnapshot.child("image").getValue();
                firstName = (String) dataSnapshot.child("firstName").getValue();
                lastName = (String) dataSnapshot.child("lastName").getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mContext = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();



        setUpAppBarConfig();

    }

    private void checkDisplayAlertDialog() {
        SharedPreferences pref = getSharedPreferences(MyConstants.PREFS, 0);
        boolean alertShown = pref.getBoolean(MyConstants.IS_DIALOG_SHOWN, false);

        if (!alertShown) {
            new Handler().postDelayed(() -> {

                WelcomeNoticeBottomSheet welcomeNoticeBottomSheet = new WelcomeNoticeBottomSheet();
                welcomeNoticeBottomSheet.setCancelable(false);
                welcomeNoticeBottomSheet.show(getSupportFragmentManager(), "welcome");

            }, 2000);

            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean(MyConstants.IS_DIALOG_SHOWN, true);
            edit.apply();
        }
    }

    private void SendUserToLoginActivity() {
        Intent Login = new Intent(MainActivity.this, SplashScreenActivity.class);
        startActivity(Login);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.action_settings) {

            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (item.getItemId() == R.id.action_logout) {

            intent = new Intent(MainActivity.this, SplashScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpAppBarConfig() {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_styles,
                R.id.navigation_activities,
                R.id.navigation_request).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(activityMainBinding.bottomNavigationView, navController);

        activityMainBinding.bottomNavigationView.setOnNavigationItemReselectedListener(menuItem -> {
            //do nothing
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            assert firebaseUser != null;

            if (mAuth.getCurrentUser() == null) {
                SendUserToLoginActivity();
            } else {
                uid = firebaseUser.getUid();
                checkDisplayAlertDialog();
                retrieveServiceType();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
