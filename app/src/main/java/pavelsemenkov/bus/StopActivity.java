package pavelsemenkov.bus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;
import pavelsemenkov.bus.database.DBHelper;
import pavelsemenkov.bus.fragment.StopFragment.FirstStopFragment;
import pavelsemenkov.bus.fragment.StopFragment.StopFragmentDB;

public class StopActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, StopFragmentDB.busListEventListener {


    private String busName, busId;
    private Map<Integer, ArrayList<ArrayList<String>>> stopData = new HashMap<>();
    private int i, j, busStopInt, noSetMenItem = -1;
    private FragmentTransaction fTrans;
    private DBHelper dbHelper;
    private final int LOADER_ID_UPDATE_SCHEDULE = 5;
    private final int LOADER_ID_GET_SCHEDULE = 6;
    private Menu menu;
    private Toolbar toolbarStop;
    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        Intent intent = getIntent();
        busName = intent.getStringExtra("title");
        busId = intent.getStringExtra("id");
        getSupportLoaderManager().initLoader(LOADER_ID_GET_SCHEDULE, null, this);
    }

    @Override
    public void onPostResume(){
        super.onPostResume();
        getSupportLoaderManager().initLoader(LOADER_ID_GET_SCHEDULE, null, this);
    }
    @Override
    public Loader onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case LOADER_ID_UPDATE_SCHEDULE:
                //return new ScheduleLoader(getActivity());
            case LOADER_ID_GET_SCHEDULE:
                return new CursorLoader(this,
                        BusProvider.CONTENT_STOP_CITY_URI,
                        BusTable.BUS_STOP_PROJECTION,
                        "bus_id = ?",
                        new String[]{busId},
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_UPDATE_SCHEDULE:
                break;
            case LOADER_ID_GET_SCHEDULE:
                Cursor cursor = (Cursor) data;
                stopData = getStopData(cursor);
                initToolbar();
                initDayView(stopData);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void initDayView(Map<Integer, ArrayList<ArrayList<String>>> stopData) {
        GregorianCalendar newCal = new GregorianCalendar();
        String day = Integer.toString(newCal.get(Calendar.DAY_OF_WEEK) - 1);
        Log.d(LOG_TAG, "initDayView");
        int size = stopData.size();
        boolean noData = true;
        fTrans = getSupportFragmentManager().beginTransaction();
        for (int m = 0; m < size; m++) {
            String wD = stopData.get(m).get(0).get(2);
            if (wD.contains(day)) {
                fTrans.add(R.id.activity_stop_frame, StopFragmentDB.getInstance(this, stopData.get(m), stopData.get(m).get(0).get(4), toolbarStop));
                noSetMenItem = m;
                noData = false;
            }
        }
        if (noData) {
            FirstStopFragment frag1 = new FirstStopFragment();
            fTrans.add(R.id.activity_stop_frame, frag1);
        }
        fTrans.commitAllowingStateLoss();
    }


    private void initToolbar() {
        toolbarStop = (Toolbar) findViewById(R.id.stop_toolbar);
        setSupportActionBar(toolbarStop);
        getSupportActionBar().setTitle("№" + busName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stop_menu, menu);
        this.menu = menu;
        int size = stopData.size();
        for (int m = 0; m < size; m++) {
            if (m != noSetMenItem) {
                menu.add(0, m, 0, stopData.get(m).get(0).get(3))
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            } else {
                menu.add(0, m, 0, stopData.get(m).get(0).get(3))
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
                menu.getItem(m).setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fTrans = getSupportFragmentManager().beginTransaction();
        int id = item.getItemId();
        int size = stopData.size();
        for (int m = 0; m < size; m++) {
            if (m != id) {
                menu.getItem(m).setVisible(true);
            } else {
                menu.getItem(m).setVisible(false);
            }
        }
        fTrans.replace(R.id.activity_stop_frame, pavelsemenkov.bus.fragment.StopFragment.StopFragmentDB.getInstance(this, stopData.get(id), stopData.get(id).get(0).get(4), toolbarStop));
        fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                /*стандартная анимация TRANSIT_FRAGMENT_CLOSE
                                        TRANSIT_FRAGMENT_OPEN
                                        TRANSIT_FRAGMENT_FADE
                                        TRANSIT_NONE*/
        fTrans.commit();
        return super.onOptionsItemSelected(item);
    }

    private Map<Integer, ArrayList<ArrayList<String>>> getStopData(Cursor c) {
        ArrayList<ArrayList<String>> stopList = new ArrayList<>();
        stopList.add(new ArrayList<String>());
        busStopInt = 0;
        int r = c.getCount();
        if (c.moveToFirst()) {
            i = 0;
            j = 0;
            int busPayColIndex = c.getColumnIndex("bus_pay");
            int busTimeColIndex = c.getColumnIndex("bus_time");
            int busStopIntColIndex = c.getColumnIndex("bus_stop_int");
            int busStopDayColIndex = c.getColumnIndex("bus_stop_day");
            int busStopColIndex = c.getColumnIndex("bus_stop");
            do {
                int inte = c.getInt(busStopIntColIndex);
                if (busStopInt == inte) {
                    stopData.get(j - 1).add(new ArrayList<String>());
                    stopData.get(j - 1).get(i).add(0, c.getString(busStopColIndex));
                    stopData.get(j - 1).get(i).add(1, c.getString(busTimeColIndex));
                } else {
                    i = 0;
                    stopList = new ArrayList<ArrayList<String>>();
                    stopList.add(new ArrayList<String>());
                    busStopInt = c.getInt(busStopIntColIndex);
                    stopList.get(i).add(0, c.getString(busStopColIndex));
                    stopList.get(i).add(1, c.getString(busTimeColIndex));
                    stopList.get(i).add(2, String.valueOf(busStopInt));
                    stopList.get(i).add(3, c.getString(busStopDayColIndex));
                    stopList.get(i).add(4, c.getString(busPayColIndex));
                    stopData.put(j, stopList);
                    j++;
                }
                i++;
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "GetBus 0 rows");
        return stopData;
    }

    @Override
    public void busListEvent() {

    }
}
