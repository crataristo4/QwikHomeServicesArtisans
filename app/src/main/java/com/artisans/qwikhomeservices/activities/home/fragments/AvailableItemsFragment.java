package com.artisans.qwikhomeservices.activities.home.fragments;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.adapters.StylesAdapter;
import com.artisans.qwikhomeservices.databinding.FragmentAvailableItemsBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class AvailableItemsFragment extends Fragment {
    private FragmentAvailableItemsBinding fragmentAvailableItemsBinding;
    private DatabaseReference databaseReference;
    private StylesAdapter adapter;
    //private AllBarbersAdapter adapter;

    public AvailableItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAvailableItemsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_available_items, container, false);
        return fragmentAvailableItemsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = fragmentAvailableItemsBinding.rvbb;
        rv.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Styles").child(MainActivity.uid);
        databaseReference.keepSynced(true);
        Query query = databaseReference.orderByChild("price");
        FirebaseRecyclerOptions<StylesItemModel> options =
                new FirebaseRecyclerOptions.Builder<StylesItemModel>().setQuery(query,
                        StylesItemModel.class)
                        .build();

        if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        } else if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        }


        adapter = new StylesAdapter(options);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new StylesAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                DisplayViewUI.displayToast(getActivity(), " Item " + position + " edit");
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
