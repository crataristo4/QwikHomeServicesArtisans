package com.artisans.qwikhomeservices.activities.home.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.LayoutAddStatusBottomSheetBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.GetTimeAgo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AddStatusToFireStore extends BottomSheetDialogFragment {
    private LayoutAddStatusBottomSheetBinding layoutAddStatusBottomSheetBinding;
    private TextInputLayout txtStatus;
    private DatabaseReference dbStatus;
    private ProgressBar loading;
    private CollectionReference dbReference = FirebaseFirestore.getInstance().collection("Activity");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutAddStatusBottomSheetBinding = DataBindingUtil.inflate(inflater, R.layout.layout_add_status_bottom_sheet, container, false);
        return layoutAddStatusBottomSheetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbStatus = FirebaseDatabase.getInstance().getReference().child("Activity");
        loading = layoutAddStatusBottomSheetBinding.pbLoading;

        layoutAddStatusBottomSheetBinding.txtCancel.setOnClickListener(v -> dismiss());

        layoutAddStatusBottomSheetBinding.txtOk.setOnClickListener(this::postStatus);
    }

    private void postStatus(View view) {
        txtStatus = layoutAddStatusBottomSheetBinding.textInputLayoutStatus;
        String getInputFromUser = Objects.requireNonNull(txtStatus.getEditText()).getText().toString();

        //for testing
        // String randomNames = DisplayViewUI.getAlphaNumericString(8);
        String getName = MainActivity.fullName;
        String getPhotoUrl = MainActivity.imageUrl;

        if (!getInputFromUser.trim().isEmpty()) {
            loading.setVisibility(View.VISIBLE);

            requireActivity().runOnUiThread(() -> {

                //post status
                Map<String, Object> statusObjectMap = new HashMap<>();
                statusObjectMap.put("timeStamp", GetTimeAgo.getTimeInMillis());
                statusObjectMap.put("userName", getName);
                statusObjectMap.put("status", getInputFromUser);
                statusObjectMap.put("userPhoto", getPhotoUrl);

        /*String dbNodeId = dbStatus.push().getKey();

        //insert into db ... firebase realtime database
        dbStatus.child(Objects.requireNonNull(dbNodeId)).setValue(statusObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    dismiss();
                    loading.setVisibility(View.GONE);
                    txtStatus.getEditText().getText().clear();
                    DisplayViewUI.displayToast(requireActivity(), "Successful");
                } else {
                    loading.setVisibility(View.GONE);
                    DisplayViewUI.displayToast(requireActivity(), Objects.requireNonNull(task.getException()).getMessage());
                }

            }
        });
*/

                //fire store cloud store
                dbReference.add(statusObjectMap).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        dismiss();
                        loading.setVisibility(View.GONE);
                        txtStatus.getEditText().getText().clear();
                        DisplayViewUI.displayToast(requireActivity(), "Successful");
                    } else {
                        loading.setVisibility(View.GONE);
                        DisplayViewUI.displayToast(requireActivity(), Objects.requireNonNull(task.getException()).getMessage());
                    }

                });

            });


        } else {
            txtStatus.setErrorEnabled(true);
            txtStatus.setError("field required");
        }
    }
}
