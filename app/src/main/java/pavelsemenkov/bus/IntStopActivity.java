package pavelsemenkov.bus;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import pavelsemenkov.bus.fragment.Dialog2;
import pavelsemenkov.bus.fragment.IntersitiFragment.IntStopFragmentDB;
import pavelsemenkov.bus.database.DBHelper;

public class IntStopActivity extends AppCompatActivity {


    private String busName, busId, busInfo;
    private ArrayList<ArrayList<String>> stopList = new ArrayList<ArrayList<String>>();
    private int i, j, busStopInt, busIndex, noSetMenItem = -1, itemId = 123, DIALOG = 7;
    private FragmentTransaction fTrans;
    private DBHelper dbHelper;
    private Menu menu;
    private Toolbar toolbarStop;
    final String LOG_TAG = "myLogs";
    private DialogFragment dlg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_stop);
        Intent intent = getIntent();
        busName = intent.getStringExtra("title");
        busId = intent.getStringExtra("id");
        busIndex = Integer.valueOf(intent.getStringExtra("index"));
        busInfo = intent.getStringExtra("info");

        dlg2 = Dialog2.getInstance(busInfo);
        initToolbar();
        initDayView(getStopData());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (stopList == null){
            getStopData();
        }
    }

    private void initDayView(ArrayList<ArrayList<String>> stopList) {
        if (stopList == null){
            stopList = getStopData();
        }
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.activity_stop_int_frame, IntStopFragmentDB.getInstance(this, stopList, busIndex, toolbarStop));
        fTrans.commit();
    }


    private void initToolbar() {
        toolbarStop = (Toolbar) findViewById(R.id.stop_int_toolbar);
        setSupportActionBar(toolbarStop);
        getSupportActionBar().setTitle(busName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stop_menu, menu);
        this.menu = menu;
        if (busInfo != null) {
            menu.add(0, itemId, 0, null).setIcon(android.R.drawable.ic_menu_help)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                            | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==itemId){
            dlg2.show(getSupportFragmentManager(), "dlg2");
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<ArrayList<String>> getStopData() {
        String selection = null;
        String[] selectionArgs = null;

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        selection = "bus_id = ?";
        selectionArgs = new String[]{busId};
        busStopInt = 0;
        Cursor c = db.query("intercity_stop", null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            i = 0;
            j = 0;
            int nameColIndex = c.getColumnIndex("name");
            int timeColIndex = c.getColumnIndex("time");
            int routeColIndex = c.getColumnIndex("route");
            do {
                if (busIndex == 2) {
                    if (i % 2 == 0) {
                        stopList.add(new ArrayList<String>());
                        stopList.get(j).add(0, c.getString(nameColIndex));
                        stopList.get(j).add(1, c.getString(timeColIndex));
                    } else {
                        stopList.get(j).add(2, c.getString(nameColIndex));
                        stopList.get(j).add(3, c.getString(timeColIndex));
                        stopList.get(j).add(4, c.getString(routeColIndex));
                        j++;
                    }
                } else if (busIndex == 4) {
                    if (i % 4 == 0) {
                        stopList.add(new ArrayList<String>());
                        stopList.get(j).add(0, c.getString(nameColIndex));
                        stopList.get(j).add(1, c.getString(timeColIndex));
                    } else if (i % 4 == 1) {
                        stopList.get(j).add(2, c.getString(nameColIndex));
                        stopList.get(j).add(3, c.getString(timeColIndex));
                    } else if (i % 4 == 2) {
                        stopList.get(j).add(4, c.getString(nameColIndex));
                        stopList.get(j).add(5, c.getString(timeColIndex));
                    } else {
                        stopList.get(j).add(6, c.getString(nameColIndex));
                        stopList.get(j).add(7, c.getString(timeColIndex));
                        stopList.get(j).add(8, c.getString(routeColIndex));
                        j++;
                    }
                }
                i++;
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "GetBus 0 rows");
        dbHelper.close();
        return stopList;
    }

}
