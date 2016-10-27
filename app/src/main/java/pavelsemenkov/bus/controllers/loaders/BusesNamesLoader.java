package pavelsemenkov.bus.controllers.loaders;

import android.content.ContentValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import pavelsemenkov.bus.database.BusProvider;

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
                String busT = contents.text(), number;
                int lengthTitle, lengthT = busT.length();
                String[] busTitle;
                link = contents.select("a").first();
                String httpBus = link.attr("abs:href");
                busTitle = busT.split("^\\d+\\s|^\\d+\\D\\s");
                lengthTitle = busTitle[1].length();
                number = busT.substring(0, (lengthT - lengthTitle));
                number = number.replaceAll("[\\s]{2,}", " ");
                number = number.trim();
                progressCount++;
                cv.clear();
                cv.put("bus_num", number);
                cv.put("bus", busTitle[1]);
                cv.put("http", httpBus);
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
