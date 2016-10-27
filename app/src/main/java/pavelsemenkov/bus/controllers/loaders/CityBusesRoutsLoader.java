package pavelsemenkov.bus.controllers.loaders;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pavelsemenkov.bus.ScheduleLoaderService;
import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;

/**
 * Created by Павел on 17.10.2016.
 */
public class CityBusesRoutsLoader {
    private static CityBusesRoutsLoader instance;
    private static final Object lock = new Object();
    private ArrayList<String> stopList = new ArrayList<String>();
    private int  stop_int, k;
    private String stop_day;
    private boolean stop_pay = false;
    private BusProvider provider;
    final String LOG_TAG = "myLogs";
    private Elements contentr;

    private CityBusesRoutsLoader(BusProvider provider) {
        this.provider = provider;
    }

    public static CityBusesRoutsLoader getInstance(BusProvider provider) {
        if (instance==null) {
            synchronized (lock) {
                if (instance==null)
                    instance = new CityBusesRoutsLoader(provider);
            }
        }
        return instance;
    }

    public boolean loadBusesRouts() throws IOException {
        Cursor c = provider.queryT(BusProvider.CONTENT_BUS_URI, null, null, null, null);
        ContentValues cv = new ContentValues();
        //ставим позицию курсора на первую строку выборки если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(BusTable.COLUMN_ID_BUS);
            int numColIndex = c.getColumnIndex(BusTable.COLUMN_BAS_NUMBER);
            int httpColIndex = c.getColumnIndex(BusTable.COLUMN_BAS_HTTP);
            do {
                stop_int = 8;
                stop_pay = false;
                boolean day = false;
                String pay = null;
                for (String contents : GetStopList(c.getString(httpColIndex))) {
                    cv.clear();
                    if (stop_pay) cv.put(BusTable.COLUMN_BUS_STOP_PAY, pay);
                    String stopD;
                    stopD = contents.replaceAll("([01]?[0-9]|2[0-3]):[0-5][0-9]|^\\s*", "");//уберем время
                    stopD = stopD.replaceAll("[\\s]{2,}", " ");//уберем пробелы
                    stopD = stopD.replaceAll("[\\s-]{2,}", "");//уберем пробелы и -
                    stopD = stopD.trim();
                    stopD = stopD.replaceAll("-+$", "");//уберем "-" в конце
                    if (stopD == "") {
                        continue;
                    }
                    Pattern n = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]", Pattern.MULTILINE);//оставим только время
                    Matcher m = n.matcher(contents);
                    boolean print = false;
                    String timeD = "";
                    while (m.find()) {
                        print = true;
                        timeD = timeD + " " + m.group();
                    }
                    if (print) {
                        cv.put(BusTable.COLUMN_BUS_STOP_TIME, timeD);
                        cv.put(BusTable.COLUMN_BUS_STOP, stopD);
                    } else {
                        if (getStopPeriod(stopD)) {
                            day = true;
                        } else if (stop_pay) {
                            pay = stopD;
                        }
                        continue;
                    }
                    if (day) {
                        cv.put(BusTable.COLUMN_BUS_STOP_WEEK_DAY_CODE, stop_int);
                        cv.put(BusTable.COLUMN_BUS_STOP_WEEK_DAY_NAME, stop_day);
                        cv.put(BusTable.COLUMN_BUS_STOP_PAY, pay);
                        cv.put(BusTable.COLUMN_BUS_STOP_NAME, c.getString(numColIndex));
                        cv.put(BusTable.COLUMN_INT_BUS_ID, c.getInt(idColIndex));
                        provider.insertT(BusProvider.CONTENT_STOP_CITY_URI, cv);
                        //String p = pay==null ? "null": pay;
                        //Log.d(LOG_TAG, "stop rows " + stopD + timeD+" "+Integer.toString(stop_int)+" "+stop_day+" "+p+" "+c.getString(numColIndex)+" "+c.getInt(idColIndex));
                    }
                }
                stopList.clear();
                myProgressDialog.SetProgressCount(ScheduleLoaderService.IncrementProgressCount());
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "GetStop 0 rows");
        c.close();
        Log.d(LOG_TAG, "h.sendEmptyMessage(++k);");
        myProgressDialog.SetProgressCount(ScheduleLoaderService.IncrementProgressCount());
        return true;
    }

    private boolean getStopPeriod(String str) {
        boolean insert = false;
        if (str.contains("Рабочие дни")) {
            stop_day = "Рабочие дни";
            stop_int = 12345;
            insert = true;
        } else if (str.contains("Выходные дни")) {
            stop_day = "Выходные дни";
            stop_int = 60;
            insert = true;
        } else if (str.contains("Суббота")) {
            stop_day = "Суббота";
            stop_int = 6;
            insert = true;
        } else if (str.contains("Воскресенье")) {
            stop_day = "Воскресенье";
            stop_int = 0;
            insert = true;
        } else if (str.contains("Ежедневно")) {
            stop_day = "Ежедневно";
            stop_int = 1234560;
            insert = true;
        } else if (str.contains("Вторник, четверг")) {
            stop_day = "Вт, Чт";
            stop_int = 24;
            insert = true;
        } else if (str.contains("Стоимость проезда")) {
            stop_pay = true;
        }
        return insert;
    }

    private ArrayList<String> GetStopList(String http) {
        Document doc;
        try {
            doc = Jsoup.connect(http).get();
            contentr = doc.select(".main-column");
            for (Element element : contentr) {
                Elements elements = element.children();
                int size = elements.size();
                for (int i = 1; i < size; i++) {
                    if(elements.get(i).toString().contains("<br>")){
                        String[] sel;
                        sel = elements.get(i).toString().split("<br>");
                        for (String r : sel) {
                            Document text = Jsoup.parse(r);
                            //r = r.replaceAll("<\\/?font.+?>|<b>|&nbsp;|<hr>|</font>", "");
                            stopList.add(text.text());
                        }
                    }else {
                        Elements elements2 = elements.get(i).children();
                        int size2 = elements2.size();
                        for (int j = 0; j < size2; j++) {
                            Elements elements3 = elements2.get(j).children();
                            int size3 = elements3.size();
                            if (size3 > 3) {
                                for (int k = 0; k < size3; k++) {
                                    //Log.d(LOG_TAG, elements2.get(j).toString());
                                    stopList.add(elements3.get(k).text());
                                }
                            } else {
                                //Log.d(LOG_TAG, elements2.get(j).toString());
                                stopList.add(elements2.get(j).text());
                            }
                        }
                        if(size2==0){
                            stopList.add(elements.get(i).text());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopList;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private CityBusRoutAsyncLoader StartAsyncTaskInParallel(CityBusRoutAsyncLoader task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
        return task;
    }

}
