package pavelsemenkov.bus.controllers.loaders;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;

import pavelsemenkov.bus.model.ModelNavigationView;

/**
 * Created by Павел on 18.10.2016.
 */
public class myProgressDialog  implements Runnable {

    private AppCompatActivity activity;
    private static Handler h;
    private int progressCount;
    private static int k;
    private String tit, mes;
    private final String LOG_TAG = "myLogs";
    private static android.app.ProgressDialog pd;

    public myProgressDialog(AppCompatActivity activity, String tit, String mes){
        this.activity = activity;
        this.tit = tit;
        this.mes = mes;
    }
    @Override
    public void run() {
        Log.d(LOG_TAG, "--- loadDB 1 ---");
        final ModelNavigationView modelNavigationView = ModelNavigationView.getInstance(activity);
        pd = new android.app.ProgressDialog(activity);
        pd.setTitle(tit);
        pd.setMessage(mes);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        // меняем стиль на индикатор
        pd.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        // включаем анимацию ожидания
        pd.setIndeterminate(true);
        pd.show();
        h = new Handler() {
            public void handleMessage(Message msg) {
                // выключаем анимацию ожидания
                pd.setIndeterminate(false);
                int total = msg.arg1;
                pd.setProgress(total);
                if (pd.getProgress() < pd.getMax()) {

                } else {
                    pd.dismiss();
                    Calendar validDate = Calendar.getInstance();
                    long diff = validDate.getTimeInMillis();
                    modelNavigationView.saveDateLoad(diff);
                }
            }
        };
    }
    public static void SetProgressCount(int k) {
        Message msg = h.obtainMessage();
        msg.arg1 = k;
        h.sendMessage(msg);
    }
    public static void SetMaxProgressCount(int progressCount) {
        pd.setMax(progressCount);
    }
}
