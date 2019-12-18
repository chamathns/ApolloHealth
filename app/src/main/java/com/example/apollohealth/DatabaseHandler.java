package com.example.apollohealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DB_NAME = "Apollo.db";

    //    user data table
    public static final String USER_TABLE = "user_table";
    public static final String USER_TABLE_ID = "ID";
    public static final String USER_TABLE_NAME = "NAME";
    public static final String USER_TABLE_AGE = "AGE";
    public static final String USER_TABLE_GENDER = "GENDER";
    public static final String USER_TABLE_WEIGHT = "WEIGHT";
    public static final String USER_TABLE_HEIGHT = "HEIGHT";

    //    health table
    //    app data table

    public DatabaseHandler(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + USER_TABLE + " (" +
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
                "DROP TABLE IF EXISTS " + USER_TABLE
        );
        this.onCreate(sqLiteDatabase);
    }

    public boolean insertUserData(String name, int age, String gender, float weight, float height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_TABLE_NAME, name);
        contentValues.put(USER_TABLE_AGE, age);
        contentValues.put(USER_TABLE_GENDER, gender);
        contentValues.put(USER_TABLE_WEIGHT, weight);
        contentValues.put(USER_TABLE_HEIGHT, height);

        long result = db.insert(USER_TABLE, null, contentValues);

        return result != -1;
    }

    public Cursor getUserData() {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + USER_TABLE,
                null
        );
    }

    public boolean updateUserData(String id, String name, int age, String gender, float weight, float height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_TABLE_NAME, name);
        contentValues.put(USER_TABLE_AGE, age);
        contentValues.put(USER_TABLE_GENDER, gender);
        contentValues.put(USER_TABLE_WEIGHT, weight);
        contentValues.put(USER_TABLE_HEIGHT, height);

        db.update(USER_TABLE, contentValues, "ID = ?", new String[]{id});

        return true;
    }


}
