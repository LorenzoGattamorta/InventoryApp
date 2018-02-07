package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ItemContract;

/**
 * Created by Lorenzo on 20/07/17.
 */

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        TextView itemName = (TextView) view.findViewById(R.id.text_view_name);
        TextView itemPrice = (TextView) view.findViewById(R.id.text_view_price);
        TextView itemQuantity = (TextView) view.findViewById(R.id.text_view_quantity);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_QUANTITY));
        final Uri uri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry._ID)));

        itemName.setText(name);
        itemPrice.setText(context.getString(R.string.label_price) + " " + price);
        itemQuantity.setText(quantity + " " + context.getString(R.string.label_quantity));

        Button saleButton = (Button) view.findViewById(R.id.button_sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, newQuantity);
                    context.getContentResolver().update(uri, values, null, null);
                }
            }
        });
    }
}
