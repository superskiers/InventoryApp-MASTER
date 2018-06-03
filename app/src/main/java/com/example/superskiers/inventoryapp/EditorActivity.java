package com.example.superskiers.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.superskiers.inventoryapp.data.BoardsContract.BoardsEntry;
import com.example.superskiers.inventoryapp.data.SurfboardDbHelper;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    //EditText field to enter the name of the surfboard
    private EditText mNameEditText;

    //EditText field to enter the price of the surfboard
    private EditText mPriceEditText;

    //EditText field to enter the quantity of stock
    private EditText mQuantityEditText;

    //EditText field to enter the supplier name
    private EditText mSupplierEditText;

    //EditText field to enter the supplier phone number
    private EditText mSupplierPhoneEditText;

    //EditText field to enter the supplier contact person
    private EditText mSupplierContactPersonEditText;

    //EditText field to enter the board length type (short or long)
    private Spinner mLengthSpinner;


    //Length of the board. A choice between long board, short board or not specified.
    //{@link BoardsEntry#LONG_BOARD}, {@link BoardsEntry#SHORT_BOARD}.
    //{@link BoardsEntry#LENGTH_NOT_SPECIFIED}
    private int mLength = BoardsEntry.BOARD_TYPE_NOT_SPECIFIED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_quantity_field);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mSupplierContactPersonEditText = findViewById(R.id.edit_supplier_contact_person);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        mLengthSpinner = findViewById(R.id.spinner_board_type);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter lengthSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_board_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        lengthSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mLengthSpinner.setAdapter(lengthSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mLengthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.board_length_short))) {
                        mLength = BoardsEntry.BOARD_TYPE_SHORT;
                    } else if (selection.equals(getString(R.string.board_length_long))) {
                        mLength = BoardsEntry.BOARD_TYPE_LONG;
                    } else {
                        mLength = BoardsEntry.BOARD_TYPE_NOT_SPECIFIED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLength = BoardsEntry.BOARD_TYPE_NOT_SPECIFIED;
            }
        });
    }

    //Get user input from editor and save new product into database
    private void insertProduct() {
        //Read from the input fields
        //.trim gets rid of extra space or typing after name
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = "$" + mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierContactString = mSupplierContactPersonEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);


        SurfboardDbHelper mDbHelper = new SurfboardDbHelper(this);

        //Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Create a content values object
        ContentValues values = new ContentValues();

        //Use content values put method to store each of the key value pairs
        values.put(BoardsEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BoardsEntry.COLUMN_PRICE, priceString);
        values.put(BoardsEntry.COLUMN_BOARD_TYPE, mLength);
        values.put(BoardsEntry.COLUMN_QUANTITY, quantity);
        values.put(BoardsEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON, supplierContactString);
        values.put(BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        //Capture the value that's returned by this insert method
        long newRowId = db.insert(BoardsEntry.TABLE_NAME, null, values);
        //Show a Toas message as to whether info was entered successfully
        if(newRowId == -1) {
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save product to the database
                insertProduct();
                //Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //For now: DELETE will navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}