package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.pets.data.PetContract.*;

public class PetDbHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "shelter.db";

    public PetDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(DELETE_ALL_ENTRIES);
//        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String SQL_CREATE_ENTRIES = "CREATE TABLE "+ PetEntry.TABLE_NAME +" ("
                + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL ,"
                + PetEntry.COLUMN_PET_BREED+ " TEXT,"
                + PetEntry.COLUMN_PET_GENDER+ " INTEGER NOT NULL ,"
                +PetEntry.COLUMN_PET_WEIGHT+ " INTEGER NOT NULL DEFAULT 0"
                + ");";
        Log.d("In onCreate", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public static final String DELETE_ALL_ENTRIES = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

}
