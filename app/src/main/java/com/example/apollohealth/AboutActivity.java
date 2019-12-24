package com.example.apollohealth;

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
        imageview_username = (ImageView) findViewById(R.id.imageview_edit_username);

        addEditUserNameDialog(imageview_username,textview_username);

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

    @Override
    public void onDialogPositiveClick(String userName) {
        textview_username.setText(userName);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
