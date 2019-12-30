package com.example.apollohealth.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.example.apollohealth.R;

public class UserHeightDialog extends DialogFragment  {
    private NumberPicker numberPickerHeight;

    public interface UserHeightDialogListener {
        public void onUserHeightDialogPositiveClick(String userHeight);
        public void onUserHeightDialogNegativeClick(DialogFragment dialog);
    }
    UserHeightDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (UserHeightDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() +
                    "must implement UserHeightDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_about_height, null);
        numberPickerHeight = view.findViewById(R.id.numberPickerHeight);
        numberPickerHeight.setMinValue(100);
        numberPickerHeight.setMaxValue(250);
        numberPickerHeight.setWrapSelectorWheel(false);
        numberPickerHeight.setValue(193);
        builder.setView(view)
                .setTitle("Set your height")

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onUserHeightDialogNegativeClick(UserHeightDialog.this);

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userHeight = String.valueOf(numberPickerHeight.getValue());
                        listener.onUserHeightDialogPositiveClick(userHeight);

                    }
                });
        return builder.create();
    }

}
