package pavelsemenkov.bus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pavelsemenkov.bus.database.BusDbHelper;
import pavelsemenkov.bus.database.BusTable;
import pavelsemenkov.bus.fragment.OtherStopFragments.OtherStopFragmentDB;
import pavelsemenkov.bus.fragment.StopFragment.FirstStopFragment;
import pavelsemenkov.bus.model.BasicStop;

public class OtherStopEditActivity extends AppCompatActivity implements OtherStopFragmentDB.busListEventListener {

    private int i;
    private FragmentTransaction fTrans;
    private BusDbHelper dbHelper;
    private Menu menu;
    private int idAppdate = 7, idSave = 7;
    private Toolbar toolbarStop;
    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_stop_editor);

        initToolbar();
        initDayViewOtherStopEditor(getBasicStopDB());
    }

    private void initDayViewOtherStopEditor(Map<String, BasicStop> BasicStop) {
        Log.d(LOG_TAG, "initDayViewOtherStopEditor");
        int size = BasicStop.size();
        fTrans = getSupportFragmentManager().beginTransaction();
        if (size != 0) {
            fTrans.add(R.id.activity_OtherStopEditor_frame, OtherStopFragmentDB.getInstance(this, BasicStop, toolbarStop));
        } else {
            FirstStopFragment frag1 = new FirstStopFragment();
            fTrans.add(R.id.activity_OtherStopEditor_frame, frag1);
        }
        fTrans.commit();
    }


    private void initToolbar() {
        toolbarStop = (Toolbar) findViewById(R.id.OtherStopEditor_toolbar);
        setSupportActionBar(toolbarStop);
        getSupportActionBar().setTitle(R.string.OtherStopEditorTitle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stop_menu, menu);
        this.menu = menu;
        menu.add(0, idAppdate, 0, R.string.OtherStopEditorAppdateButton)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                        | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(1, idSave, 0, R.string.OtherStopEditorSaveButton)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                        | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == idAppdate) {
            try {
                updateBasicStopTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
            initDayViewOtherStopEditor(getBasicStopDB());
        } else if (id == idSave) {
            //
        }
        return super.onOptionsItemSelected(item);
    }

    private Map<String, HashSet<String>> getBasicStopMap() {
        Map<String, HashSet<String>> basicStopMap = new TreeMap<>();
        String selection = null;
        String[] selectionArgs = null;
        ArrayList<ArrayList<String>> stopList = new ArrayList<>();
        stopList.add(new ArrayList<String>());
        dbHelper = new BusDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cB = db.query(BusTable.TABLE_BUS_NAME, null, null, null, null, null, null);
        if (cB.moveToFirst()) {
            selection = BusTable.COLUMN_BUS_ID + " = ?";
            while (cB.moveToNext()) {
                int idColIndex = cB.getColumnIndex(BusTable.COLUMN_ID_BUS);
                selectionArgs = new String[]{Integer.toString(cB.getInt(idColIndex))};
                Cursor c = db.query(BusTable.TABLE_BUS_STOP_NAME, null, selection, selectionArgs, null, null, null);
                if (c.moveToFirst()) {
                    String baseStop = null, nextStop;
                    int busTimeColIndex = c.getColumnIndex(BusTable.COLUMN_BUS_STOP_TIME);
                    int busStopColIndex = c.getColumnIndex(BusTable.COLUMN_BUS_STOP);
                    do {
                        String busStop = c.getString(busStopColIndex);
                        String busTime = c.getString(busTimeColIndex);
                        if (busTime.equals("")) continue;
                        if (busStop.equals(baseStop)) continue;
                        if (c.isFirst()) {//если первая запишем как базовую остановку
                            baseStop = busStop;
                            continue;
                        }
                        nextStop = busStop;
                        if (basicStopMap.containsKey(baseStop)) {
                            basicStopMap.get(baseStop).add(nextStop);
                            baseStop = nextStop;
                        } else {
                            basicStopMap.put(baseStop, new HashSet<String>());
                            basicStopMap.get(baseStop).add(nextStop);
                            baseStop = nextStop;
                        }
                    } while (c.moveToNext());
                    c.close();
                } else Log.d(LOG_TAG, "GetBus 0 rows");
            }
            cB.close();
        } else Log.d(LOG_TAG, "GetStop 0 rows");
        dbHelper.close();
        return basicStopMap;
    }

    public void updateBasicStopTable() throws IOException {
        Map<String, HashSet<String>> basicStopMap = getBasicStopMap();
        Map<String, BasicStop> BasicStopDB = getBasicStopDB();
        List<ContentValues> values = new ArrayList<>();
        for (Map.Entry<String, HashSet<String>> rec : basicStopMap.entrySet()) {
            if (BasicStopDB.containsKey(rec.getKey())) {
                Map<String, ArrayList<String>> NextStopSet = BasicStopDB.get(rec.getKey()).getNextStopSet();
                for (String nextStop : basicStopMap.get(rec.getKey())) {
                    if (!NextStopSet.containsKey(nextStop)) {
                        ContentValues value = new ContentValues();
                        value.put(BusTable.COLUMN_BASIC_ROOT_STOP_ID, BasicStopDB.get(rec.getKey()).getBasicStopId());
                        value.put(BusTable.COLUMN_BASIC_NEXT_STOP_NAME, nextStop);
                        value.put(BusTable.COLUMN_BASIC_ROOT_STOP_NAME, rec.getKey());
                        value.put(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LAT, 0d);
                        value.put(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LNG, 0d);
                        values.add(value);
                    }
                }
            } else {
                i++;
                for (String nextStop : basicStopMap.get(rec.getKey())) {
                    ContentValues value = new ContentValues();
                    value.put(BusTable.COLUMN_BASIC_ROOT_STOP_ID, i);
                    value.put(BusTable.COLUMN_BASIC_NEXT_STOP_NAME, nextStop);
                    value.put(BusTable.COLUMN_BASIC_ROOT_STOP_NAME, rec.getKey());
                    value.put(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LAT, 0d);
                    value.put(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LNG, 0d);
                    values.add(value);
                }
            }
        }
        if (values.size() > 0) {
            dbHelper = new BusDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.bulkInsertWithTrans(db, BusTable.TABLE_BASIC_OTHER_STOP_NAME, values.toArray(new ContentValues[values.size()]));
            dbHelper.close();
        }
    }

    private Map<String, BasicStop> getBasicStopDB() {
        Map<String, BasicStop> BasicStopDB = new HashMap<>();
        dbHelper = new BusDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BusTable.TABLE_BASIC_OTHER_STOP_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            String baseStop = null;
            int newId = -1;
            int rootStopIdColIndex = cursor.getColumnIndex(BusTable.COLUMN_BASIC_ROOT_STOP_ID);
            int nextStopIdColIndex = cursor.getColumnIndex(BusTable.COLUMN_BASIC_NEXT_STOP_ID);
            int rootStopColIndex = cursor.getColumnIndex(BusTable.COLUMN_BASIC_ROOT_STOP_NAME);
            int nextStopColIndex = cursor.getColumnIndex(BusTable.COLUMN_BASIC_NEXT_STOP_NAME);
            int nextStopCoordLatColIndex = cursor.getColumnIndex(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LAT);
            int nextStopCoordLngColIndex = cursor.getColumnIndex(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LNG);
            while (cursor.moveToNext()) {
                String coordinate = this.getString(R.string.OtherStopEditorDefoltCoord);
                if(cursor.getDouble(nextStopCoordLatColIndex)!=0d&&cursor.getDouble(nextStopCoordLngColIndex)!=0d){
                    coordinate = Double.toString(cursor.getDouble(nextStopCoordLatColIndex))+","+
                            Double.toString(cursor.getDouble(nextStopCoordLngColIndex));
                }
                if (newId == cursor.getInt(rootStopIdColIndex)) {
                    BasicStopDB.get(baseStop).putNextStopSet(cursor.getString(nextStopColIndex), coordinate
                            , Integer.toString(cursor.getInt(nextStopIdColIndex)));
                } else {
                    baseStop = cursor.getString(rootStopColIndex);
                    newId = cursor.getInt(rootStopIdColIndex);
                    i = i >= newId ? i : newId;
                    BasicStopDB.put(baseStop, new BasicStop(baseStop, newId, new HashMap<String, ArrayList<String>>()));
                    BasicStopDB.get(baseStop).putNextStopSet(cursor.getString(nextStopColIndex), coordinate
                            , Integer.toString(cursor.getInt(nextStopIdColIndex)));
                }
            }
            cursor.close();
        }
        return BasicStopDB;
    }

    @Override
    public void busListEvent() {
        //
    }
}
