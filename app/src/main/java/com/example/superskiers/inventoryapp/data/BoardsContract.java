package com.example.superskiers.inventoryapp.data;

import android.provider.BaseColumns;


//BoardsContract defines the Schema of our database
//API Contract for the Inventory app.
public class BoardsContract {


        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        private BoardsContract() {}


        //Inner class that defines constant values for the surfboards database table.
        //Each entry in the table represents a single surfboard.
        public static final class BoardsEntry implements BaseColumns {

            //Name of database table for InventoryApp
            public final static String TABLE_NAME = "surfboards";


            //Unique ID number for the product (only for use in the database table).

            //Type: INTEGER

            public final static String _ID = BaseColumns._ID;


            //Name of the specific board/product.

            //Type: TEXT

            public final static String COLUMN_PRODUCT_NAME ="name";


            //Price of the board.

            //Type: TEXT

            public final static String COLUMN_PRICE = "price";


            //Length of board.

            //The only possible values are {@link #BOARD_TYPE_NOT_SPECIFIED}, {@link #BOARD_TYPE_SHORT},
            //or {@link #BOARD_TYPE_LONG}.

            //Type: INTEGER

            public final static String COLUMN_BOARD_TYPE = "board_type";


            //Quantity of the product.

            //Type: INTEGER

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


            //Possible values for the type/length of board.

            public static final int BOARD_TYPE_NOT_SPECIFIED = 0;
            public static final int BOARD_TYPE_SHORT = 1;
            public static final int BOARD_TYPE_LONG = 2;
        }

    }

