package pavelsemenkov.bus.controllers.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pavelsemenkov.bus.Adapters.TabFragmentAdapter;
import pavelsemenkov.bus.R;
import pavelsemenkov.bus.TaxiActivity;
import pavelsemenkov.bus.controllers.loaders.ScheduleLoader;
import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;
import pavelsemenkov.bus.database.DBHelper;
import pavelsemenkov.bus.fragment.Dialog1;
import pavelsemenkov.bus.model.ModelNavigationView;
import pavelsemenkov.bus.views.MainActivty.MainActivityViewMvcImpl;

//House of Cards
public class MainActivity2 extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private MainActivityViewMvcImpl mainActivityView;
    private static Handler h;
    public AppCompatActivity activity;
    private ModelNavigationView modelNavigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private boolean[] checketBus;
    private ArrayList<Boolean> checketRemind = new ArrayList<>();
    private ViewPager viewPager;
    private TabFragmentAdapter adapter;
    public static DBHelper dbHelper;
    public ProgressDialog pd;
    public boolean first = false;
    private final int LOADER_ID_UPDATE_SCHEDULE = 1;
    private final int LOADER_ID_GET_SCHEDULE = 2;
    private final long week = 604800000L;
    private final long month = 604800000L;
    private String[] httpInterCity = {
            "http://pinskap.by/content/traffic/pinsk.php",
            "http://pinskap.by/content/traffic/ivanovo.php",
            "http://pinskap.by/content/traffic/logishin.php"
    };
    private long[] setRemind = {week, month};
    private DialogFragment dlg1;
    private String httpBusesNames = "http://pinskap.by/content/traffic/urban_transport/";
    private String selectorBusesNames = "ul.left-menu > li > a";
    private String selectorIntercityBuses = ".main-column";
    private String dataAppdate = "";
    private Elements content;
    private Element link;
    private int progCount, setRem;

    final String LOG_TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        mainActivityView = new MainActivityViewMvcImpl(this, null);
        setContentView(mainActivityView.getRootView());
        activity = this;
        modelNavigationView = ModelNavigationView.getInstance(this);
        initRemindMe(modelNavigationView.getDateLoad(), modelNavigationView.getRemind());
        checketBus = modelNavigationView.getChecketBus();
        first = modelNavigationView.getFirstLoad();
        dbHelper = new DBHelper(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.close();
        getSupportLoaderManager().initLoader(LOADER_ID_GET_SCHEDULE, null, this);
    }

    @Override
    public Loader onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case LOADER_ID_UPDATE_SCHEDULE:
                if (isConnectingToInternet()) {
                    return new ScheduleLoader(this, args, httpInterCity);
                } else {
                    Toast toast2 = Toast.makeText(getApplicationContext(), activity.getString(R.string.NoDownload), Toast.LENGTH_LONG);
                    toast2.setGravity(Gravity.TOP, 0, 150);
                    toast2.show();
                }
            case LOADER_ID_GET_SCHEDULE:
                return new CursorLoader(this,
                        BusProvider.CONTENT_STOP_CITY_URI,
                        BusTable.BUS_STOP_PROJECTION,
                        null,
                        null,
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_UPDATE_SCHEDULE:
                getSupportLoaderManager().initLoader(LOADER_ID_GET_SCHEDULE, null, this);
                break;
            case LOADER_ID_GET_SCHEDULE:
                Cursor cursor = (Cursor) data;
                int r = cursor.getCount();
                if (cursor == null || cursor.getCount() <= 0) {
                    Bundle bndl = new Bundle();
                    bndl.putString(ScheduleLoader.TITLE, this.getString(R.string.LoadTitle));
                    bndl.putString(ScheduleLoader.MESEGE, this.getString(R.string.LoadMes));
                    bndl.putString(ScheduleLoader.SELECTOR_BASES_NAMES, selectorBusesNames);
                    bndl.putString(ScheduleLoader.SELECTOR_INTERCITY_BASES_NAMES, selectorIntercityBuses);
                    bndl.putString(ScheduleLoader.HTTP_BASES_NAMES, httpBusesNames);
                    getSupportLoaderManager().initLoader(LOADER_ID_UPDATE_SCHEDULE, bndl, this);
                } else {
                    initToolbar();
                    initNavigationView();
                    initTabs();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_head, menu);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentT;
        switch (item.getItemId()) {
            case R.id.sityTaxi:
                intentT = new Intent(activity, TaxiActivity.class);
                intentT.putExtra("index", 1);
                intentT.putExtra("title", getString(R.string.sityTaxi));
                startActivity(intentT);
                break;
            case R.id.interTaxi:
                intentT = new Intent(activity, TaxiActivity.class);
                intentT.putExtra("index", 2);
                intentT.putExtra("title", getString(R.string.interTaxi));
                startActivity(intentT);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!first) {
            if (adapter == null) {
                adapter = new TabFragmentAdapter(this, getSupportFragmentManager(), checketBus);
            }
        }
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TabFragmentAdapter(this, getSupportFragmentManager(), checketBus);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigation = (NavigationView) findViewById(R.id.navigation);

        Menu menu = navigation.getMenu();
        final ArrayList<MenuItem> ItemMenu = new ArrayList<>();
        final ArrayList<MenuItem> ItemMenuRemind = new ArrayList<>();
        ItemMenu.add((MenuItem) menu.getItem(2).getSubMenu().getItem(0).setIcon(R.drawable.checkbox).setChecked(checketBus[0]));
        ItemMenu.add((MenuItem) menu.getItem(2).getSubMenu().getItem(1).setIcon(R.drawable.checkbox).setChecked(checketBus[1]));
        ItemMenu.add((MenuItem) menu.getItem(2).getSubMenu().getItem(2).setIcon(R.drawable.checkbox).setChecked(checketBus[2]));
        ItemMenu.add((MenuItem) menu.getItem(2).getSubMenu().getItem(3).setIcon(R.drawable.checkbox).setChecked(checketBus[3]));
        ItemMenuRemind.add((MenuItem) menu.getItem(1).getSubMenu().getItem(0).setIcon(R.drawable.checkbox).setChecked(checketRemind.get(0)));
        ItemMenuRemind.add((MenuItem) menu.getItem(1).getSubMenu().getItem(1).setIcon(R.drawable.checkbox).setChecked(checketRemind.get(1)));
        ItemMenuRemind.add((MenuItem) menu.getItem(1).getSubMenu().getItem(2).setIcon(R.drawable.checkbox).setChecked(checketRemind.get(2)));
        menu.getItem(0).setTitle(activity.getString(R.string.appdateName) + dataAppdate);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Toast toast = Toast.makeText(getApplicationContext(), activity.getString(R.string.changes), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 150);
                switch (item.getItemId()) {
                    case R.id.one_week:
                        ItemMenuRemind.get(0).setChecked(true);
                        ItemMenuRemind.get(1).setChecked(false);
                        ItemMenuRemind.get(2).setChecked(false);
                        setRem = 0;
                        break;
                    case R.id.one_month:
                        ItemMenuRemind.get(0).setChecked(false);
                        ItemMenuRemind.get(1).setChecked(true);
                        ItemMenuRemind.get(2).setChecked(false);
                        setRem = 1;
                        break;
                    case R.id.never_remind:
                        ItemMenuRemind.get(0).setChecked(false);
                        ItemMenuRemind.get(1).setChecked(false);
                        ItemMenuRemind.get(2).setChecked(true);
                        setRem = -1;
                        break;
                    case R.id.pinsk_city:
                        setTabBas(0);
                        toast.show();
                        break;
                    case R.id.pinsk_intercity:
                        setTabBas(1);
                        toast.show();
                        break;
                    case R.id.ivanovo_intercity:
                        setTabBas(2);
                        toast.show();
                        break;
                    case R.id.logishin_intercity:
                        setTabBas(3);
                        toast.show();
                        break;
                    case R.id.set_appdate:
                        if (isConnectingToInternet()) {
                            drawerLayout.closeDrawers();//Закрывает меню
                            Bundle bndl = new Bundle();
                            bndl.putString(ScheduleLoader.TITLE, activity.getString(R.string.AppdataTitle));
                            bndl.putString(ScheduleLoader.MESEGE, activity.getString(R.string.AppdataMes));
                            bndl.putString(ScheduleLoader.SELECTOR_BASES_NAMES, selectorBusesNames);
                            bndl.putString(ScheduleLoader.SELECTOR_INTERCITY_BASES_NAMES, selectorIntercityBuses);
                            bndl.putString(ScheduleLoader.HTTP_BASES_NAMES, httpBusesNames);
                            activity.getSupportLoaderManager().initLoader(LOADER_ID_UPDATE_SCHEDULE, bndl, (LoaderManager.LoaderCallbacks<Object>) activity);
                            //new GetBus(activity.getString(R.string.AppdataTitle), activity.getString(R.string.AppdataMes), true).execute();
                        } else {
                            Toast toast2 = Toast.makeText(getApplicationContext(), activity.getString(R.string.NoInternet), Toast.LENGTH_LONG);
                            toast2.setGravity(Gravity.TOP, 0, 150);
                            toast2.show();
                        }
                        break;
                    default:
                        break;
                }
                setTabBas(100);
                return false;//если true глючит множественный выбор
            }

            public void setTabBas(int item) {
                int size = checketBus.length;
                for (int i = 0; i < size; i++) {
                    if (i == item) {
                        boolean set = checketBus[i] == true ? false : true;
                        checketBus[item] = set;
                        if (set == false) {
                            boolean one = false;
                            for (int j = 0; j < size; j++) {
                                if (checketBus[j]) one = true;
                            }
                            if (one) {
                                checketBus[item] = false;
                                ItemMenu.get(i).setChecked(false);
                                ItemMenu.get(i).setCheckable(false);
                            } else {
                                checketBus[item] = true;
                                ItemMenu.get(i).setChecked(true);
                                ItemMenu.get(i).setCheckable(true);
                            }
                        } else {
                            checketBus[item] = set;
                            ItemMenu.get(i).setChecked(set);
                            ItemMenu.get(i).setCheckable(set);
                        }
                    } else {
                        ItemMenu.get(i).setChecked(checketBus[i]);
                        ItemMenu.get(i).setCheckable(checketBus[i]);
                    }
                }
            }
        });
    }

    private void initRemindMe(long date, int set) {
        if (set != -1) {
            if (set == 0) {
                checketRemind.add(0, true);
                checketRemind.add(1, false);
                checketRemind.add(2, false);
            } else {
                checketRemind.add(0, false);
                checketRemind.add(1, true);
                checketRemind.add(2, false);
            }
            Calendar currentDate = Calendar.getInstance();
            long add = setRemind[set];
            currentDate.setTimeInMillis(date);
            SimpleDateFormat simpleFormatter = new SimpleDateFormat("dd.MM.yyyy");
            dataAppdate = simpleFormatter.format(currentDate.getTime());
            dlg1 = Dialog1.getInstance(dataAppdate);
            currentDate.setTimeInMillis(date + add);
            Calendar validDate = Calendar.getInstance();
            dataAppdate = first ? "" : " (" + dataAppdate + ")";
            if (validDate.after(currentDate) && date!=0) {
                dlg1.show(getSupportFragmentManager(), "dlg1");
            }

        } else {
            checketRemind.add(0, false);
            checketRemind.add(1, false);
            checketRemind.add(2, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        modelNavigationView.saveMemuProperty(checketBus, setRem);
    }

    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static Handler getH() {
        return h;
    }
}
