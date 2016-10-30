package pavelsemenkov.bus.controllers.loaders;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pavelsemenkov.bus.ScheduleLoaderService;
import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;

/**
 * Created by Павел on 17.10.2016.
 */
public class BusesRoutsLoader {
    private static BusesRoutsLoader instance;
    private String[] http;
    private String select;
    private static final Object lock = new Object();
    private static List<ContentValues> allValuesCityStop, allValuesIntCityStop, allValuesIntCityName;
    Object sync = new Object();
    private static int countThread, idIntCity = 0;
    private int city;
    private BusProvider provider;
    final String LOG_TAG = "myLogs";

    private BusesRoutsLoader(BusProvider provider, String[] http, String select) {
        this.provider = provider;
        this.http = http;
        this.select = select;
    }

    public static BusesRoutsLoader getInstance(BusProvider provider, String[] http, String select) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null)
                    instance = new BusesRoutsLoader(provider, http, select);
            }
        }
        return instance;
    }

    public boolean loadBusesRouts() throws IOException, InterruptedException {
        allValuesCityStop = new ArrayList<>();
        allValuesIntCityStop = new ArrayList<>();
        allValuesIntCityName = new ArrayList<>();
        Cursor c = provider.queryT(BusProvider.CONTENT_BUS_URI, null, null, null, null);
        ExecutorService executor = Executors.newFixedThreadPool(12);
        ContentValues cv = new ContentValues();
        int j = http.length;
        city = 0;
        countThread = 0;
        for (int i = 0; i < j; i++) {
            executor.submit( new IntercityBusRoutLoadThread(http[i], select, city));
            city++;
            countThread++;
            Log.d(LOG_TAG, "IntercityBusRoutLoadThread Start");
        }
        //ставим позицию курсора на первую строку выборки если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(BusTable.COLUMN_ID_BUS);
            int numColIndex = c.getColumnIndex(BusTable.COLUMN_BAS_NUMBER);
            int httpColIndex = c.getColumnIndex(BusTable.COLUMN_BAS_HTTP);
            do {
                executor.submit(new CityBusRoutLoadThread(c.getInt(idColIndex), c.getString(numColIndex), c.getString(httpColIndex), allValuesCityStop, sync));
                countThread++;
            } while (c.moveToNext());
            synchronized (sync) {
                while (countThread != ScheduleLoaderService.getCountFinishThread()) {
                    sync.wait();
                }
            }
        } else Log.d(LOG_TAG, "GetStop 0 rows");
        c.close();
        provider.bulkInsert(BusProvider.CONTENT_STOP_CITY_URI, allValuesCityStop.toArray(new ContentValues[allValuesCityStop.size()]));
        provider.bulkInsert(BusProvider.CONTENT_INTERCITY_BUS_CITY_URI, allValuesIntCityName.toArray(new ContentValues[allValuesIntCityName.size()]));
        provider.bulkInsert(BusProvider.CONTENT_INTERCITY_BUS_STOP_URI, allValuesIntCityStop.toArray(new ContentValues[allValuesIntCityStop.size()]));
        Log.d(LOG_TAG, "provider.bulkInsert success");
        return true;
    }

    public static void addListBusStop(List<ContentValues> b) {
        allValuesCityStop = joinLists(allValuesCityStop, b);
    }
    public static void addListIntBusName(List<ContentValues> b) {
        allValuesIntCityName = joinLists(allValuesIntCityName, b);
    }
    public static void addListIntBusStop(List<ContentValues> b) {
        allValuesIntCityStop = joinLists(allValuesIntCityStop, b);
    }

    public static int getIdIntCity() {
        return ++idIntCity;
    }

    private static List<ContentValues> joinLists(List<ContentValues> a, List<ContentValues> b) {
        if ((a == null) || (a.isEmpty() && (b != null))) return b;
        if ((b == null) || b.isEmpty()) return a;
        ArrayList<ContentValues> result = new ArrayList(a.size() + b.size()); // Закладываем размер достаточный для всех элементов
        result.addAll(a);
        result.addAll(b);
        return result;
    }

}
