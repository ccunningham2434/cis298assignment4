package edu.kvcc.cis298.cis298assignment4.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import edu.kvcc.cis298.cis298assignment4.Beverage;

import edu.kvcc.cis298.cis298assignment4.database.BeverageDBSchema.BeverageTable;

/**
 * Created by ccunn on 05-Dec-16.
 */

public class BeverageCursorWrapper extends CursorWrapper {

    public BeverageCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Beverage getBeverage() {
        String uuidString = getString(getColumnIndex(BeverageTable.Cols.UUID));
        String id = getString(getColumnIndex(BeverageTable.Cols.ID));
        String name = getString(getColumnIndex(BeverageTable.Cols.NAME));
        String pack = getString(getColumnIndex(BeverageTable.Cols.PACK));
        double price = getDouble(getColumnIndex(BeverageTable.Cols.PRICE));
        int active = getInt(getColumnIndex(BeverageTable.Cols.ACTIVE));

        // >Create a new beverage with the uuid from the database.
        Beverage beverage = new Beverage(UUID.fromString(uuidString));
        // >Set the remaining properties.
        beverage.setId(id);
        beverage.setName(name);
        beverage.setPack(pack);
        beverage.setPrice(price);
        beverage.setActive(active != 0);

        return beverage;
    }


}
