package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetDbHelper mDbHelper;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
//        PetDbHelper mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        String[] projections = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT };
//        String selection = " WHERE ";
        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(
                PetEntry.TABLE_NAME, projections,
                null,
                null,
                null,
                null,
                null,
                null );

        TextView textViewPet = (TextView) findViewById(R.id.text_view_pet);

        try
        {
            textViewPet.setText("The pets table contains "+cursor.getCount()+" pets.\n\n");
            int idIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            textViewPet.append(PetEntry._ID +" - "
                    +PetEntry.COLUMN_PET_NAME +" - "
                    +PetEntry.COLUMN_PET_BREED +" - "
                    +PetEntry.COLUMN_PET_GENDER +" - "
                    +PetEntry.COLUMN_PET_WEIGHT+ "\n\n");

            while(cursor.moveToNext())
            {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String breed = cursor.getString(breedIndex);
                int gender = cursor.getInt(genderIndex);
                int weight = cursor.getInt(weightIndex);

                textViewPet.append(
                        id + " - "+
                                name + " - " +
                                breed + " - " +
                                gender + " - "
                                + weight );
                textViewPet.append("\n");
            }
        }
        finally
        {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
                // Do nothing for now
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet()
    {
//        PetDbHelper mDbHelper = new PetDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(PetEntry._ID, 1);
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        long id = db.insert(PetEntry.TABLE_NAME, null, values);
    }
}