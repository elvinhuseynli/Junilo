package com.example.junilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class Database extends SQLiteOpenHelper {

    final String databaseName = "UserDB.db";

    public Database(Context context) {
        super(context, "UserDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(username TEXT primary key, name TEXT, email TEXT, phone TEXT, dob TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int j) {
        MyDB.execSQL("drop Table if exists users");
    }

    public Boolean insertData(String name, String username, String email, String phone, String dob, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("username", username);
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("dob", dob);
        contentValues.put("password", password);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;
    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public Boolean checkPhoneNumber(String phoneNumber) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where phone = ?", new String[]{phoneNumber});
        return cursor.getCount() > 0;
    }

    public Boolean checkEmailAddress(String emailAddress) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where email = ?", new String[]{emailAddress});
        return cursor.getCount() > 0;
    }

    public Boolean updatePasswordEmail(String password, String email) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where email = ? and password=?", new String[]{email,password});
        if(cursor.getCount()==0) {
            MyDB.execSQL("UPDATE users SET password=? WHERE email = ?", new String[]{password, email});
            return true;
        }
        return false;
    }

    public Boolean updatePasswordPhone(String password, String phone) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where phone = ? and password=?", new String[]{phone, password});
        if (cursor.getCount()==0) {
            MyDB.execSQL("UPDATE users SET password=? WHERE phone = ?", new String[]{password, phone});
            return true;
        }
        return false;
    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ? and password = ?", new String[]{username, password});
        return cursor.getCount() > 0;
    }

    public String getName(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select name from users where username = ?", new String[]{username});
        return cursor.getString(0);
    }

}