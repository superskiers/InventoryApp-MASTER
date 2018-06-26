package com.example.superskiers.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.superskiers.inventoryapp.data.BoardsContract.BoardsEntry;

//Displays the list of products that were entered and stored in the app.
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Integer Loader constant
    private static final int BOARD_LOADER = 0;

    //Create an instance of that Class
    BoardCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //Find the ListView which will be populated with the product data
        ListView boardListView = findViewById(R.id.list);

        //Find and set empty view on the ListView, so that it only shows
        //when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        boardListView.setEmptyView(emptyView);


        //Setup an Adapter to create a list item for each row of product data in the Cursor.
        //There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BoardCursorAdapter(this, null);
        boardListView.setAdapter(mCursorAdapter);

        //Setup on item click listener
        boardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                //Form the content URI that represents the specific product that was clicked on
                //by appending the "id" (passed as input to this method) onto the
                //{@link BoardsEntry#CONTENT_URI}.
                Uri currentProductUri = ContentUris.withAppendedId(BoardsEntry.CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentProductUri);

                Toast.makeText(CatalogActivity.this, R.string.update_database_catalogactivity, Toast.LENGTH_SHORT).show();


                //Launch the {@link EditorActivity} to display the data for the current product
                startActivity(intent);
            }
        });
        //Kick off the loader
       getLoaderManager().initLoader(BOARD_LOADER, null, this);
    }

    private void insertBoard() {
        //Create a content values object where column names are the keys,
        //and the FireWire board attributes are the values.
        ContentValues values = new ContentValues();

        //Use content values put method to store each of the key value pairs
        values.put(BoardsEntry.COLUMN_PRODUCT_NAME, "Firewire Everyday Go Fish");
        values.put(BoardsEntry.COLUMN_PRICE, "689.99");
        values.put(BoardsEntry.COLUMN_BOARD_TYPE, BoardsEntry.BOARD_TYPE_NOT_SPECIFIED);
        values.put(BoardsEntry.COLUMN_QUANTITY, "12");
        values.put(BoardsEntry.COLUMN_SUPPLIER_NAME, "Global Industries");
        values.put(BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON, "Gabriel Medina");
        values.put(BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "1-917-732-5401");

        //Insert a new row for FireWire into the provider using the ContentResolver
        //Use the {@link BoardsEntry#CONTENT_URI} to indicate that we want to insert
        //into the surfboards database table.
        //Receive the new content URI that will allow us to access FireWire's data in the future.
        Uri newUri = getContentResolver().insert(BoardsEntry.CONTENT_URI, values);

    }

    //Helper method to delete ALL products in the database.
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(BoardsEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from surfboard database");

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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies which columns from the database
        //you will actually use after this query.
        String[] projection = {
                BoardsEntry._ID,
                BoardsEntry.COLUMN_PRODUCT_NAME,
                BoardsEntry.COLUMN_PRICE,
                BoardsEntry.COLUMN_QUANTITY};
        //This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,    //Parent activity context
                BoardsEntry.CONTENT_URI,        //Provider content URI to query
                projection,                     //Columns to include in the resulting Cursor
                null,                  //No selection clause
                null,               //No selection arguments
                null);                //Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update {@link BoardCursorAdapter} with this new cursor containing updated product data.
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted.
        mCursorAdapter.swapCursor(null);

    }
}