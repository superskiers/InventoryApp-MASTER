package com.example.superskiers.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.superskiers.inventoryapp.data.BoardsContract.BoardsEntry;



//Allows user to create a new product or edit an existing one.
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the surfboard data loader
    private static final int EXISTING_SURFBOARD_LOADER = 0;

    //Content URI for the existing product (null if it's a new product)
    private Uri mCurrentSurfboardUri;

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

    //Boolean that keeps track of whether the surfboard/product has been edited.
    private boolean mSurfboardHasChanged = false;


    private EditText quantityEditText;
    private ImageButton decrementButton;
    private String stock;
    public int newStock = 0;

    //OnTouchListener that listens for any user touches on a View, implying that they are
    //modifying the view and we change the mSurfboardHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSurfboardHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        quantityEditText = findViewById(R.id.edit_quantity_field);
        decrementButton = findViewById(R.id.decrement);

        //Examine the intent that was used to launch this activity, in order
        //to figure out if we're creating a new product entry or editing an existing one
        Intent intent = getIntent();
        mCurrentSurfboardUri = intent.getData();

        ImageButton phoneDialer = findViewById(R.id.phone_dialer);

        phoneDialer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phoneNumber = findViewById(R.id.edit_supplier_phone);
                Intent dialerIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber.getText().toString().trim()));
                startActivity(dialerIntent);
            }
        });


        //If the intent DOES NOT contain a product content URI, then we know that we
        //are creating a new product.
        if (mCurrentSurfboardUri == null) {
            //This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.editor_activity_title_new_product));

            //Invalidate the options menu, so the "Delete" menu option can be hidden.
            //(It doesn't make sense to delete a product that hasn't been created yet).
            invalidateOptionsMenu();
        } else {
            //Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));

            //Initialize a loader to read the product data from the database and display
            //the current values in the editor
            getLoaderManager().initLoader(EXISTING_SURFBOARD_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_quantity_field);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mSupplierContactPersonEditText = findViewById(R.id.edit_supplier_contact_person);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        mLengthSpinner = findViewById(R.id.spinner_board_type);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierContactPersonEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mLengthSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock = quantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(stock)) {
                    newStock = Integer.parseInt(stock);
                    quantityEditText.setText(String.valueOf(newStock - 1));
                }
                decrementButton(v);
                if(newStock == 0){
                    return;
                }
            }
        });
    }


    //Setup the dropdown spinner that allows the user to select the type of board being entered.
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

    //Get user input from editor and save product into database
    private void saveSurfboard() {
        //Read from the input fields
        //.trim gets rid of extra space or typing after name
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierContactString = mSupplierContactPersonEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        //If statement used to ensure all fields are filled in. If info is missing, will NOT save entry.
        if (mCurrentSurfboardUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(supplierContactString) && TextUtils.isEmpty(supplierPhoneString) &&
                mLength == BoardsEntry.BOARD_TYPE_NOT_SPECIFIED) {
            return;
        }

        //Create a content values object
        ContentValues values = new ContentValues();

        //Use content values put method to store each of the key value pairs
        values.put(BoardsEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BoardsEntry.COLUMN_PRICE, priceString);
        values.put(BoardsEntry.COLUMN_BOARD_TYPE, mLength);
        values.put(BoardsEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON, supplierContactString);
        values.put(BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);
        //If the quantity is not provided by the user, don't try to parse a string to an
        //integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BoardsEntry.COLUMN_QUANTITY, quantity);

        //Determine if this is a new or existing product by checking if mCurrentSurfboardUri is null or not
        if (mCurrentSurfboardUri == null) {
            //This is a NEW product, so insert a new surfboard into the provider,
            //returning the content URI for the new surfboard.
            Uri newUri = getContentResolver().insert(BoardsEntry.CONTENT_URI, values);

            //Show a Toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                //If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            //Otherwise this is an EXISTING product, so update the product with content URI: mCurrentSurfboardUri
            //and pass in the new ContentValues. Pass in null for the selection and selection args
            //because mCurrentSurfboardUri will already identify the correct row in the database that
            //we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentSurfboardUri, values, null, null);

            //Show a toast message depending on whether or not the update was successful.
            if(rowsAffected == 0) {
                //If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //This method is called after invalidateOptionsMenu(), so that the
    //menu can be updated (some menu items can be hidden or made visible).
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //If this is a new product, hide the "Delete" menu item.
        if (mCurrentSurfboardUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save product to the database
                saveSurfboard();
                //Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the product hasn't changed, continue with navigating up to parent
                // activity which is the {@link CatalogActivity}
                if (!mSurfboardHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
        }
        //Otherwise if there are unsaved changes, setup a dialog to warn the user.
        //Create a click listener to handle the user confirming that
        //changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User clicked "Discard" button, navigate to parent activity
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
            };

            //Show a dialog that notifies the user they have unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //This method is called when the back button is pressed
    @Override
    public void onBackPressed() {
        //If the product hasn't changed, continue with handling back button press
        if(!mSurfboardHasChanged) {
            super.onBackPressed();
            return;
    }

    //Otherwise if there are unsaved changes, setup a dialog to warn the user.
    //Create a click listener to handle the user confirming that changes should be discarded
    DialogInterface.OnClickListener discardButtonClickListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //User clicked "Discard" button, close the current activity
                    finish();
                }
            };
        //Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
}

    //Called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Since the editor shows all surfboard attributes, define a projection that contains
        //all columns from the surfboards table
        String[] projection = {
                BoardsEntry._ID,
                BoardsEntry.COLUMN_PRODUCT_NAME,
                BoardsEntry.COLUMN_PRICE,
                BoardsEntry.COLUMN_QUANTITY,
                BoardsEntry.COLUMN_BOARD_TYPE,
                BoardsEntry.COLUMN_SUPPLIER_NAME,
                BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON,
                BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
        };
        //This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,    //Parent activity context
                mCurrentSurfboardUri,           //Provider content URI to query
                projection,                     //Columns to include in the resulting Cursor
                null,                  //No selection clause
                null,               //No selection arguments
                null );                //Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    //Bail early if the cursor is null or there is less then 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of products attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_QUANTITY);
            int boardTypeColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_BOARD_TYPE);
            int supplierColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_SUPPLIER_NAME);
            int supplierContactColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_SUPPLIER_CONTACT_PERSON);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BoardsEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierContact = cursor.getString(supplierContactColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            int length = cursor.getInt(boardTypeColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mSupplierContactPersonEditText.setText(supplierContact);
            mSupplierPhoneEditText.setText(supplierPhone);

            // Board Type is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Not Specified, 1 is Short Board, 2 is Long Board).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (length) {
                case BoardsEntry.BOARD_TYPE_SHORT:
                    mLengthSpinner.setSelection(1);
                    break;
                case BoardsEntry.BOARD_TYPE_LONG:
                    mLengthSpinner.setSelection(2);
                    break;
                default:
                    mLengthSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mQuantityEditText.setText("");
        mQuantityEditText.setText("");
        mQuantityEditText.setText("");
        mLengthSpinner.setSelection(0); // Select "unspecified" type
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the surfboard in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing surfboard.
        if (mCurrentSurfboardUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentSurfboardUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentSurfboardUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    public void decrementButton(View view) {
        if(newStock != 0)
            newStock--;
        displayQuantity();
    }
    public void displayQuantity() {
        stock = quantityEditText.getText().toString().trim();
        quantityEditText.setText(String.valueOf(newStock));
    }

//    public void orderMore() {
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse(mSupplierPhoneEditText.getText().toString().trim()));
//        startActivity(intent);

}
