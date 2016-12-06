package edu.kvcc.cis298.cis298assignment4;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.kvcc.cis298.cis298assignment4.database.BeverageBaseHelper;
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

    //List to store all of the beverages
    private List<Beverage> mBeverages;

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
        //Make a new list to hold the beverages
        mBeverages = new ArrayList<>();
        //Set the context to the one that is passed in
        mContext = context.getApplicationContext();
        // >Set the database.
        mDatabase = new BeverageBaseHelper(mContext).getWritableDatabase();
    }

    //Getters
    public List<Beverage> getBeverages() {
        return mBeverages;
    }

    public Beverage getBeverage(String Id) {
        for (Beverage beverage : mBeverages) {
            if (beverage.getId().equals(Id)) {
                return beverage;
            }
        }
        return null;
    }

    //Method to load the beverage list from a CSV file
    private void loadBeverageList() {

        //Define a scanner
        Scanner scanner = null;

        try {

            //Instanciate a new scanner
            scanner = new Scanner(mContext.getResources().openRawResource(R.raw.beverage_list));

            //While the scanner has another line to read
            while (scanner.hasNextLine()) {

                //Get the next line and split it into parts
                String line = scanner.nextLine();
                String parts[] = line.split(",");

                //Assign each part to a local var
                String id = parts[0];
                String name = parts[1];
                String pack = parts[2];

                //setup some vars for doing parsing
                double price = Double.parseDouble(parts[3]);
                boolean active = ((parts[4].equals("True")));

                //Add the beverage to the list
                mBeverages.add(new Beverage(id, name, pack, price, active));
            }

        //catch any errors that occur and finally close the scanner
        } catch (Exception e) {
            Log.e("Read CSV", e.toString());
        } finally {
            scanner.close();
        }
    }


    // >Add a beverage to the database.
    public void addBeverage(Beverage beverage) {
        ContentValues values = getContentValues(beverage);
        // >Insert a new record into the database.
        mDatabase.insert(BeverageTable.NAME, null, values);
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

    public void updateBeverage(Beverage beverage) {


        ContentValues values = getContentValues(beverage);

        mDatabase.update(BeverageTable.NAME, values,
                uuid + " = ?")
    }

}
