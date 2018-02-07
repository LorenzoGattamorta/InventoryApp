package com.example.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract;

import java.io.ByteArrayOutputStream;

/**
 * Created by Lorenzo on 20/07/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private EditText mName;

    private EditText mQuantityText;

    private EditText mPrice;

    private TextView mQuantityView;

    private String mProductName;
    private int mProductQuantity;

    private Button mIncreaseQuantityByOne;
    private Button mDecreaseQuantityByOne;
    private Button mIncreaseQuantityLargeButton;
    private Button mDecreaseQuantityLargeButton;

    private final static int SELECT_PHOTO = 200;

    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 666;    // Yes, 666! Took me a while to figure this beast!

    private Button mSelectImage;
    private ImageView mProductImage;
    private Bitmap mProductBitmap;

    public TextView mOrderButton;

    private static final String URI_EMAIL = "mailto:";

    private static final int URI_LOADER = 0;

    private Uri mProductUri;

    private boolean mProductHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mProductUri = intent.getData();

        if (mProductUri != null) {
            setTitle(R.string.activity_detail_edit);
            getLoaderManager().initLoader(URI_LOADER, null, this);
        } else {
            setTitle(R.string.activity_detail_new);
            invalidateOptionsMenu();
        }

        initialiseViews();

        setOnTouchListener();
    }

    private void initialiseViews() {
        if (mProductUri != null) {
            mOrderButton = (TextView) findViewById(R.id.button_order_from_supplier);
            mOrderButton.setVisibility(View.VISIBLE);
            mOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setData(Uri.parse("mailto:"));
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.supplier_email));
                    intent.putExtra(Intent.EXTRA_SUBJECT, mProductName);
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        }

        mName = (EditText) findViewById(R.id.edit_text_name);
        mQuantityText = (EditText) findViewById(R.id.edit_text_quantity);
        mPrice = (EditText) findViewById(R.id.edit_text_price);

        mQuantityView = (TextView) findViewById(R.id.text_view_quantity_final);

        mIncreaseQuantityByOne = (Button) findViewById(R.id.button_increase_one);
        mIncreaseQuantityByOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductQuantity++;
                mQuantityView.setText(String.valueOf(mProductQuantity));

            }
        });

        mDecreaseQuantityByOne = (Button) findViewById(R.id.button_decrease_one);
        mDecreaseQuantityByOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProductQuantity > 0) {
                    mProductQuantity--;
                    mQuantityView.setText(String.valueOf(mProductQuantity));
                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.toast_invalid_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIncreaseQuantityLargeButton = (Button) findViewById(R.id.button_increase_n);
        mIncreaseQuantityLargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantityText.getText()) && Integer.valueOf(mQuantityText.getText().toString()) > 0) {
                    mProductQuantity += Integer.valueOf(mQuantityText.getText().toString());
                    mQuantityView.setText(String.valueOf(mProductQuantity));
                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.toast_missing_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDecreaseQuantityLargeButton = (Button) findViewById(R.id.button_decrease_n);
        mDecreaseQuantityLargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantityText.getText()) && Integer.valueOf(mQuantityText.getText().toString()) > 0) {
                    int newQuantity = mProductQuantity - Integer.valueOf(mQuantityText.getText().toString());
                    if (newQuantity < 0) {
                        Toast.makeText(EditorActivity.this, getString(R.string.toast_invalid_quantity), Toast.LENGTH_SHORT).show();
                    } else {
                        mProductQuantity -= Integer.valueOf(mQuantityText.getText().toString());
                        mQuantityView.setText(String.valueOf(mProductQuantity));
                    }
                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.toast_missing_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mProductImage = (ImageView) findViewById(R.id.image);

        mSelectImage = (Button) findViewById(R.id.button_select_image);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                        return;
                    }

                    Intent getIntent = new Intent(Intent.ACTION_PICK);
                    getIntent.setType("image/*");
                    startActivityForResult(getIntent, SELECT_PHOTO);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mProductBitmap = BitmapFactory.decodeFile(picturePath);
            mProductImage.setImageBitmap(mProductBitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (mProductHasChanged) {
                    saveProduct();
                } else {
                    Toast.makeText(this, getString(R.string.toast_insert_or_update_product_failed), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                } else {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };

                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

          DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void saveProduct() {
        boolean nameIsEmpty = checkFieldEmpty(mName.getText().toString().trim());
        boolean priceIsEmpty = checkFieldEmpty(mPrice.getText().toString().trim());

        if (nameIsEmpty) {
            Toast.makeText(this, getString(R.string.toast_invalid_name_add), Toast.LENGTH_SHORT).show();
        } else if (mProductQuantity <= 0) {
            Toast.makeText(this, getString(R.string.toast_invalid_quantity_add), Toast.LENGTH_SHORT).show();
        } else if (priceIsEmpty) {
            Toast.makeText(this, getString(R.string.toast_invalid_price_add), Toast.LENGTH_SHORT).show();
        } else if (mProductBitmap == null) {
            Toast.makeText(this, getString(R.string.toast_invalid_image_add), Toast.LENGTH_SHORT).show();
        } else {
            String name = mName.getText().toString().trim();
            double price = Double.parseDouble(mPrice.getText().toString().trim());

            ContentValues values = new ContentValues();
            values.put(ItemContract.ItemEntry.COLUMN_NAME, name);
            values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, mProductQuantity);
            values.put(ItemContract.ItemEntry.COLUMN_PRICE, price);
            byte[] image = getBytes(mProductBitmap);
            values.put(ItemContract.ItemEntry.COLUMN_IMAGE, image);

            if (mProductUri == null) {
                Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
            } else {
                int newUri = getContentResolver().update(mProductUri, values, null, null);
            }
            finish();
        }
    }


    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    private boolean checkFieldEmpty(String string) {
        return TextUtils.isEmpty(string) || string.equals(".");
    }


    private void deleteItem() {
        if (mProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.toast_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_delete_product_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getString(R.string.prompt_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void setOnTouchListener() {
        mName.setOnTouchListener(mTouchListener);
        mQuantityText.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mIncreaseQuantityByOne.setOnTouchListener(mTouchListener);
        mDecreaseQuantityByOne.setOnTouchListener(mTouchListener);
        mIncreaseQuantityLargeButton.setOnTouchListener(mTouchListener);
        mDecreaseQuantityLargeButton.setOnTouchListener(mTouchListener);
        mSelectImage.setOnTouchListener(mTouchListener);
    }

    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URI_LOADER:
                return new CursorLoader(
                        this,
                        mProductUri,
                        null,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mProductName = data.getString(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME));
            mName.setText(mProductName);
            mPrice.setText(data.getString(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE)));
            mProductQuantity = data.getInt(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));
            mQuantityView.setText(String.valueOf(mProductQuantity));
            mProductBitmap = getImage(data.getBlob(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_IMAGE)));
            mProductImage.setImageBitmap(mProductBitmap);        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.getText().clear();
        mQuantityText.getText().clear();
        mQuantityView.setText("");
    }
}
