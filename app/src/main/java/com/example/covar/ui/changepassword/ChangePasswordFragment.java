package com.example.covar.ui.changepassword;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private static final String TAG = "ChangePassword";
    private EditText editOldPassword, editNewPassword;
    private Button btnChangePwd;

    //Firebase authentication
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_password, container, false);
        wireUI(root);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        return root;
    }

    private void wireUI(View root) {
        editOldPassword = root.findViewById(R.id.old_password);
        editNewPassword = root.findViewById(R.id.new_password);
        btnChangePwd = root.findViewById(R.id.changePasswordBtn);

        btnChangePwd.setOnClickListener(this::changePassword);

    }

    private void changePassword(View root) {
        String oldPassword = editOldPassword.getText().toString();
        String newPassword = editNewPassword.getText().toString();
        FirebaseUser user = mAuth.getCurrentUser();

        //https://stackoverflow.com/questions/39866086/change-password-with-firebase-for-android
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassword);
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                        Toast.makeText(getActivity(), R.string.pwd_updated,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                    } else {
                                        Toast.makeText(getActivity(), task.getException()
                                                .getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Old password entered wrongly",
                                    Toast.LENGTH_SHORT).show();
                            editOldPassword.getText().clear();
                            Log.d(TAG, "Error auth failed");
                        }
                    }
                });
    }

}