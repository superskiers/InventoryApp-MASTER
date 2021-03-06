package com.example.superskiers.inventoryapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.superskiers.inventoryapp.data.BoardsContract.BoardsEntry;

//{@link ContentProvider} for InventoryApp.
public class SurfboardProvider extends ContentProvider {

    //{@link ContentProvider} for InventoryApp.
    public static final String LOG_TAG = SurfboardProvider.class.getSimpleName();

    //URI matcher code for the content URI for the surfboards table
    private static final int BOARDS = 100;

    //URI matcher code for the content URI for a single product
    private static final int BOARDS_ID = 101;

    //UriMatcher object to match a content URI to a corresponding code.
    //The input passed into the constructor represents the code to return for the root URI.
    //It's common to use NO_MATCH as the input for this case.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(BoardsContract.CONTENT_AUTHORITY, BoardsContract.PATH_BOARDS, BOARDS);

        sUriMatcher.addURI(BoardsContract.CONTENT_AUTHORITY, BoardsContract.PATH_BOARDS + "/#", BOARDS_ID);

    }
    //Database helper object.
    private SurfboardDbHelper mDbHelper;

    //Initialize the provider and the database helper object.
    @Override
    public boolean onCreate() {
        //Create and initialize a SurfboardDbHelper object.
        mDbHelper = new SurfboardDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }


    //Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOARDS:
                // For the BOARDS code, query the surfboards table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the surfboards table.
                cursor = database.query(BoardsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOARDS_ID:
                // For the BOARDS_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.superskiers.inventoryapp/surfboards/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BoardsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the surfboards table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BoardsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOARDS:
                return insertSurfboard(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    //Insert a product into the database with the given content values. Return the new content URI
    //for that specific row in the database.
    private Uri insertSurfboard(Uri uri, ContentValues values) {

        //Check that the name is not null
        String name = values.getAsString(BoardsEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        //Check that the length is valid.
        Integer length = values.getAsInteger(BoardsEntry.COLUMN_BOARD_TYPE);
        if (length == null || !BoardsEntry.isLengthValid(length)) {
            throw new IllegalArgumentException("Board requires a valid type; long OR short");
        }

        //If the quantity is provided, check that the quantity is not less than 0. Quantity can be null which will default to 0.
        Integer quantity = values.getAsInteger(BoardsEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Board requires a quantity value");
        }

        //No need to check the price, any value is valid (including null).

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert the new product with the given values
        long id = database.insert(BoardsEntry.TABLE_NAME, null, values);
        //If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the board content URI
        getContext().getContentResolver().notifyChange(uri, null);

        //Return the new URI with the ID (of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }


    //Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOARDS:
                return updateSurfboard(uri, contentValues, selection, selectionArgs);
            case BOARDS_ID:
                // For the BOARDS_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BoardsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateSurfboard(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update surfboards in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more surfboards).
     * Return the number of rows that were successfully updated.
     */
    private int updateSurfboard(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //If the {@link BoardsEntry#COLUMN_PRODUCT_NAME} key is present,
        //check that the name value is not null.
        if (values.containsKey(BoardsEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BoardsEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        //If the {@link BoardsEntry#COLUMN_BOARD_TYPE} key is present,
        //check that the length value is valid.
        if (values.containsKey(BoardsEntry.COLUMN_BOARD_TYPE)) {
            Integer length = values.getAsInteger(BoardsEntry.COLUMN_BOARD_TYPE);
            if (length == null || !BoardsEntry.isLengthValid(length)) {
                throw new IllegalArgumentException("Board requires a valid type");
            }
        }

        //If the {@link BoardsEntry#COLUMN_QUANTITY} key is present,
        //check that the quantity value is valid.
        if (values.containsKey(BoardsEntry.COLUMN_QUANTITY)) {
            //Check that the quantity is greater than or equal to 0.
            Integer quantity = values.getAsInteger(BoardsEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Stock requires a valid quantity");
            }
        }
        //No need to check the price, any value is valid (including null).

        //If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        //Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BoardsEntry.TABLE_NAME, values, selection, selectionArgs);


        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }


    //Delete the data at the given selection and selection arguments.
    @Override
    public int delete (Uri uri, String selection, String[]selectionArgs){
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOARDS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BoardsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOARDS_ID:
                // Delete a single row given by the ID in the URI
                selection = BoardsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BoardsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }


    //Returns the MIME type of data for the content URI.
    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOARDS:
                return BoardsEntry.CONTENT_LIST_TYPE;
            case BOARDS_ID:
                return BoardsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match." + match);
        }
    }
}
