package com.example.superskiers.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.superskiers.inventoryapp.data.BoardsContract;


/**
     * {@link BoardCursorAdapter} is an adapter for a list or grid view
     * that uses a {@link Cursor} of surfboard data as its data source. This adapter knows
     * how to create list items for each row of surfboard data in the {@link Cursor}.
     */
    public class BoardCursorAdapter extends CursorAdapter {

        /**
         * Constructs a new {@link BoardCursorAdapter}.
         *
         * @param context The context
         * @param c       The cursor from which to get the data.
         */
        public BoardCursorAdapter(Context context, Cursor c) {
            super(context, c, 0 /* flags */);
        }

        /**
         * Makes a new blank list item view. No data is set (or bound) to the views yet.
         *
         * @param context app context
         * @param cursor  The cursor from which to get the data. The cursor is already
         *                moved to the correct position.
         * @param parent  The parent to which the new view is attached to
         * @return the newly created list item view.
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //Inflate a list item view using the layout specified in list_item.xml
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        /**
         * This method binds the surfboard data (in the current row pointed to by cursor) to the given
         * list item layout. For example, the name for the current product can be set on the name TextView
         * in the list item layout.
         *
         * @param view    Existing view, returned earlier by newView() method
         * @param context app context
         * @param cursor  The cursor from which to get the data. The cursor is already moved to the
         *                correct row.
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //Find the fields to populate in inflated layout
            TextView nameTextView = view.findViewById(R.id.name);
            TextView summaryTextView = view.findViewById(R.id.summary);

            //Find the columns of product attributes that we're interested in.
            int nameColumnIndex = cursor.getColumnIndex(BoardsContract.BoardsEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BoardsContract.BoardsEntry.COLUMN_PRICE);

            //Read the surfboards attributes from the Cursor for the current product
            String boardName = cursor.getString(nameColumnIndex);
            String boardPrice = cursor.getString(priceColumnIndex);

            //If the surfboard price is an empty string or null, then use some default text
            //that says "Unknown Price", so the TextView isn't blank.
            if (TextUtils.isEmpty(boardPrice)){
                boardPrice = context.getString(R.string.unknown_price);
            }

            //Update the TextViews with the attributes for the current product
            nameTextView.setText(boardName);
            summaryTextView.setText(String.valueOf(boardPrice));
        }
    }

