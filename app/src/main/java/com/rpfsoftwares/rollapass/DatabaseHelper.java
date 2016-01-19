package com.rpfsoftwares.rollapass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Database Helper for SQLite
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "bd_passroll";
    // Table Names
    private static final String TABLE= "rap_accounts"; //saves the user's accounts
    private static final String TABLE_USER= "rap_user"; //saves the user's Master Password
    private static final String TABLE_WORD= "rap_words"; //saves the words used to generate passwords

    // Column names
    private static final String KEY_ID = "id_account";
    private static final String KEY_WEBSITE = "website_account";
    private static final String KEY_USERNAME = "username_account";
    private static final String KEY_PASSWORD = "password_account";
    private static final String KEY_MASTER_PASSWORD = "password_account";
    private static final String KEY_ID_WORD = "id_word";
    private static final String KEY_WORD = "full_word";


    //query to create the table rap_accounts
    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_WEBSITE + " TEXT,"
            + KEY_USERNAME + " TEXT NULL,"
            + KEY_PASSWORD + " TEXT"
            + ")";

    //query to create the table rap_user
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_MASTER_PASSWORD + " TEXT"
            + ")";

    //query to create the table rap_words
    private static final String CREATE_TABLE_WORD = "CREATE TABLE "
            + TABLE_WORD + "(" + KEY_ID_WORD + " INTEGER PRIMARY KEY,"
            + KEY_WORD + " TEXT"
            + ")";

    // Class Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
        criarTabelas();
    }

    //method to close the Database connection
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    //method to create the tables
    public void criarTabelas()
    {
        SQLiteDatabase db= this.getReadableDatabase();
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_WORD);
        db.execSQL(CREATE_TABLE_USER);
    }

    //method to insert a new account on the database
    public long create(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WEBSITE, account.getWebsite());
        values.put(KEY_USERNAME, account.getUsername());
        values.put(KEY_PASSWORD, account.getPassword());
        long todo_id = db.insert(TABLE, null, values);
        return todo_id;
    }

    //Method to insert the master password on the database
    // *It inserts 12345 as default
    public long setMaster()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, "12345");
        long id = db.insert(TABLE_USER, null, values);
        return id;
    }

    //method to update the master password
    public int updatemaster(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, password);
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(1) });
    }

    //method to insert a new word on the database
    public long createWord(int id, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_WORD, id);
        values.put(KEY_WORD, pass);
        long todo_id = db.insert(TABLE_WORD, null, values);
        return todo_id;
    }

    //method to read all the accounts on the database
    public List<Account> read() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Account> accounts = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Account account= new Account(c.getInt(c.getColumnIndex(KEY_ID)),c.getString(c.getColumnIndex(KEY_WEBSITE)),c.getString(c.getColumnIndex(KEY_USERNAME)),c.getString(c.getColumnIndex(KEY_PASSWORD)));
                    accounts.add(account);
                } while (c.moveToNext());

            }
        }
        return accounts;
    }

    //method to get the MasterPassword
    public String getMasterPassword() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USER+" WHERE "+KEY_ID+"=1";
        String Master="";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Master=c.getString(c.getColumnIndex(KEY_PASSWORD));
                } while (c.moveToNext());

            }
        }
        return Master;
    }

    //method to export all the password to the Phone's Memory
    public String export()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE+" ORDER BY "+ KEY_ID+" ASC";
        String body= "";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Account account= new Account(c.getInt(c.getColumnIndex(KEY_ID)),c.getString(c.getColumnIndex(KEY_WEBSITE)),c.getString(c.getColumnIndex(KEY_USERNAME)),c.getString(c.getColumnIndex(KEY_PASSWORD)));
                    body=body+account.toString()+"\n";
                } while (c.moveToNext());

            }
        }
        return body;
    }

    //method to get an word to generate passwords.
    public String getWord(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_WORD+ " WHERE "+KEY_ID_WORD+"="+id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String word=c.getString(c.getColumnIndex(KEY_WORD));
                c.close();
                return word;
            }
            else {
                c.close();
                return "";
            }
        }
        else {
            c.close();
            return "";
        }
    }

    //method to delete an account
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, KEY_ID + " = ?",new String[] { String.valueOf(id) });
    }

}