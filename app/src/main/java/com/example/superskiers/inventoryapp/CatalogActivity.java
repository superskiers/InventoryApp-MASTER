package com.example.superskiers.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superskiers.inventoryapp.data.BoardsContract.BoardsEntry;
import com.example.superskiers.inventoryapp.data.SurfboardDbHelper;


public class CatalogActivity extends AppCompatActivity {

    //Instance variable for the database Helper
    private SurfboardDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //Instantiate helper and pass in the context
        mDbHelper = new SurfboardDbHelper(this);
        displayDatabaseInfo();
    }
    //When the activity starts again (after user hits save) the list will refresh with the
    //new prpduct in the database.
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


    //Temporary helper method to display information in the onscreen TextView about the state of
    //the products database.
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BoardsEntry._ID,
                BoardsEntry.COLUMN_PRODUCT_NAME,
                BoardsEntry.COLUMN_PRICE,
                BoardsEntry.COLUMN_BOARD_TYPE,
                BoardsEntry.COLUMN_QUANTITY,
                BoardsEntry.COLUMN_SUPPLIER_NAME,
                BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON,
                BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        // Perform a query on the surfboards table
        Cursor cursor = db.query(
                BoardsEntry.TABLE_NAME,   //The table to query
                projection,               //The columns to return
                null,            //The columns for the WHERE clause
                null,         //The values for the WHERE clause
                null,            //Don't group the rows
                null,             //Don't filter by row groups
                null,            //Sort order
                null);             //No limitations

        TextView displayView = (TextView) findViewById(R.id.text_view_board);

        try {
            //Create a header in the Text View that looks like this:
            //The surfboards table contains <number of rows in Cursor> surfboards.
            //_id - name - price - board_type - quantity - supplier_name - supplier_contact - supplier_phone#
            //In the while loop below, iterate through the rows of the cursor and display
            //the information from each column in this order.
            displayView.setText("The surfboards table contains " + cursor.getCount() + " surfboards.\n\n");
            displayView.append(BoardsEntry._ID + " - " +
                    BoardsEntry.COLUMN_PRODUCT_NAME + " - " +
                    BoardsEntry.COLUMN_PRICE + " - " +
                    BoardsEntry.COLUMN_BOARD_TYPE + " - " +
                    BoardsEntry.COLUMN_QUANTITY + " - " +
                    BoardsEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON + " - " +
                    BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            //Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BoardsEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_PRICE);
            int boardTypeColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_BOARD_TYPE);
            int quantityColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_SUPPLIER_NAME);
            int supplierContactPersonColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


            //Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()){
                //Use that index to extract the String or Int value of the word
                //at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentBoardType = cursor.getInt(boardTypeColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentSupplierContact = cursor.getString(supplierContactPersonColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                //Display the values from each column of the current row in the cursor
                //in the TextView: text_view_product
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentBoardType + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentSupplierContact + " - " +
                        currentSupplierPhone));
            }
        } finally {
            //Always close the cursor when you're done reading from it. This releases all its
            //resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertBoard(){
        //Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Create a content values object
        ContentValues values = new ContentValues();

        //Use content values put method to store each of the key value pairs
        values.put(BoardsEntry.COLUMN_PRODUCT_NAME, "Firewire");
        values.put(BoardsEntry.COLUMN_PRICE, "$689.00");
        values.put(BoardsEntry.COLUMN_BOARD_TYPE, BoardsEntry.BOARD_TYPE_NOT_SPECIFIED);
        values.put(BoardsEntry.COLUMN_QUANTITY, "$689.00");
        values.put(BoardsEntry.COLUMN_SUPPLIER_NAME, "Global Industries");
        values.put(BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON, "Gabriel Medina");
        values.put(BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "US: +1-917-732-5401");

        //Capture the value that's returned by this insert method
        long newRowId = db.insert(BoardsEntry.TABLE_NAME, null, values);
        //This Log can catch any potential errors
        Log.v("CatalogActivity", "New row ID " + newRowId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                //Call both methods when option is selected
                insertBoard();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                Toast notJustYet = Toast.makeText(this, R.string.next_lesson_msg, Toast.LENGTH_LONG);
                notJustYet.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}