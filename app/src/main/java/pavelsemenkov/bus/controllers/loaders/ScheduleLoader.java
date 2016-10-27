package pavelsemenkov.bus.controllers.loaders;

import android.content.ContentProviderClient;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;

import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.model.AsyncResult;
import pavelsemenkov.bus.model.ModelNavigationView;

public class ScheduleLoader extends AsyncTaskLoader<AsyncResult<Boolean>> {

    private AsyncResult<Boolean> mResult;
    private ModelNavigationView modelNavigationView;
    public static final String TITLE = "title";
    public static final String MESEGE = "mesege";
    public static final String SELECTOR_BASES_NAMES = "select";
    public static final String SELECTOR_INTERCITY_BASES_NAMES = "selectInter";
    public static final String HTTP_BASES_NAMES = "http";
    private AppCompatActivity activity;
    private final String LOG_TAG = "myLogs";
    private static int progressCount, k;
    private ContentProviderClient providerClient;
    private BusProvider provider;
    private static Handler h;
    String[] httpInterCity;
    String title, mesege, selectorBusesNames, httpBusesNames, selectorIntersityBuses;


    public ScheduleLoader(Context context, Bundle args, String[] http) {
        super(context);
        activity = (AppCompatActivity) context;
        if (args != null){
            title = args.getString(TITLE);
            mesege = args.getString(MESEGE);
            selectorBusesNames = args.getString(SELECTOR_BASES_NAMES);
            selectorIntersityBuses = args.getString(SELECTOR_INTERCITY_BASES_NAMES);
            httpBusesNames = args.getString(HTTP_BASES_NAMES);
        }
        httpInterCity = http;
    }

    @Override
    public AsyncResult<Boolean> loadInBackground() {
        AsyncResult<Boolean> data = new AsyncResult<Boolean>();
        try {
            providerClient = activity.getContentResolver().acquireContentProviderClient(BusProvider.DATABUSE_URI);
            provider = (BusProvider) providerClient.getLocalContentProvider();
            provider.reset();
            provider.beginTransaction();
            BusesNamesLoader busesNamesLoader = BusesNamesLoader.getInstance(provider, httpBusesNames, selectorBusesNames);
            data.setData(busesNamesLoader.loadBusesNames());
            progressCount = busesNamesLoader.getProgressCount()+httpInterCity.length;
            activity.runOnUiThread(new myProgressDialog(activity, progressCount, title, mesege));
            data.setData(CityBusesRoutsLoader.getInstance(provider).loadBusesRouts());
            data.setData(IntercityBusesRoutsLoader.getInstance(provider, httpInterCity, selectorIntersityBuses).loadIntercityBusesRouts());
            provider.setTransactionSuccessful();
        } catch (Exception e) {
            data.setException(e);
        }finally {
            provider.endTransaction();
            providerClient.release();
        }
        return data;
    }

    @Override
    public void deliverResult(AsyncResult<Boolean> result) {
        mResult = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }

        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        mResult = null;
    }
    public static int IncrementProgressCount() {
        return k++;
    }
}
