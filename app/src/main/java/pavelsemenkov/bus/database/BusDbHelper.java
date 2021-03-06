package pavelsemenkov.bus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BusDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "raspisanieDB";
    private static final int DATABASE_VERSION = 1;

    public BusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        BusTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        BusTable.onUpgrade(database, oldVersion, newVersion);
    }
    public void onReset(SQLiteDatabase database) {
        BusTable.onReset(database);
    }
    public int bulkInsertWithTrans(SQLiteDatabase sqlDB, String table, ContentValues[] values) {
        sqlDB.beginTransaction();
        int updatedRowsCount = 0;
        for (ContentValues cv : values) {
            boolean success = sqlDB.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_IGNORE) != -1;
            if (success) ++updatedRowsCount;
        }
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
        sqlDB.close();
        return updatedRowsCount;
    }
    public void resetOtherStop(SQLiteDatabase sqlDB){
        BusTable.resetOtherTable(sqlDB);
    }
}

