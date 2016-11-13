package pavelsemenkov.bus.database;

import android.database.sqlite.SQLiteDatabase;


public class BusTable {
    public static final String TABLE_BUS_NAME = "bus";
    public static final String COLUMN_ID_BUS = "_id";
    public static final String COLUMN_BAS_NUMBER = "bus_num";
    public static final String COLUMN_BAS_TEXT = "bus";
    public static final String COLUMN_BAS_HTTP = "http";

    public static final String TABLE_BUS_STOP_NAME = "stop";
    public static final String COLUMN_ID_BUS_STOP = "_id";
    public static final String COLUMN_BUS_ID = "bus_id";
    public static final String COLUMN_BUS_STOP_PAY = "bus_pay";
    public static final String COLUMN_BUS_STOP_NAME = "bus_name";
    public static final String COLUMN_BUS_STOP_TIME = "bus_time";
    public static final String COLUMN_BUS_STOP_WEEK_DAY_CODE = "bus_stop_int";
    public static final String COLUMN_BUS_STOP_WEEK_DAY_NAME = "bus_stop_day";
    public static final String COLUMN_BUS_STOP = "bus_stop";

    public static final String TABLE_BASIC_OTHER_STOP_NAME = "basic_other_stop";
    public static final String COLUMN_BASIC_NEXT_STOP_ID = "_id";
    public static final String COLUMN_BASIC_ROOT_STOP_ID = "root_id";
    public static final String COLUMN_BASIC_NEXT_STOP_NAME = "next_stop_name";
    public static final String COLUMN_BASIC_ROOT_STOP_NAME = "root_stop_name";
    public static final String COLUMN_BASIC_ROOT_STOP_TARGET_LAT = "root_stop_coord_lat";
    public static final String COLUMN_BASIC_ROOT_STOP_TARGET_LNG = "root_stop_coord_lng";

    public static final String TABLE_INTERCITY_BUS_NAME = "intercity_bus";
    public static final String COLUMN_ID_INT_BUS = "_id";
    public static final String COLUMN_INT_ROUTE_TYP = "route_typ";
    public static final String COLUMN_INT_CITY = "city";
    public static final String COLUMN_INT_TABLE_IND = "ind";
    public static final String COLUMN_INT_INFO = "info";

    public static final String TABLE_INTERCITY_BUS_STOP_NAME = "intercity_stop";
    public static final String COLUMN_ID_INT_BUS_STOP = "_id";
    public static final String COLUMN_INT_BUS_ID = "bus_id";
    public static final String COLUMN_INT_CITY_STOP_NAME = "name";
    public static final String COLUMN_INT_CITY_STOP_TIME = "time";
    public static final String COLUMN_INT_CITY_STOP_ROUTE = "route";

    //public static final String DEFAULT_SORT_ORDER = COLUMN_PUBLICATION_DATE + " DESC";

    public static final String[] BUS_PROJECTION = new String[] {
            COLUMN_ID_BUS, COLUMN_BAS_NUMBER, COLUMN_BAS_TEXT, COLUMN_BAS_HTTP
    };
    public static final String[] BUS_STOP_PROJECTION = new String[] {
            COLUMN_ID_BUS_STOP, COLUMN_BUS_ID, COLUMN_BUS_STOP_PAY,
            COLUMN_BUS_STOP_NAME, COLUMN_BUS_STOP_TIME, COLUMN_BUS_STOP_WEEK_DAY_CODE,
            COLUMN_BUS_STOP_WEEK_DAY_NAME, COLUMN_BUS_STOP
    };
    public static final String[] BASIC_OTHER_STOP_PROJECTION = new String[] {
            COLUMN_BASIC_NEXT_STOP_ID, COLUMN_BASIC_ROOT_STOP_ID,
            COLUMN_BASIC_NEXT_STOP_NAME, COLUMN_BASIC_ROOT_STOP_NAME,
            COLUMN_BASIC_ROOT_STOP_TARGET_LAT, COLUMN_BASIC_ROOT_STOP_TARGET_LNG
    };
    public static final String[] INTERCITY_BUS_PROJECTION = new String[] {
            COLUMN_ID_INT_BUS, COLUMN_INT_ROUTE_TYP, COLUMN_INT_CITY,
            COLUMN_INT_TABLE_IND, COLUMN_INT_INFO
    };
    public static final String[] INTERCITY_BUS_STOP_PROJECTION = new String[] {
            COLUMN_ID_INT_BUS_STOP, COLUMN_INT_BUS_ID, COLUMN_INT_CITY_STOP_NAME,
            COLUMN_INT_CITY_STOP_TIME, COLUMN_INT_CITY_STOP_ROUTE
    };

