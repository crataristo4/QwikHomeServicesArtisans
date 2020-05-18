package com.artisans.qwikhomeservices.activities.home.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.about.AddDesignOrStyleActivity;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.AddStatusToFireStore;
import com.artisans.qwikhomeservices.adapters.MultiViewTypeAdapter;
import com.artisans.qwikhomeservices.databinding.FragmentActivitiesBinding;
import com.artisans.qwikhomeservices.models.ActivityItemModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class ActivitiesFragment extends Fragment {

    private static final String KEY = "key";
    private static final String TAG = "ActivityFragment";
    private static final int INITIAL_LOAD = 15;
    private Bundle mBundleState;
    private boolean userScrolled = false;
    private int currentPage = 1;
    private FragmentActivitiesBinding fragmentActivitiesBinding;
    private RecyclerView recyclerView;
    private MultiViewTypeAdapter adapter;
    private ArrayList<ActivityItemModel> arrayList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private Parcelable mState;
    private ListenerRegistration registration;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentActivitiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activities, container, false);

        return fragmentActivitiesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        loadActivityData();
        requireActivity().runOnUiThread(this::fetchDataFromFireStore);

    }

    private void loadActivityData() {
        recyclerView = fragmentActivitiesBinding.rvItems;
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // isLoading = activityTestListBinding.pbLoading;

        adapter = new MultiViewTypeAdapter(arrayList, getContext());
        recyclerView.setAdapter(adapter);

        fragmentActivitiesBinding.addStatus.setOnClickListener(v -> {

            AddStatusToFireStore addStatusBottomSheet = new AddStatusToFireStore();
            addStatusBottomSheet.setCancelable(false);
            addStatusBottomSheet.show(requireActivity().getSupportFragmentManager(), "status");

        });

        fragmentActivitiesBinding.addItem.setOnClickListener(v -> startActivity(new Intent(getContext(), AddDesignOrStyleActivity.class)));

    }

/*
    private void loadActivityData() {
        recyclerView = fragmentActivitiesBinding.rvItems;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        dbRef = FirebaseDatabase.getInstance().getReference()
                .child("Activity");
        dbRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = dbRef.orderByValue();

        FirebaseRecyclerOptions<StylesItemModel> options =
                new FirebaseRecyclerOptions.Builder<StylesItemModel>().setQuery(query,
                        StylesItemModel.class)
                        .build();

        activityItemAdapter = new ActivityItemAdapter(options);
        recyclerView.setAdapter(activityItemAdapter);
    }
*/

    private void fetchDataFromFireStore() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Activity");

        // Create a query against the collection.
        Query query = collectionReference.orderBy("timeStamp", Query.Direction.DESCENDING).limit(INITIAL_LOAD);

        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            arrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                if (ds != null) {

                    ActivityItemModel itemModel = ds.toObject(ActivityItemModel.class);
                    //get data from model
                    String userName = itemModel.getUserName();
                    String userPhoto = itemModel.getUserPhoto();
                    String itemDescription = itemModel.getItemDescription();
                    String status = itemModel.getStatus();
                    String itemImage = itemModel.getItemImage();
                    long timeStamp = itemModel.getTimeStamp();
                    String id = ds.getId();

//group data by status
                    if (ds.getData().containsKey("status")) {
                        Log.i(TAG, "status: " + ds.getData().get("status"));

                        arrayList.add(new ActivityItemModel(ActivityItemModel.TEXT_TYPE,
                                status,
                                userName,
                                userPhoto,
                                timeStamp,
                                id));

                    }
                    //group data by item description
                    else if (ds.getData().containsKey("itemDescription")) {
                        arrayList.add(new ActivityItemModel(ActivityItemModel.IMAGE_TYPE,
                                itemImage,
                                itemDescription,
                                userName,
                                userPhoto,
                                timeStamp,
                                id
                        ));
                    }
                }
            }
            adapter.notifyDataSetChanged();


        });

       /* //get all items from fire store
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                    Log.i(TAG, "onComplete: " + ds.getId() + " " + ds.getData());

                    ActivityItemModel itemModel = ds.toObject(ActivityItemModel.class);

                    //group data by status
                    if (ds.getData().containsKey("status")) {
                        Log.i(TAG, "status: " + ds.getData().get("status"));

                        arrayList.add(new ActivityItemModel(ActivityItemModel.TEXT_TYPE,
                                itemModel.getStatus(),
                                itemModel.getUserName(),
                                itemModel.getUserPhoto(),
                                itemModel.getTimeStamp()));

                    }
                    //group data by item description
                    else if (ds.getData().containsKey("itemDescription")) {
                        Log.i(TAG, "itemDescription: " + ds.getData().get("itemDescription"));

                        arrayList.add(new ActivityItemModel(ActivityItemModel.IMAGE_TYPE,
                                itemModel.getItemImage(),
                                itemModel.getItemDescription(),
                                itemModel.getUserName(),
                                itemModel.getUserPhoto(),
                                itemModel.getTimeStamp()));
                    }

                }

                adapter.notifyDataSetChanged();

            }

        });*/
    }


    // Implement scroll listener
    public void implementScrollListener() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

              /*  int pos = layoutManager.findLastCompletelyVisibleItemPosition();
                int numItems =  adapter.getItemCount();
                if (pos >= numItems -1){
                    fetchMoreData();
                }*/

                if (!userScrolled) {
                    if (layoutManager != null &&
                            layoutManager.findLastCompletelyVisibleItemPosition() == arrayList.size() - 1) {
                        //bottom of list!
                        fetchMoreData();
                        userScrolled = true;
                    }
                }


            }
        });
    }

    private void fetchMoreData() {
    }


    @Override
    public void onStart() {
        super.onStart();
        // activityItemAdapter.startListening();



    }


    @Override
    public void onStop() {
        super.onStop();
        //activityItemAdapter.stopListening();
        registration.remove();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBundleState = new Bundle();
        mState = layoutManager.onSaveInstanceState();
        mBundleState.putParcelable(KEY, mState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBundleState != null) {

            new Handler().postDelayed(() -> {

                mState = mBundleState.getParcelable(KEY);
                layoutManager.onRestoreInstanceState(mState);
            }, 50);
        }

        recyclerView.setLayoutManager(layoutManager);
    }

}
