package pavelsemenkov.bus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    final String LOG_TAG = "myLogs";

    public DBHelper(Context paramContext) {
        super(paramContext, "raspisanieDB", null, 1);
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        Log.d("myLogs", "--- onCreate database 1 ---");
        paramSQLiteDatabase.execSQL("create table bus (_id integer primary key autoincrement," +
                "bus_num text," +
                "bus text," +
                "http text);");
        paramSQLiteDatabase.execSQL("create table intercity_bus (_id integer primary key," +
                "route_typ text," +
                "city integer," +
                "ind integer," +
                "info text default 'null');");
        paramSQLiteDatabase.execSQL("create table intercity_stop (_id integer primary key autoincrement," +
                "bus_id integer," +
                "name text," +
                "time text," +
                "route text default 'null');");
        paramSQLiteDatabase.execSQL("create table stop (_id integer primary key autoincrement," +
                "bus_id integer," +
                "bus_pay text default 'null'," +
                "bus_name text," +
                "bus_time text," +
                "bus_stop_int integer," +
                "bus_stop_day text," +
                "bus_stop text);");
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
    }

    public void reset(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL("drop table bus;");
        paramSQLiteDatabase.execSQL("drop table intercity_bus;");
        paramSQLiteDatabase.execSQL("drop table intercity_stop;");
        paramSQLiteDatabase.execSQL("drop table stop;");
        paramSQLiteDatabase.execSQL("create table bus (_id integer primary key autoincrement," +
                "bus_num text," +
                "bus text," +
                "http text);");
        paramSQLiteDatabase.execSQL("create table intercity_bus (_id integer primary key," +
                "route_typ text," +
                "city integer," +
                "ind integer," +
                "info text default 'null');");
        paramSQLiteDatabase.execSQL("create table intercity_stop (_id integer primary key autoincrement," +
                "bus_id integer," +
                "name text," +
                "time text," +
                "route text default 'null');");
        paramSQLiteDatabase.execSQL("create table stop (_id integer primary key autoincrement," +
                "bus_id integer," +
                "bus_pay text default 'null'," +
                "bus_name text," +
                "bus_time text," +
                "bus_stop_int integer," +
                "bus_stop_day text," +
                "bus_stop text);");
    }
}