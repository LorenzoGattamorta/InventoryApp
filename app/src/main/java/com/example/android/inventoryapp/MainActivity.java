package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ItemContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ItemCursorAdapter mItemCursorsAdapter;
    private static final int URI_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView editor = (TextView) findViewById(R.id.entries);
        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(MainActivity.this, EditorActivity.class );
                startActivity(editorIntent);
            }

        });


        getLoaderManager().initLoader(URI_LOADER, null, this);

        initaliseListView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case URI_LOADER:
                String projection[] = {
                        ItemContract.ItemEntry._ID,
                        ItemContract.ItemEntry.COLUMN_NAME,
                        ItemContract.ItemEntry.COLUMN_PRICE,
                        ItemContract.ItemEntry.COLUMN_QUANTITY
                };
                String sortOrder =
                        ItemContract.ItemEntry._ID + " DESC";
                return new CursorLoader(
                        this,
                        ItemContract.ItemEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            mItemCursorsAdapter.swapCursor(data);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemCursorsAdapter.swapCursor(null);
    }

    private void initaliseListView() {
        ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        mItemCursorsAdapter = new ItemCursorAdapter(this, null, false);
        listView.setAdapter(mItemCursorsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
    }






}
