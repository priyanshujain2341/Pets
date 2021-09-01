package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private PetDbHelper mDbHelper;

    private static final int PET_LOADER = 0;
    PetCursorAdapter mCursorAdapter;

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

        mDbHelper = new PetDbHelper(this);

        ListView petListView = (ListView) findViewById(R.id.list);
        View emptyView  = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        mCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        //Start the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }


    private void deleteAllPets()
    {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        displayDatabaseInfo();
//    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
//    private void displayDatabaseInfo() {
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
////        PetDbHelper mDbHelper = new PetDbHelper(this);
//
//        // Create and/or open a database to read from it
////        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//
//        String[] projections = {
//                PetEntry._ID,
//                PetEntry.COLUMN_PET_NAME,
//                PetEntry.COLUMN_PET_BREED,
//                PetEntry.COLUMN_PET_GENDER,
//                PetEntry.COLUMN_PET_WEIGHT};
//
////        String selection = " WHERE ";
//        // Perform this raw SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
////        Cursor cursor = db.query(
////                PetEntry.TABLE_NAME, projections,
////                null,
////                null,
////                null,
////                null,
////                null,
////                null );
//
//        Cursor cursor = getContentResolver().query(
//                PetEntry.CONTENT_URI,
//                projections,
//                null,
//                null,
//                null);
//
////        TextView textViewPet = (TextView) findViewById(R.id.text_view_pet);
////
////        try
////        {
////            textViewPet.setText("The pets table contains "+cursor.getCount()+" pets.\n\n");
////            int idIndex = cursor.getColumnIndex(PetEntry._ID);
////            int nameIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
////            int breedIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
////            int genderIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
////            int weightIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
////
////            textViewPet.append(PetEntry._ID +" - "
////                    +PetEntry.COLUMN_PET_NAME +" - "
////                    +PetEntry.COLUMN_PET_BREED +" - "
////                    +PetEntry.COLUMN_PET_GENDER +" - "
////                    +PetEntry.COLUMN_PET_WEIGHT+ "\n\n");
////
////            while(cursor.moveToNext())
////            {
////                int id = cursor.getInt(idIndex);
////                String name = cursor.getString(nameIndex);
////                String breed = cursor.getString(breedIndex);
////                int gender = cursor.getInt(genderIndex);
////                int weight = cursor.getInt(weightIndex);
////
////                textViewPet.append(
////                        id + " - "+
////                                name + " - " +
////                                breed + " - " +
////                                gender + " - "
////                                + weight );
////                textViewPet.append("\n");
////            }
////        }
////        finally
////        {
////            // Always close the cursor when you're done reading from it. This releases all its
////            // resources and makes it invalid.
////            cursor.close();
////        }
//
//
//        ListView petListView = (ListView) findViewById(R.id.list);
//
//        PetCursorAdapter adapter = new PetCursorAdapter(this, cursor);
//
//        petListView.setAdapter(adapter);
//    }

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
                // Do nothing for now
                insertPet();
//                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet()
    {
//        PetDbHelper mDbHelper = new PetDbHelper(this);
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED };

        return new CursorLoader(
                this,
                PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        //Callbackk called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}