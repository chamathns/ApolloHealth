package com.example.apollohealth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.apollohealth.ui.dialog.UsernameDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;

import customfonts.MyTextView_Roboto_Regular;

public class AboutActivity extends FragmentActivity implements UsernameDialog.UserNameDialogListener {

    private MyTextView_Roboto_Regular textview_username;
    private MyTextView_Roboto_Regular textview_gender;
    private MyTextView_Roboto_Regular textview_height;
    private MyTextView_Roboto_Regular textview_weight;
    private ImageView imageview_username;
    private ImageView imageview_gender;
    private ImageView imageview_height;
    private ImageView imageview_weight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textview_username = (MyTextView_Roboto_Regular) findViewById(R.id.textview_username);
        textview_gender = (MyTextView_Roboto_Regular) findViewById(R.id.textview_gender);

        imageview_username = (ImageView) findViewById(R.id.imageview_edit_username);
        imageview_gender = (ImageView) findViewById(R.id.imageview_edit_gender);

        addEditUserNameDialog(imageview_username,textview_username);
        addEditGenderDialog(imageview_gender, textview_gender);

        addBottomNavigation();

    }

    public void addEditUserNameDialog(ImageView imageview_username, MyTextView_Roboto_Regular textview_username){
        imageview_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUserNameDialog();
            }
        });

        textview_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUserNameDialog();
            }
        });

    }

    public void openEditUserNameDialog() {
        UsernameDialog usernameDialog = new UsernameDialog();
        usernameDialog.show(getSupportFragmentManager(),"edit username");

    }

    public void addBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final int result = 1;
                switch (menuItem.getItemId()) {

                    case R.id.action_profile:
                        Intent profileIntent = new Intent(AboutActivity.this, MainActivity.class);
                        startActivityForResult(profileIntent, result);
                        break;

                    case R.id.action_health:
                        Intent healthIntent = new Intent(AboutActivity.this, HealthActivity.class);
                        startActivityForResult(healthIntent, result);
                        break;

                    case R.id.action_journal:
                        Intent journalIntent = new Intent(AboutActivity.this, JournalActivity.class);
                        startActivityForResult(journalIntent, result);
                        break;
//                        startActivity(journalIntent);
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
                return false;
            }
        });
    }
    public void addEditGenderDialog(ImageView imageview_gender, MyTextView_Roboto_Regular textview_gender){
        imageview_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditGenderDialog();
            }
        });

        textview_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditGenderDialog();
            }
        });
    }

    public void openEditGenderDialog() {
        final CharSequence[] gender = {"Male", "Female"};
        AlertDialog.Builder genderDialog = new AlertDialog.Builder(this);
        genderDialog.setTitle("Select your gender");
        genderDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Continue with ok
            }
        });
        genderDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Continue with cancel
            }
        });
        genderDialog.setSingleChoiceItems(gender, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (gender[which] == "Male"){
                    textview_gender.setText("Male");
                }
                else if (gender[which] == "Female"){
                    textview_gender.setText("Female");
                }
            }
        });
        genderDialog.show();
    }

    @Override
    public void onUserNameDialogPositiveClick(String userName) {
        if (userName.matches("[a-zA-Z]+")){
            textview_username.setText(userName);
        }
        else if (!StringUtils.isEmpty(userName)){
            new AlertDialog.Builder(this)
                    .setTitle("User name is not valid")
                    .setMessage("Your name should only include letters")

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onUserNameDialogNegativeClick(DialogFragment dialog) {

    }
}
