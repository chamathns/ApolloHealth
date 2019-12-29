package com.example.apollohealth.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.apollohealth.R;

public class UsernameDialog extends DialogFragment {
    private EditText editTextUsername;



    public interface UserNameDialogListener {
        public void onUserNameDialogPositiveClick(String username);
        public void onUserNameDialogNegativeClick(DialogFragment dialog);
    }
    UserNameDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (UserNameDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() +
                    "must implement UserNameDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_about_username, null);

        builder.setView(view)
                .setTitle("Enter your name")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onUserNameDialogNegativeClick(UsernameDialog.this);

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = editTextUsername.getText().toString();
                        listener.onUserNameDialogPositiveClick(username);

                    }
                });
        editTextUsername = view.findViewById(R.id.edit_username);
        return builder.create();
    }

}
