package com.example.securefit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "securefit.db";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_GOALS = "goals";

    // Common Columns
    private static final String COLUMN_ID = "id";

    // Users Table Columns
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";

    // Goals Table Columns
    private static final String COLUMN_GOAL_WEIGHT = "goal_weight";
    private static final String COLUMN_CURRENT_WEIGHT = "current_weight";
    private static final String COLUMN_WEIGHT_GOAL = "weight_goal";

    // Create Users Table Query
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_EMAIL + " TEXT" + ")";

    // Create Goals Table Query
    private static final String CREATE_GOALS_TABLE = "CREATE TABLE " + TABLE_GOALS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GOAL_WEIGHT + " INTEGER,"
            + COLUMN_CURRENT_WEIGHT + " INTEGER,"
            + COLUMN_WEIGHT_GOAL + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_GOALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        // Create new tables
        onCreate(db);
    }

    // Add User Method
    public long addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // Check if User Exists
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get User Information
    public Cursor getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
    }

    // Add Goal
    public long addGoal(int goalWeight, int currentWeight, int weightGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_WEIGHT, goalWeight);
        values.put(COLUMN_CURRENT_WEIGHT, currentWeight);
        values.put(COLUMN_WEIGHT_GOAL, weightGoal);

        long result = db.insert(TABLE_GOALS, null, values);
        db.close();
        return result;
    }

    // Get Goal Information
    public Cursor getGoal(int goalId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GOALS + " WHERE " + COLUMN_ID + "=?", new String[]{String.valueOf(goalId)});
    }

    // Update User Goal Information
    public int updateGoal(int goalId, int goalWeight, int currentWeight, int weightGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_WEIGHT, goalWeight);
        values.put(COLUMN_CURRENT_WEIGHT, currentWeight);
        values.put(COLUMN_WEIGHT_GOAL, weightGoal);

        return db.update(TABLE_GOALS, values, COLUMN_ID + "=?", new String[]{String.valueOf(goalId)});
    }

    // Delete Goal
    public void deleteGoal(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GOALS, COLUMN_ID + "=?", new String[]{String.valueOf(goalId)});
        db.close();
    }

    public boolean signupUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the username already exists in the database
        if (checkUserExists(username)) {
            return false;  // User already exists, cannot sign up
        }

        // Insert the new user into the database
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        // Insert the user data into the 'users' table and get the row ID
        long result = db.insert(TABLE_USERS, null, values);

        db.close();

        // If result is -1, the insert failed, otherwise it succeeded
        return result != -1;
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to find a user with the provided username and password
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password});

        // Check if a matching user was found
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;  // User found, login successful
        } else {
            cursor.close();
            db.close();
            return false;  // No user found with the given credentials, login failed
        }
    }
    public boolean isFirstTimeUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database to check if the user exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username});

        // If the cursor has no rows, it means the user doesn't exist, so it's the first time user
        boolean isFirstTime = cursor.getCount() == 0;

        cursor.close();
        db.close();

        return isFirstTime;
    }

}
