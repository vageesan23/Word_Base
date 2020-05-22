package com.example.test.connections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Creating a Local DataBase connection to store and use words and phrases
public class DatabaseConnection extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wordbase.db"; //Defining the database name
    private static final String TABLE_NAME = "phrases";  // Creating one table to insert words
    private static final String SUBSCRIPTION = "subscription"; // To store the foreign languages
    private static final String COL_2 = "name"; // Column to store words
    private static final int DATABASE_VERSION = 1; // define the database version
    private static String STOREDWORDS="storedwords"; //retrieve the words

    // Defining the local SQLite database to project
    public DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    // Implementing the Database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT)");
        db.execSQL("create table " + SUBSCRIPTION + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,LANGUAGETYPE TEXT)");
        db.execSQL("create table " + STOREDWORDS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,LANGUAGETYPE TEXT,PHRASE TEXT)");

    }

    // Upgrade and Drop the table if it already exist
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SUBSCRIPTION);
        db.execSQL("DROP TABLE IF EXISTS " + STOREDWORDS);
        onCreate(db);

    }

    // Adding the words and phrases to the database
    public void addToDB(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, name);
        db.insertOrThrow(TABLE_NAME, null, values);

    }

    // Retrieving all the words and phrases from the database
    public Cursor getAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cs = db.rawQuery("select * from " + TABLE_NAME+" ORDER BY NAME  ASC", null);
        return cs;
    }

    // getting specified word to edit
    public Cursor getSpecifiedName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_NAME + " where ID = '" + id + "'";
        return db.rawQuery(query, null);
    }

    // Updating the existing word with new word
    public void updateAll(int id, String updateName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2,updateName);
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(id)});

    }

    // Adding the subscribed languages to database to make changes
    public void addSubscription(String name, String languagetype) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("LANGUAGETYPE", languagetype);

        db.insertOrThrow(SUBSCRIPTION, null, values);

    }

    // Adding the translated words to database to use that activity in offline
    public void addWords(String name, String languageType, String phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("LANGUAGETYPE", languageType);
        values.put("PHRASE", phrase);
        db.insertOrThrow(STOREDWORDS, null, values);

    }

    // Define subscribed languages to database
    public Cursor getSubscriptionAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cs = db.rawQuery("select ID,NAME,LANGUAGETYPE from " + SUBSCRIPTION+" ORDER BY NAME  ASC", null);
        return cs;
    }

    // This method to uncheck and remove the subscribe languages from database
    public boolean deleteSubscription(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SUBSCRIPTION, "ID=" + id, null) > 0;
    }

    // getting the words
    public Cursor getPhrase(String type){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cs = db.rawQuery("select * from  storedwords WHERE LANGUAGETYPE = '"+type+"'", null);
        return cs;
    }

    //checking the phrases
    public Cursor checkPhrase(String check){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cs = db.rawQuery("select * from  phrases WHERE NAME like '"+'%'+check+'%'+"'", null);
        return cs;
    }

}