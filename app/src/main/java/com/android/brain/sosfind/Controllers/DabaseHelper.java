package com.android.brain.sosfind.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Brain on 13/04/2019.
 */

public class DabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "users.db";
    public static final String TABLE_NAME = "tbl_users";
    public static final int DATABASE_VERSION = 1;

    public static final String COL_1 = "ID";
    public static final String COL_2 = "CONNECTED_USER"; // 1 = agent ou chauffeur et 2 = civil ou Client

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
            + COL_1 + " INTEGER PRIMARY KEY,"
            + COL_2 + " INTEGER)";

    public static final String UPDATE_TABLE = "DROP TABLE IF EXISTS" + TABLE_NAME;

    public DabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UPDATE_TABLE);
        onCreate(db);
    }

    public boolean insertData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, 1);
        contentValues.put(COL_2, 0);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, int conected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, conected);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }

}
