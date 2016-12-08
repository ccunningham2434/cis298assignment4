package edu.kvcc.cis298.cis298assignment4.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Scanner;

import edu.kvcc.cis298.cis298assignment4.Beverage;
import edu.kvcc.cis298.cis298assignment4.BeverageCollection;
import edu.kvcc.cis298.cis298assignment4.BeverageListFragment;
import edu.kvcc.cis298.cis298assignment4.database.BeverageDBSchema.BeverageTable;

/**
 * Created by ccunn on 05-Dec-16.
 */

public class BeverageBaseHelper extends SQLiteOpenHelper {

    Context mContext;// >The context for this class.
    boolean mShouldSeed; // >Flag weather to seed the database or not.

    private SQLiteDatabase mDatabase;// >The database.

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "beverageBase.db";

    public BeverageBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
        // >Do not seed the database.
        mShouldSeed = false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BeverageTable.NAME
            + "(" + " _id integer primary key autoincrement, "
                + BeverageTable.Cols.UUID + ","
            + BeverageTable.Cols.ID + ","
            + BeverageTable.Cols.NAME + ","
            + BeverageTable.Cols.PACK + ","
            + BeverageTable.Cols.PRICE + ","
            + BeverageTable.Cols.ACTIVE
            + ")"
        );

        mDatabase = db;
        // >Mark that the database should be seeded..
        mShouldSeed = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // >Seed the database if needed.
        if (mShouldSeed) {
            new FetchBeveragesTask().execute();
        }
    }

    // >Seed the database from a web server.
    private class FetchBeveragesTask extends AsyncTask<Void, Void, List<Beverage>> {
        @Override
        protected List<Beverage> doInBackground(Void... params) {
            return new BeverageFetcher().fetchBeverages();
        }

        @Override
        protected void onPostExecute(List<Beverage> beverageList) {
            for (Beverage beverage : beverageList) {
                ContentValues values = getContentValues(beverage);
                // >Insert a new record into the database.
                mDatabase.insert(BeverageTable.NAME, null, values);
            }

            BeverageListFragment.staticSelf.setupAdapter();
            BeverageListFragment.staticSelf.updateUI();
        }
    }

    private static ContentValues getContentValues(Beverage beverage) {
        ContentValues values = new ContentValues();

        values.put(BeverageTable.Cols.UUID, beverage.getUUID().toString());
        values.put(BeverageTable.Cols.ID, beverage.getId());
        values.put(BeverageTable.Cols.NAME, beverage.getName());
        values.put(BeverageTable.Cols.PACK, beverage.getPack());
        values.put(BeverageTable.Cols.PRICE, beverage.getPrice());
        values.put(BeverageTable.Cols.ACTIVE, beverage.isActive() ? 1 : 0);

        return values;
    }


}
