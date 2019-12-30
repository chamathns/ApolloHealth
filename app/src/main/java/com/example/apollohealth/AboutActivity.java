package com.example.apollohealth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.apollohealth.db.DatabaseHandler;
import com.example.apollohealth.ui.dialog.UserHeightDialog;
import com.example.apollohealth.ui.dialog.UserWeightDialog;
import com.example.apollohealth.ui.dialog.UsernameDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;

import customfonts.MyTextView_Roboto_Regular;

public class AboutActivity extends FragmentActivity implements UsernameDialog.UserNameDialogListener, UserHeightDialog.UserHeightDialogListener, UserWeightDialog.UserWeightDialogListener {

    private MyTextView_Roboto_Regular textview_username;
    private MyTextView_Roboto_Regular textview_gender;
    private MyTextView_Roboto_Regular textview_height;
    private MyTextView_Roboto_Regular textview_weight;
    private ImageView imageview_username;
    private ImageView imageview_gender;
    private ImageView imageview_height;
    private ImageView imageview_weight;

    private String username;
    private int age;
    private String gender;
    private String height;
    private String weight;

    private MetricGenerator metrics;
    private DatabaseHandler myDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textview_username = (MyTextView_Roboto_Regular) findViewById(R.id.textview_username);
        textview_gender = (MyTextView_Roboto_Regular) findViewById(R.id.textview_gender);
        textview_height = (MyTextView_Roboto_Regular) findViewById(R.id.textview_height);
        textview_weight = (MyTextView_Roboto_Regular) findViewById(R.id.textview_weight);

        myDB = new DatabaseHandler(this);
        Cursor userData = myDB.getUserData();
        if (!userData.moveToFirst()){
            myDB.insertUserData("John Doe", 20,"Male", 60, 170);
        }
        else{
            userData.moveToFirst();
            username = userData.getString(1);
            age = Integer.parseInt(userData.getString(2));
            gender = userData.getString(3);
            weight = userData.getString(4);
            height = userData.getString(5);
            textview_username.setText(username);
            textview_gender.setText(gender);
            textview_weight.setText(weight);
            textview_height.setText(height);
        }

        imageview_username = (ImageView) findViewById(R.id.imageview_edit_username);
        imageview_gender = (ImageView) findViewById(R.id.imageview_edit_gender);
        imageview_height = (ImageView) findViewById(R.id.imageview_edit_height);
        imageview_weight = (ImageView) findViewById(R.id.imageview_edit_weight);

        addEditUserNameDialog(imageview_username, textview_username);
        addEditGenderDialog(imageview_gender, textview_gender);
        addEditHeightDialog(imageview_height, textview_height);
        addEditWeightDialog(imageview_weight, textview_weight);
        addBottomNavigation();

    }

    public void addEditUserNameDialog(ImageView imageview_username, MyTextView_Roboto_Regular textview_username) {
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
        usernameDialog.show(getSupportFragmentManager(), "edit username");
    }

    @Override
    public void onUserNameDialogPositiveClick(String userName) {
        if (userName.matches("[a-zA-Z]+")) {
            this.username = userName;
            myDB.updateUserData("1", username,age, gender, Float.parseFloat(weight), Float.parseFloat(height));
            textview_username.setText(username);
        } else if (!StringUtils.isEmpty(userName)) {
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

    public void addEditGenderDialog(ImageView imageview_gender, MyTextView_Roboto_Regular textview_gender) {
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
                if (gender[which] == "Male") {
                    myDB.updateUserData("1", username,age, "Male", Float.parseFloat(weight), Float.parseFloat(height));
                    textview_gender.setText("Male");
                } else if (gender[which] == "Female") {
                    myDB.updateUserData("1", username,age, "Female", Float.parseFloat(weight), Float.parseFloat(height));
                    textview_gender.setText("Female");
                }
            }
        });
        genderDialog.show();
    }


    public void addEditHeightDialog(ImageView imageview_height, MyTextView_Roboto_Regular textview_height) {
        imageview_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUserHeightDialog();
            }
        });

        textview_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUserHeightDialog();
            }
        });

    }

    public void openEditUserHeightDialog() {
        UserHeightDialog userHeightDialog = new UserHeightDialog();
        userHeightDialog.show(getSupportFragmentManager(), "edit user height");
    }

    @Override
    public void onUserHeightDialogPositiveClick(String userHeight) {
        this.height = userHeight;
        myDB.updateUserData("1", username,age, gender, Float.parseFloat(weight), Float.parseFloat(height));
        textview_height.setText(height + " cm");

    }

    public void addEditWeightDialog(ImageView imageview_weight, MyTextView_Roboto_Regular textview_weight) {
        imageview_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUserWeightDialog();
            }
        });

        textview_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUserWeightDialog();
            }
        });

    }

    public void openEditUserWeightDialog() {
        UserWeightDialog userWeightDialog = new UserWeightDialog();
        userWeightDialog.show(getSupportFragmentManager(), "edit user height");
    }

    @Override
    public void onUserWeightDialogPositiveClick(String userWeight) {
        this.weight = userWeight;
        myDB.updateUserData("1", username,age, gender, Float.parseFloat(weight), Float.parseFloat(height));
        textview_weight.setText(weight + " kg");
    }

    @Override
    public void onUserNameDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onUserHeightDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onUserWeightDialogNegativeClick(DialogFragment dialog) {

    }
}
