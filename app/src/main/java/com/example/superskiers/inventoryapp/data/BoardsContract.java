package com.example.superskiers.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


//BoardsContract defines the Schema of our database
//API Contract for the Inventory app.
public class BoardsContract {


        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        private BoardsContract() {}

    //String constant set up with the Content_Provider
    //The "Content authority" is a name for the entire content provider, similar to the
    //relationship between a domain name and its website.  A convenient string to use for the
    //content authority is the package name for the app, which is guaranteed to be unique on the
    //device.
    public static final String CONTENT_AUTHORITY = "com.example.superskiers.inventoryapp";

    //Concatenate the CONTENT_AUTHORITY constant with scheme
    //Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    //the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //String Constant stores the path for each of the tables to be appended
    //Possible path (appended to base content URI for possible URI's)
    //For instance, content://com.example.superskiers.inventoryapp/surfboards/ is a valid path for
    //looking at surfboard data. content://com.example.superskiers.inventoryapp/shape/ will fail,
    //as the ContentProvider hasn't been given any information on what to do with "shape".
    public static final String PATH_BOARDS = "surfboards";


    //Inner class that defines constant values for the surfboards database table.
    //Each entry in the table represents a single surfboard (product).
    public static final class BoardsEntry implements BaseColumns {


        //The content URI to access the product data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOARDS);


        //The MIME type of the {@link #CONTENT_URI} for a list of products.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOARDS;

        //The MIME type of the {@link #CONTENT_URI} for a single product.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOARDS;


        //Name of database table for InventoryApp
        public final static String TABLE_NAME = "surfboards";


        //Unique ID number for the product (only for use in the database table).

        //Type: INTEGER

        public final static String _ID = BaseColumns._ID;


        //Name of the specific surfboard/product.

        //Type: TEXT

        public final static String COLUMN_PRODUCT_NAME = "name";


        //Price of the surfboard.

        //Type: TEXT

        public final static String COLUMN_PRICE = "price";


        //Length of surfboard.

        //The only possible values are {@link #BOARD_TYPE_NOT_SPECIFIED}, {@link #BOARD_TYPE_SHORT},
        //or {@link #BOARD_TYPE_LONG}.

        //Type: INTEGER

        public final static String COLUMN_BOARD_TYPE = "board_type";


        //Quantity of the product.

        //Type: TEXT

        public final static String COLUMN_QUANTITY = "quantity";


        //Supplier Name of the product.

        //Type: TEXT

        public final static String COLUMN_SUPPLIER_NAME = "supplier";


        //Contact Person at Supplier.

        //Type: TEXT

        public final static String COLUMN_SUPPLIER_CONTACT_PERSON = "contact_person";


        //Phone Number of the Supplier.

        //Type: TEXT

        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone";


        //Possible values for the type/length of surfboard.

        public static final int BOARD_TYPE_NOT_SPECIFIED = 0;
        public static final int BOARD_TYPE_SHORT = 1;
        public static final int BOARD_TYPE_LONG = 2;

        //Returns whether or not the given Board Type is {@link #BOARD_TYPE_NOT_SPECIFIED},
        //{@link #BOARD_TYPE_SHORT}, {@link #BOARD_TYPE_LONG}
        public static boolean isLengthValid(int lengthOfBoard) {
            return lengthOfBoard == BOARD_TYPE_NOT_SPECIFIED || lengthOfBoard == BOARD_TYPE_SHORT
                    || lengthOfBoard == BOARD_TYPE_LONG;
        }
    }
}

