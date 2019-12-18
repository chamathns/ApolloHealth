package com.example.apollohealth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DB_NAME = "Apollo.db";

    //    user data table
    public static final String TABLE_NAME = "user_table";
    public static final String USER_TABLE_ID = "ID";
    public static final String USER_TABLE_NAME = "NAME";
    public static final String USER_TABLE_AGE = "AGE";
    public static final String USER_TABLE_GENDER = "GENDER";
    public static final String USER_TABLE_WEIGHT = "WEIGHT";
    public static final String USER_TABLE_HEIGHT = "HEIGHT";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        USER_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        USER_TABLE_NAME + " TEXT," +
                        USER_TABLE_AGE + " INTEGER," +
                        USER_TABLE_GENDER + " TEXT," +
                        USER_TABLE_WEIGHT + " FLOAT," +
                        USER_TABLE_HEIGHT + " FLOAT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_NAME
        );
        this.onCreate(sqLiteDatabase);
    }

//    health table
//    app data table

}
