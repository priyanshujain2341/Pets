package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.pets.data.PetContract.*;

import com.example.android.pets.data.PetContract.*;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider
{
    private PetDbHelper mDbHelper;

    public static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PetContract.PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS+"/#", PetContract.PET_ID);
    }


    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate()
    {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = null;
//        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PetContract.PETS:
            {
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor = db.query(PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case PetContract.PET_ID:
            {
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor= db.query(PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                Log.e(LOG_TAG, "Uri is: "+uri+"\n match is: "+match );
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PetContract.PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values)
    {
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if(name==null)
        {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if(gender==null || !PetEntry.isValidGender(gender))
        {
            throw new IllegalArgumentException("Pet requires a valid gender");
        }

        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if(weight!=null && weight<0)
        {
            throw new IllegalArgumentException("Pet requires a valid weight");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(PetEntry.TABLE_NAME, null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it

        if(id==-1)
        {
            Log.e(LOG_TAG, "Failed to insert for uri: "+uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PetContract.PETS:
            {
                return updatePet(uri, contentValues, selection, selectionArgs);
            }
            case PetContract.PET_ID:
            {
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            }
            default:
                throw new IllegalArgumentException("Update is not supported for "+uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {

        if(values.size()==0)
        {
            return 0;
        }

        if(values.containsKey(PetEntry.COLUMN_PET_NAME))
        {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if(name==null)
            {
                throw new IllegalArgumentException("Pet requires a valid name");
            }
        }

        if(values.containsKey(PetEntry.COLUMN_PET_GENDER))
        {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if(gender==null || !PetEntry.isValidGender(gender))
            {
                throw new IllegalArgumentException("Pet requires a valid gender");
            }
        }

        if(values.containsKey(PetEntry.COLUMN_PET_WEIGHT))
        {
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if(weight!=null && weight<0)
            {
                throw new IllegalArgumentException("Pet requires a valid weight");
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated!=0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int rowsDeleted;
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (match)
        {
            case PetContract.PETS:
            {
                rowsDeleted = db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case PetContract.PET_ID:
            {
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted =  db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }

        if(rowsDeleted!=0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PetContract.PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PetContract.PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri+" with match: "+match);
        }
    }
}