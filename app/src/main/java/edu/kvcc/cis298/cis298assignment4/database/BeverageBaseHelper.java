package edu.kvcc.cis298.cis298assignment4.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Scanner;

import edu.kvcc.cis298.cis298assignment4.database.BeverageDBSchema.BeverageTable;

/**
 * Created by ccunn on 05-Dec-16.
 */

public class BeverageBaseHelper extends SQLiteOpenHelper {

    Context mContext;// >The context for this class.
    boolean mShouldSeed; // >Flag weather to seed the database or not.

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
    private class FetchBeveragesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new BeverageFetcher().fetchBeverages();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
