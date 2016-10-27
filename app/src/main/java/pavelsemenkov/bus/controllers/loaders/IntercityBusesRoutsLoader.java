package pavelsemenkov.bus.controllers.loaders;

import android.content.ContentValues;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import pavelsemenkov.bus.ScheduleLoaderService;
import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;
import pavelsemenkov.bus.database.DBHelper;

/**
 * Created by Павел on 17.10.2016.
 */
public class IntercityBusesRoutsLoader {
    final String LOG_TAG = "myLogs";
    private Elements contentr;
    private DBHelper dbHelper;
    private String[] http;
    private int id = 0, city = 0;
    private BusProvider provider;
    private static IntercityBusesRoutsLoader instance;
    private static final Object lock = new Object();
    private String select;

    private IntercityBusesRoutsLoader(BusProvider provider, String[] http, String select) {
        this.provider = provider;
        this.http = http;
        this.select = select;
    }

    public static IntercityBusesRoutsLoader getInstance(BusProvider provider, String[] http, String select) {
        if (instance==null) {
            synchronized (lock) {
                if (instance==null)
                    instance = new IntercityBusesRoutsLoader(provider, http, select);
            }
        }
        return instance;
    }

    public boolean loadIntercityBusesRouts() throws IOException {
        int j = http.length;
        for (int i = 0; i < j; i++) {
            try {
                GetList(http[i]);
                myProgressDialog.SetProgressCount(ScheduleLoaderService.IncrementProgressCount());
                city++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("myLogs", "h.sendEmptyMessage(++k);");
        }
        city = 0;
        return true;
    }

    private void GetList(String http) throws IOException {
        // создаем объект для данных
        ContentValues cvB = new ContentValues(), cvS = new ContentValues();
        Elements elementsTr, elementsTable;
        contentr = Jsoup.connect(http).get().select(select);
        boolean table = false, firstInfo = false;
        String info = null;
        for (Element element : contentr) {
            elementsTable = element.children();
            int size = elementsTable.size();
            for (int i = 0; i < size; i++) {
                if (elementsTable.get(i).nodeName() == "h2") {
                    cvB.put(BusTable.COLUMN_ID_INT_BUS, id);
                    if (table) {
                        cvB.put(BusTable.COLUMN_INT_INFO, info);
                        cvB.put(BusTable.COLUMN_INT_CITY, city);
                        provider.insertT(BusProvider.CONTENT_INTERCITY_BUS_CITY_URI, cvB);
                        id++;
                    }
                    info = null;
                    cvB.put(BusTable.COLUMN_INT_ROUTE_TYP, elementsTable.get(i).text());
                    //Log.d("myLogs", elementsTable.get(i).text());
                    table = false;
                    firstInfo = false;
                }
                if (elementsTable.get(i).nodeName() == "table") {
                    elementsTr = elementsTable.get(i).getElementsByTag("tr");
                    Elements elementsTd;
                    table = true;
                    ArrayList<String> name = new ArrayList<>();
                    int sizeTr = elementsTr.size();
                    for (int j = 0; j < sizeTr; j++) {
                        elementsTd = elementsTr.get(j).getElementsByTag("td");
                        int sizeTd = elementsTd.size();
                        String route = null;
                        cvB.put(BusTable.COLUMN_INT_TABLE_IND, sizeTd-1);
                        for (int k = 0; k < sizeTd; k++) {
                            if(j==0){
                                name.add(k, elementsTd.get(k).text());
                            }else {
                                if(sizeTd==3){
                                    if(k!=1){
                                        cvS.put(BusTable.COLUMN_INT_BUS_ID, id);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_NAME, name.get(k%sizeTd));
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_ROUTE, route);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_TIME, elementsTd.get(k).text());
                                        provider.insertT(BusProvider.CONTENT_INTERCITY_BUS_STOP_URI, cvS);
                                        //Log.d("myLogs", elementsTd.get(k).text()+" " +Integer.toString(id)+" " +name.get(k%sizeTd)+" " +(route = route==null?"null":route));
                                    }else {
                                        route = elementsTd.get(k).text();
                                    }
                                }
                                if(sizeTd==5){
                                    if(k!=2){
                                        cvS.put(BusTable.COLUMN_INT_BUS_ID, id);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_NAME, name.get(k%sizeTd));
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_ROUTE, route);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_TIME, elementsTd.get(k).text());
                                        provider.insertT(BusProvider.CONTENT_INTERCITY_BUS_STOP_URI, cvS);
                                        //Log.d("myLogs", elementsTd.get(k).text()+" " +Integer.toString(id)+" " +name.get(k%sizeTd)+" " +(route = route==null?"null":route));
                                    }else {
                                        route = elementsTd.get(k).text();
                                    }
                                }
                            }
                        }
                    }
                    name.clear();
                    continue;
                }
                if (table) {
                    if (firstInfo) {
                        info = info +"<br>" + elementsTable.get(i).text().replaceAll("[\\s]{2,}", " ");
                    }else {
                        if(elementsTable.get(i).text().replaceAll("[\\s]{2,}", " ") != ""){
                            info =  elementsTable.get(i).text().replaceAll("[\\s]{2,}", " ");
                            firstInfo = true;
                        }
                    }
                }
            }
            if (table) {
                cvB.put(BusTable.COLUMN_ID_INT_BUS, id);
                cvB.put(BusTable.COLUMN_INT_CITY, city);
                cvB.put(BusTable.COLUMN_INT_INFO, info);
                //Log.d("myLogs", info);
                provider.insertT(BusProvider.CONTENT_INTERCITY_BUS_CITY_URI, cvB);
                id++;
            }
        }
    }
}
