package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lorenzo on 20/07/17.
 */

public class ItemDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "inventario.db";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " ( " +
            ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ItemContract.ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            ItemContract.ItemEntry.COLUMN_PRICE + " REAL NOT NULL, " +
            ItemContract.ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
            ItemContract.ItemEntry.COLUMN_IMAGE + " SPACE NOT NULL ) " +
            ";";

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("SQLENTRIES", SQL_CREATE_ENTRIES);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
