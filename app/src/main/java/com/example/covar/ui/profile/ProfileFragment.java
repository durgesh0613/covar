package com.example.covar.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.covar.R;
import com.example.covar.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {
    private EditText editMobileNum, editAge;
    private Button btnUpdateProf;

    //Firebase database
    private DatabaseReference mDatabase;

    //Firebase authentication
    private FirebaseAuth mAuth;

    //Firebase current user
    private FirebaseUser currUser;

    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        wireUI(root);

        //Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        currUser = mAuth.getCurrentUser();

        fillOldData();

        return root;
    }

    private void fillOldData() {
        String username = currUser.getEmail().split("@")[0];
        mDatabase.child("users").child(username).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error loading data" + task.getException()
                            .getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    user = task.getResult().getValue(User.class);
                    editMobileNum.setText(user.getMobileNum());
                    editAge.setText(user.getAge());
                }
            }
        });

    }

    private void wireUI(View root) {
        editMobileNum = root.findViewById(R.id.mobileNumber);
        editAge = root.findViewById(R.id.age);
        btnUpdateProf = root.findViewById(R.id.changeProfileBtn);

        btnUpdateProf.setOnClickListener(this::updateProfile);
    }

    private void updateProfile(View view) {
        try {
            String newMobileNum = editMobileNum.getText().toString();
            String newAge = editAge.getText().toString();
            String username = currUser.getEmail().split("@")[0];

            user.setMobileNum(newMobileNum);
            user.setAge(newAge);
            mDatabase.child("users").child(username).setValue(user);
            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT)
                    .show();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Profile update failed | " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}