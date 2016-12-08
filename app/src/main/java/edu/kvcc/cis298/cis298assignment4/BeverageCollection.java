package edu.kvcc.cis298.cis298assignment4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import edu.kvcc.cis298.cis298assignment4.database.BeverageBaseHelper;
import edu.kvcc.cis298.cis298assignment4.database.BeverageCursorWrapper;
import edu.kvcc.cis298.cis298assignment4.database.BeverageDBSchema;
import edu.kvcc.cis298.cis298assignment4.database.BeverageDBSchema.BeverageTable;

/**
 * Created by David Barnes on 11/3/2015.
 * This is a singleton that will store the data for our application
 */
public class BeverageCollection {

    //Static variable that represents this class
    private static BeverageCollection sBeverageCollection;

    private SQLiteDatabase mDatabase;// >The database.

    //private variable for the context that the singleton operates in
    private Context mContext;

    //public static method to get the single instance of this class
    public static BeverageCollection get(Context context) {
        //If the collection is null
        if (sBeverageCollection == null) {
            //make a new one
            sBeverageCollection = new BeverageCollection(context);
        }
        //regardless of whether it was just made or not, return the instance
        return sBeverageCollection;
    }

    //Private constructor to create a new BeverageCollection
    private BeverageCollection(Context context) {
        //Set the context to the one that is passed in
        mContext = context.getApplicationContext();
        // >Set the database.
        mDatabase = new BeverageBaseHelper(mContext).getWritableDatabase();
    }


    // >Add a beverage to the database.
    public void addBeverage(Beverage beverage) {
        ContentValues values = getContentValues(beverage);
        // >Insert a new record into the database.
        mDatabase.insert(BeverageTable.NAME, null, values);
    }

    public List<Beverage> getBeverages() {
        List<Beverage> beverages = new ArrayList<>();// >List to hold the beverages.

        // >Create a cursor wrapper with no where clause or arguments.
        BeverageCursorWrapper cursorWrapper = queryBeverages(null, null);

        // >Add all of the beverages from the database to the list.
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                beverages.add(cursorWrapper.getBeverage());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return beverages;
    }

    public Beverage getBeverage(UUID uuid) {
        // >Create a cursor wrapper to get a single beverage.
        BeverageCursorWrapper cursorWrapper = queryBeverages(
                BeverageTable.Cols.UUID + " = ?",
                new String[] {uuid.toString()}
        );

        try {
            // >Return if there was no result.
            if (cursorWrapper.getCount() == 0) {
                return null;
            }

            cursorWrapper.moveToFirst();
            return cursorWrapper.getBeverage();
        } finally {
            cursorWrapper.close();
        }
    }

    public Beverage getBeverageByString(String id) {
        // >Create a cursor wrapper to get a single beverage.
        BeverageCursorWrapper cursorWrapper = queryBeverages(
                BeverageTable.Cols.ID + " = ?",
                new String[] {id}
        );

        try {
            // >Return if there was no result.
            if (cursorWrapper.getCount() == 0) {
                return null;
            }

            cursorWrapper.moveToFirst();
            return cursorWrapper.getBeverage();
        } finally {
            cursorWrapper.close();
        }
    }

    public void updateBeverage(Beverage beverage) {
        // >Convert the UUID so it can be used in the where clause.
        String uuidString = beverage.getUUID().toString();
        // >Get the content values from the beverage.
        ContentValues values = getContentValues(beverage);
        // >Update the database.
        mDatabase.update(BeverageTable.NAME, values,
                BeverageTable.Cols.UUID + " = ?",
                new String[] {uuidString}
        );
    }

    private static ContentValues getContentValues(Beverage beverage) {
        ContentValues values = new ContentValues();

        values.put(BeverageTable.Cols.ID, beverage.getId());
        values.put(BeverageTable.Cols.NAME, beverage.getName());
        values.put(BeverageTable.Cols.PACK, beverage.getPack());
        values.put(BeverageTable.Cols.PRICE, beverage.getPrice());
        values.put(BeverageTable.Cols.ACTIVE, beverage.isActive() ? 1 : 0);

        return values;
    }

    private BeverageCursorWrapper queryBeverages(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BeverageTable.NAME,
                null,// >columns
                whereClause,
                whereArgs,
                null,// >group by
                null,// >having
                null// >order by
        );
        return new BeverageCursorWrapper(cursor);
    }



}
