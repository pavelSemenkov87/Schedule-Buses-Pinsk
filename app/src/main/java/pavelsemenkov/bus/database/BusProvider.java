package pavelsemenkov.bus.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;


public class BusProvider extends ContentProvider {

    private BusDbHelper database;
    private SQLiteDatabase sqlDB;

    final String LOG_TAG = "myLogs";
    private static final int BUS = 10;
    private static final int BUS_ID = 20;
    private static final int BUS_TABLE = 30;

    private static final String AUTHORITY = "pavelsemenkov.bus.BusProvider";

    public static final Uri CONTENT_BUS_URI = Uri.parse("content://" + AUTHORITY + "/" + BusTable.TABLE_BUS_NAME);
    public static final Uri CONTENT_STOP_CITY_URI = Uri.parse("content://" + AUTHORITY + "/" + BusTable.TABLE_BUS_STOP_NAME);
    public static final Uri CONTENT_INTERCITY_BUS_CITY_URI = Uri.parse("content://" + AUTHORITY + "/" + BusTable.TABLE_INTERCITY_BUS_NAME);
    public static final Uri CONTENT_INTERCITY_BUS_STOP_URI = Uri.parse("content://" + AUTHORITY + "/" + BusTable.TABLE_INTERCITY_BUS_STOP_NAME);
    public static final Uri DATABUSE_URI = Uri.parse("content://" + AUTHORITY);


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BusTable.TABLE_BUS_NAME, BUS);
        sURIMatcher.addURI(AUTHORITY, BusTable.TABLE_BUS_NAME + "/#", BUS_ID);
        sURIMatcher.addURI(AUTHORITY, BusTable.TABLE_BUS_NAME + "/*", BUS_TABLE);
    }

    @Override
    public boolean onCreate() {
        database = new BusDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(checkColumns(projection, uri.getPath()));
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public Cursor queryT(Uri uri, String[] projection, String selection,
                         String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(checkColumns(projection, uri.getPath()));
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int updatedRowsCount = 0;
        for (ContentValues cv : values) {
            boolean success = sqlDB.insertWithOnConflict(uri.getLastPathSegment(), null, cv, SQLiteDatabase.CONFLICT_IGNORE) != -1;
            if (success) ++updatedRowsCount;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "CityBusRoutLoadThread provider.bulkInsert finish");
        return updatedRowsCount;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        long id = 0;
        id = sqlDB.insertWithOnConflict(uri.getLastPathSegment(), null, values, SQLiteDatabase.CONFLICT_IGNORE);
        /*switch (uriType) {
            case BUS:
                id = sqlDB.insertWithOnConflict(uri.getLastPathSegment(), null, values, SQLiteDatabase.CONFLICT_IGNORE);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }*/
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BusTable.TABLE_BUS_NAME + "/" + id);
    }

    public Uri insertT(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id = 0;
        id = sqlDB.insert(uri.getLastPathSegment(), null, values);
        /*switch (uriType) {
            case BUS:
                id = sqlDB.insertWithOnConflict(uri.getLastPathSegment(), null, values, SQLiteDatabase.CONFLICT_IGNORE);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }*/
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BusTable.TABLE_BUS_NAME + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        rowsDeleted = sqlDB.delete(uri.getLastPathSegment(), selection, selectionArgs);
        /*switch (uriType) {
            case BUS:
                rowsDeleted = sqlDB.delete(HeadlineTable.TABLE_NAME, selection, selectionArgs);
                break;
            case BUS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(HeadlineTable.TABLE_NAME, HeadlineTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(HeadlineTable.TABLE_NAME,
                            HeadlineTable.COLUMN_ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }*/
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        int rowsUpdated = 0;
        rowsUpdated = sqlDB.update(uri.getLastPathSegment(), values, selection, selectionArgs);
        /*switch (uriType) {
            case BUS:
                rowsUpdated = sqlDB.update(HeadlineTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BUS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(HeadlineTable.TABLE_NAME,
                            values, HeadlineTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(HeadlineTable.TABLE_NAME, values,
                            HeadlineTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }*/
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


    public void reset() {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        database.onReset(sqlDB);
    }

    public void beginTransaction() {
        sqlDB = database.getWritableDatabase();
        sqlDB.beginTransaction();
    }

    public void setTransactionSuccessful() {
        sqlDB.setTransactionSuccessful();
    }

    public void endTransaction() {
        sqlDB.endTransaction();
        sqlDB.close();
    }

    private String checkColumns(String[] projection, String tableName) {
        String[] available;
        switch (tableName) {
            case "/" + BusTable.TABLE_BUS_NAME:
                tableName = BusTable.TABLE_BUS_NAME;
                available = BusTable.BUS_PROJECTION;
                break;
            case "/" + BusTable.TABLE_BUS_STOP_NAME:
                tableName = BusTable.TABLE_BUS_STOP_NAME;
                available = BusTable.BUS_STOP_PROJECTION;
                break;
            case "/" + BusTable.TABLE_INTERCITY_BUS_NAME:
                tableName = BusTable.TABLE_INTERCITY_BUS_NAME;
                available = BusTable.INTERCITY_BUS_PROJECTION;
                break;
            case "/" + BusTable.TABLE_INTERCITY_BUS_STOP_NAME:
                tableName = BusTable.TABLE_INTERCITY_BUS_STOP_NAME;
                available = BusTable.INTERCITY_BUS_STOP_PROJECTION;
                break;
            default:
                throw new IllegalArgumentException("Unknown table name");
        }
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));

            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
        return tableName;
    }

}
