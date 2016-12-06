package edu.kvcc.cis298.cis298assignment4.database;

/**
 * Created by ccunn on 05-Dec-16.
 */

public class BeverageDBSchema {

    public static final class BeverageTable {
        public static final String NAME = "beverages";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String PACK = "pack";
            public static final String PRICE = "price";
            public static final String ACTIVE = "active";
        }
    }
}
