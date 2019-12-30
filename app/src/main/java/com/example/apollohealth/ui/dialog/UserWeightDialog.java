package com.example.apollohealth.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.example.apollohealth.R;

public class UserWeightDialog extends DialogFragment {
    private NumberPicker numberPickerWeight;

    public interface UserWeightDialogListener {
        public void onUserWeightDialogPositiveClick(String userWeight);

        public void onUserWeightDialogNegativeClick(DialogFragment dialog);
    }

    UserWeightDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (UserWeightDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() +
                    "must implement UserWeightDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_about_weight, null);
        numberPickerWeight = view.findViewById(R.id.numberPickerWeight);
        numberPickerWeight.setMinValue(20);
        numberPickerWeight.setMaxValue(300);
        numberPickerWeight.setWrapSelectorWheel(false);
        numberPickerWeight.setValue(88);
        builder.setView(view)
                .setTitle("Set your weight")

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onUserWeightDialogNegativeClick(UserWeightDialog.this);

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userWeight = String.valueOf(numberPickerWeight.getValue());
                        listener.onUserWeightDialogPositiveClick(userWeight);

                    }
                });
        return builder.create();
    }

}
