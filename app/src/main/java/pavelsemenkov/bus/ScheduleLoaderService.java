package pavelsemenkov.bus;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pavelsemenkov.bus.controllers.loaders.BusesNamesLoader;
import pavelsemenkov.bus.controllers.loaders.BusesRoutsLoader;
import pavelsemenkov.bus.controllers.loaders.myProgressDialog;
import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.model.AsyncResult;

public class ScheduleLoaderService extends Service {

    public static final String TITLE = "title";
    public static final String MESEGE = "mesege";
    public static final String SELECTOR_BASES_NAMES = "select";
    public static final String SELECTOR_INTERCITY_BASES_NAMES = "selectInter";
    public static final String HTTP_BASES_NAMES = "http";
    public static final String HTTP_INTER_CITY = "httpInterCity";
    public static final int THREAD_COUNT = 3;
    public final static String PINTENT = "pendingIntent";
    private AppCompatActivity activity;
    private final String LOG_TAG = "myLogs";
    private static int progressCount, k;
    private ContentProviderClient providerClient;
    private BusProvider provider;
    String[] httpInterCity;
    String title, mesege, selectorBusesNames, httpBusesNames, selectorIntersityBuses;

    ExecutorService es;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "ScheduleLoaderService onCreate");
        es = Executors.newFixedThreadPool(2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "ScheduleLoaderService onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "ScheduleLoaderService onStartCommand");
        PendingIntent pi = intent.getParcelableExtra(PINTENT);
        activity = MainActivity.getInstance();
        title = intent.getStringExtra(TITLE);
        mesege = intent.getStringExtra(MESEGE);
        selectorBusesNames = intent.getStringExtra(SELECTOR_BASES_NAMES);
        selectorIntersityBuses = intent.getStringExtra(SELECTOR_INTERCITY_BASES_NAMES);
        httpBusesNames = intent.getStringExtra(HTTP_BASES_NAMES);
        httpInterCity = intent.getStringArrayExtra(HTTP_INTER_CITY);
        k = 0;
        MyRun mr = new MyRun(THREAD_COUNT, startId, pi);
        es.execute(mr);

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    class MyRun implements Runnable {

        int time;
        int startId;
        PendingIntent pi;

        public MyRun(int time, int startId, PendingIntent pi) {
            this.time = time;
            this.startId = startId;
            this.pi = pi;
            Log.d(LOG_TAG, "MyRun#" + startId + " create");
        }

        public void run() {
            AsyncResult<Boolean> data = new AsyncResult<Boolean>();
            try {
                providerClient = activity.getContentResolver().acquireContentProviderClient(BusProvider.DATABUSE_URI);
                provider = (BusProvider) providerClient.getLocalContentProvider();
                provider.reset();
                provider.beginTransaction();
                activity.runOnUiThread(new myProgressDialog(activity, title, mesege));
                BusesNamesLoader busesNamesLoader = BusesNamesLoader.getInstance(provider, httpBusesNames, selectorBusesNames);
                data.setData(busesNamesLoader.loadBusesNames());
                progressCount = busesNamesLoader.getProgressCount() + httpInterCity.length;
                myProgressDialog.SetMaxProgressCount(progressCount);
                data.setData(BusesRoutsLoader.getInstance(provider, httpInterCity, selectorIntersityBuses).loadBusesRouts());
                //data.setData(IntercityBusesRoutsLoader.getInstance(provider, httpInterCity, selectorIntersityBuses).loadIntercityBusesRouts());
                myProgressDialog.SetProgressCount(ScheduleLoaderService.IncrementProgressCount());
                provider.setTransactionSuccessful();
            } catch (Exception e) {
                data.setException(e);
            } finally {
                provider.endTransaction();
                providerClient.release();
                Intent intent = new Intent().putExtra(MainActivity.PARAM_RESULT, data.getData());
                try {
                    pi.send(ScheduleLoaderService.this, MainActivity.STATUS_FINISH, intent);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                stop();
            }


        }

        void stop() {
            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
        }
    }

    public static int IncrementProgressCount() {
        return k++;
    }
    public static int getCountFinishThread() {
        return k;
    }
}
