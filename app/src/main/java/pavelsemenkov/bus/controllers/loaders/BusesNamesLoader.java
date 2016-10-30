package pavelsemenkov.bus.controllers.loaders;

import android.content.ContentValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;

/**
 * Created by Павел on 17.10.2016.
 */
public class BusesNamesLoader {
    private static BusesNamesLoader instance;
    private static final Object lock = new Object();
    private static int progressCount;
    private Elements content;
    private Element link;
    private BusProvider provider;
    private String selectorBusesNames, httpBusesNames;

    private BusesNamesLoader(BusProvider provider, String http, String select) {
        httpBusesNames = http;
        selectorBusesNames = select;
        this.provider = provider;
    }

    public static BusesNamesLoader getInstance(BusProvider provider, String http, String select) {
        if (instance==null) {
            synchronized (lock) {
                if (instance==null)
                    instance = new BusesNamesLoader(provider, http, select);
            }
        }
        return instance;
    }

    public boolean loadBusesNames() throws IOException {
        Document doc;
        ContentValues cv = new ContentValues();
        try {
            doc = Jsoup.connect(httpBusesNames).get();
            content = doc.select(selectorBusesNames);
            progressCount = 0;
            for (Element contents : content) {
                String busT = contents.text(), number, busName;
                int lengthTitle = 0, lengthT = busT.length();
                String[] busTitle;
                link = contents.select("a").first();
                if(progressCount == 15){
                    progressCount=15;
                }
                String httpBus = link.attr("abs:href");
                busTitle = busT.split("^\\s?\\d+\\s[А-Яа-яA-Za-z]\\s|^\\s?\\d+\\s|^\\s?\\d+[А-Яа-яA-Za-z]\\s");
                if(busTitle.length>1){
                    lengthTitle = busTitle[1].length();
                    busName = busTitle[1];
                }else busName = "";
                number = busT.substring(0, (lengthT - lengthTitle));
                number = number.replaceAll("[\\s]+", "");
                number = number.trim();
                progressCount++;
                cv.clear();
                cv.put(BusTable.COLUMN_BAS_NUMBER, number);
                cv.put(BusTable.COLUMN_BAS_TEXT, busName);
                cv.put(BusTable.COLUMN_BAS_HTTP, httpBus);
                //Log.d(LOG_TAG, String.valueOf(progCount) + " bus rows " + busT + httpBus);
                provider.insertT(BusProvider.CONTENT_BUS_URI, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getProgressCount() {
        return progressCount;
    }
}
