package com.example.superskiers.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.superskiers.inventoryapp.data.BoardsContract.BoardsEntry;



    //Database helper for Inventory app. Manages database creation and version management.
    public class SurfboardDbHelper extends SQLiteOpenHelper {

        public static final String LOG_TAG = SurfboardDbHelper.class.getSimpleName();

        //Name of the database file
        private static final String DATABASE_NAME = "surfboards.db";


        //Database version. If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 1;


        //Constructs a new instance of {@link SurfboardDbHelper}.
        //@param context of the app
        public SurfboardDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        //This is called when the database is created for the first time.
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create a String that contains the SQL statement to create the Inventory table
            String SQL_CREATE_SURFBOARDS_TABLE =  "CREATE TABLE " + BoardsEntry.TABLE_NAME + " ("
                    + BoardsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + BoardsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                    + BoardsEntry.COLUMN_PRICE + " TEXT NOT NULL, "
                    + BoardsEntry.COLUMN_BOARD_TYPE + " INTEGER NOT NULL, "
                    + BoardsEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                    + BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON + " TEXT NOT NULL, "
                    + BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL, "
                    + BoardsEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";

            // Execute the SQL statement
            db.execSQL(SQL_CREATE_SURFBOARDS_TABLE);

        }

        //This is called when the database needs to be upgraded.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // The database is still at version 1, so there's nothing to do be done here.
        }
    }

