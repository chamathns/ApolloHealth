package com.example.apollohealth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.Format;
import java.text.SimpleDateFormat;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DB_NAME = "Apollo.db";

    //    user data table
    public static final String USER_TABLE = "USER_TABLE";
    public static final String USER_TABLE_ID = "ID";
    public static final String USER_TABLE_NAME = "NAME";
    public static final String USER_TABLE_AGE = "AGE";
    public static final String USER_TABLE_GENDER = "GENDER";
    public static final String USER_TABLE_WEIGHT = "WEIGHT";
    public static final String USER_TABLE_HEIGHT = "HEIGHT";

    //    health table
    public static final String HEALTH_TABLE = "HEALTH_TABLE";
    public static final String HEALTH_TABLE_TIMESTAMP = "TIMESTAMP";
    public static final String HEALTH_TABLE_SCREEN_TIME = "SCREEN_TIME";
    public static final String HEALTH_TABLE_UNLOCKS = "UNLOCKS";
    public static final String HEALTH_TABLE_PICKUPS = "PICKUPS";
    public static final String HEALTH_TABLE_DIST = "WALK_DIST";
    public static final String HEALTH_TABLE_STEPS = "STEPS";
    public static final String HEALTH_TABLE_HEIGHTS = "HEIGHT_CLIMBED";

    //    app data table
    public static final String APP_TABLE = "APP_TABLE";
    public static final String APP_TABLE_NAME = "APP_NAME";
    public static final String APP_TABLE_GENRE = "GENRE";
    public static final String APP_TABLE_SCREEN_TIME = "SCREEN_TIME";
    public static final String APP_TABLE_TIMESTAMP = "TIMESTAMP";

    private static final Format dateFormat = new SimpleDateFormat("yyyyMMdd");

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
                        USER_TABLE_HEIGHT + " FLOAT);"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + HEALTH_TABLE + " (" +
                        HEALTH_TABLE_TIMESTAMP + " BIGINT PRIMARY KEY, " +
                        HEALTH_TABLE_SCREEN_TIME + " INTEGER NOT NULL, " +
                        HEALTH_TABLE_UNLOCKS + " INTEGER NOT NULL, " +
                        HEALTH_TABLE_PICKUPS + " INTEGER NOT NULL, " +
                        HEALTH_TABLE_DIST + " INTEGER NOT NULL, " +
                        HEALTH_TABLE_STEPS + " INTEGER NOT NULL, " +
                        HEALTH_TABLE_HEIGHTS + " INTEGER NOT NULL);"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + APP_TABLE + " (" +
                        APP_TABLE_NAME + " TEXT NOT NULL, " +
                        APP_TABLE_GENRE + " TEXT NOT NULL, " +
                        APP_TABLE_SCREEN_TIME + " INT NOT NULL, " +
                        APP_TABLE_TIMESTAMP + " BIGINT NOT NULL, " +
                        "PRIMARY KEY (" + APP_TABLE_NAME + ", " + APP_TABLE_TIMESTAMP + "), " +
                        "FOREIGN KEY (" + APP_TABLE_TIMESTAMP + ") REFERENCES " + HEALTH_TABLE + " (" + HEALTH_TABLE_TIMESTAMP + "));"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(
                "DROP TABLE IF EXISTS " + USER_TABLE + ";"
        );

        sqLiteDatabase.execSQL(
                "DROP TABLE IF EXISTS " + HEALTH_TABLE + ";"
        );

        sqLiteDatabase.execSQL(
                "DROP TABLE IF EXISTS " + APP_TABLE + ";"
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
                "SELECT * FROM " + USER_TABLE + ";",
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

    private boolean insertHealthData(long timestamp, int screenTime, int unlocks, int pickups, int walkingDist, int steps, int heights) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        long timestampDate = Long.parseLong(dateFormat.format(timestamp));

        contentValues.put(HEALTH_TABLE_TIMESTAMP, timestampDate);
        contentValues.put(HEALTH_TABLE_SCREEN_TIME, screenTime);
        contentValues.put(HEALTH_TABLE_UNLOCKS, unlocks);
        contentValues.put(HEALTH_TABLE_PICKUPS, pickups);
        contentValues.put(HEALTH_TABLE_DIST, walkingDist);
        contentValues.put(HEALTH_TABLE_STEPS, steps);
        contentValues.put(HEALTH_TABLE_HEIGHTS, heights);

        long result = db.insert(HEALTH_TABLE, null, contentValues);

        return result != -1;
    }

    public Cursor getEmotionData(int numDays) {
        SQLiteDatabase db = this.getWritableDatabase();

        long tsCurrent = System.currentTimeMillis();
        long tsLimit = tsCurrent - numDays * 24 * 3600 * 1000L;
        long tsLimitDate = Long.parseLong(dateFormat.format(tsLimit));

        return db.rawQuery(
                "SELECT " + HEALTH_TABLE_SCREEN_TIME + ", " + HEALTH_TABLE_UNLOCKS + ", " + HEALTH_TABLE_PICKUPS + " " +
                        "FROM " + HEALTH_TABLE + " " +
                        "WHERE (" + HEALTH_TABLE_TIMESTAMP + " > " + tsLimitDate + ");",
                null
        );
    }

    public Cursor getPhysicalData(int numDays) {
        SQLiteDatabase db = this.getWritableDatabase();

        long tsCurrent = System.currentTimeMillis();
        long tsLimit = tsCurrent - numDays * 24 * 3600 * 1000L;
        long tsLimitDate = Long.parseLong(dateFormat.format(tsLimit));

        return db.rawQuery(
                "SELECT " + HEALTH_TABLE_DIST + ", " + HEALTH_TABLE_STEPS + ", " + HEALTH_TABLE_HEIGHTS + " " +
                        "FROM " + HEALTH_TABLE + " " +
                        "WHERE (" + HEALTH_TABLE_TIMESTAMP + " > " + tsLimitDate + ");",
                null
        );
    }

    public boolean updateHealthData(long timestamp, int screenTime, int unlocks, int pickups, int walkingDist, int steps, int heights) {
        SQLiteDatabase db = this.getWritableDatabase();
        long timestampDate = Long.parseLong(dateFormat.format(timestamp));

        Cursor c = db.rawQuery(
                "SELECT 1 " +
                        "FROM " + HEALTH_TABLE + " " +
                        "WHERE (" + HEALTH_TABLE_TIMESTAMP + " = " + timestampDate + ");",
                null
        );

        if (c.getCount() <= 0) {
            c.close();
            return insertHealthData(timestamp, screenTime, unlocks, pickups, walkingDist, steps, heights);
        }

        c.close();

        db.execSQL(
                "UPDATE " + HEALTH_TABLE +
                        " SET " + HEALTH_TABLE_SCREEN_TIME + " = " + HEALTH_TABLE_SCREEN_TIME + " + " + screenTime + ", " +
                        HEALTH_TABLE_UNLOCKS + " = " + HEALTH_TABLE_UNLOCKS + " + " + unlocks + ", " +
                        HEALTH_TABLE_PICKUPS + " = " + HEALTH_TABLE_PICKUPS + " + " + pickups + ", " +
                        HEALTH_TABLE_DIST + " = " + HEALTH_TABLE_DIST + " + " + walkingDist + ", " +
                        HEALTH_TABLE_STEPS + " = " + HEALTH_TABLE_STEPS + " + " + steps + ", " +
                        HEALTH_TABLE_HEIGHTS + " = " + HEALTH_TABLE_HEIGHTS + " + " + heights +
                        " WHERE " + HEALTH_TABLE_TIMESTAMP + " = " + timestampDate + ";"
        );

        return true;
    }

    public boolean insertAppData(String appName, String appGenre, long timestamp, int screenTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long timestampDate = Long.parseLong(dateFormat.format(timestamp));

        contentValues.put(APP_TABLE_NAME, appName);
        contentValues.put(APP_TABLE_GENRE, appGenre);
        contentValues.put(APP_TABLE_SCREEN_TIME, screenTime);
        contentValues.put(APP_TABLE_TIMESTAMP, timestampDate);

        long result = db.insert(APP_TABLE, null, contentValues);

        return result != -1;
    }

    public Cursor getAppDataApp(int numDays) {
        SQLiteDatabase db = this.getWritableDatabase();

        long tsCurrent = System.currentTimeMillis();
        long tsLimit = tsCurrent - numDays * 24 * 3600 * 1000L;
        long tsLimitDate = Long.parseLong(dateFormat.format(tsLimit));

        return db.rawQuery(
                "SELECT " + APP_TABLE_NAME + ", SUM(" + APP_TABLE_SCREEN_TIME + ")" +
                        " FROM " + APP_TABLE +
                        " GROUP BY " + APP_TABLE_NAME +
                        " WHERE (" + APP_TABLE_TIMESTAMP + " > " + tsLimitDate + ");",
                null
        );
    }

    public Cursor getAppDataGenre(int numDays) {
        SQLiteDatabase db = this.getWritableDatabase();

        long tsCurrent = System.currentTimeMillis();
        long tsLimit = tsCurrent - numDays * 24 * 3600 * 1000L;
        long tsLimitDate = Long.parseLong(dateFormat.format(tsLimit));

        return db.rawQuery(
                "SELECT " + APP_TABLE_GENRE + ", SUM(" + APP_TABLE_SCREEN_TIME + ")" +
                        " FROM " + APP_TABLE +
                        " GROUP BY " + APP_TABLE_GENRE +
                        " WHERE (" + APP_TABLE_TIMESTAMP + " > " + tsLimitDate + ");",
                null
        );
    }

    public boolean updateAppData(String appName, String appGenre, long timestamp, int screenTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        long timestampDate = Long.parseLong(dateFormat.format(timestamp));

        Cursor c = db.rawQuery(
                "SELECT 1" +
                        " FROM " + APP_TABLE +
                        " WHERE (" + APP_TABLE_NAME + " = " + appName + ")" +
                        " AND (" + APP_TABLE_TIMESTAMP + " = " + timestampDate + ");",
                null
        );

        if (c.getCount() <= 0) {
            c.close();
            return insertAppData(appName, appGenre, timestamp, screenTime);
        }

        c.close();

        db.execSQL(
                "UPDATE " + APP_TABLE +
                        " SET " + APP_TABLE_SCREEN_TIME + " = " + APP_TABLE_SCREEN_TIME + " + " + screenTime +
                        " WHERE (" + APP_TABLE_NAME + " = " + appName + ")" +
                        " AND (" + APP_TABLE_TIMESTAMP + " = " + timestampDate + ");"
        );

        return true;
    }
}