    private static final String TABLE_BUS_CREATE = "create table "
            + TABLE_BUS_NAME
            + "("
            + COLUMN_ID_BUS + " integer primary key autoincrement, "
            + COLUMN_BAS_NUMBER + " text, "
            + COLUMN_BAS_TEXT + " text,"
            + COLUMN_BAS_HTTP + " text"
            + ");";

    private static final String TABLE_BUS_STOP_CREATE = "create table "
            + TABLE_BUS_STOP_NAME
            + "("
            + COLUMN_ID_BUS_STOP + " integer primary key autoincrement, "
            + COLUMN_BUS_ID + " integer, "
            + COLUMN_BUS_STOP_PAY + " text default 'null',"
            + COLUMN_BUS_STOP_NAME + " text,"
            + COLUMN_BUS_STOP_TIME + " text,"
            + COLUMN_BUS_STOP_WEEK_DAY_CODE + " integer,"
            + COLUMN_BUS_STOP_WEEK_DAY_NAME + " text,"
            + COLUMN_BUS_STOP + " text"
            + ");";

    private static final String TABLE_BASIC_OTHER_STOP_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_BASIC_OTHER_STOP_NAME
            + "("
            + COLUMN_BASIC_NEXT_STOP_ID + " integer primary key autoincrement, "
            + COLUMN_BASIC_ROOT_STOP_ID + " integer, "
            + COLUMN_BASIC_NEXT_STOP_NAME + " text,"
            + COLUMN_BASIC_ROOT_STOP_NAME + " text,"
            + COLUMN_BASIC_ROOT_STOP_TARGET_LAT + " real,"
            + COLUMN_BASIC_ROOT_STOP_TARGET_LNG + " real"
            + ");";

    private static final String TABLE_INT_BUS_CREATE = "create table "
            + TABLE_INTERCITY_BUS_NAME
            + "("
            + COLUMN_ID_INT_BUS + " integer primary key, "
            + COLUMN_INT_ROUTE_TYP + " text, "
            + COLUMN_INT_CITY + " integer,"
            + COLUMN_INT_TABLE_IND + " integer,"
            + COLUMN_INT_INFO + " text default 'null'"
            + ");";

    private static final String TABLE_INT_BUS_STOP_CREATE = "create table "
            + TABLE_INTERCITY_BUS_STOP_NAME
            + "("
            + COLUMN_ID_INT_BUS_STOP + " integer primary key autoincrement, "
            + COLUMN_INT_BUS_ID + " integer, "
            + COLUMN_INT_CITY_STOP_NAME + " text,"
            + COLUMN_INT_CITY_STOP_TIME + " text,"
            + COLUMN_INT_CITY_STOP_ROUTE + " text default 'null'"
            + ");";
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_BUS_CREATE);
        database.execSQL(TABLE_BUS_STOP_CREATE);
        database.execSQL(TABLE_BASIC_OTHER_STOP_CREATE);
        database.execSQL(TABLE_INT_BUS_CREATE);
        database.execSQL(TABLE_INT_BUS_STOP_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        //fast implementation of upgrade
        onReset(database);
    }

    public static void onReset(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BUS_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BUS_STOP_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERCITY_BUS_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERCITY_BUS_STOP_NAME);
        onCreate(database);
    }
    public static void resetOtherTable(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BASIC_OTHER_STOP_NAME);
        database.execSQL(TABLE_BASIC_OTHER_STOP_CREATE);
    }
}
