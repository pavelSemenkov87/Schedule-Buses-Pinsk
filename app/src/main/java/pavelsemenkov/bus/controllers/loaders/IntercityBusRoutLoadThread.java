package pavelsemenkov.bus.controllers.loaders;

import android.content.ContentValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pavelsemenkov.bus.ScheduleLoaderService;
import pavelsemenkov.bus.database.BusTable;

/**
 * Created by Павел on 17.10.2016.
 */
public class IntercityBusRoutLoadThread implements Runnable {
    private Elements contentr;
    private String http, select;
    private int city = 0;
    private static int id = 0;

    public IntercityBusRoutLoadThread(String http, String select, int city) {
        this.http = http;
        this.select = select;
        this.city = city;
    }

    @Override
    public void run() {
        try {
            GetList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setId() {
        id = BusesRoutsLoader.getIdIntCity();
    }

    private void GetList() throws IOException {
        List<ContentValues> valuesB = new ArrayList<>(), valuesS = new ArrayList<>();
        ContentValues cvB = new ContentValues(), cvS = new ContentValues();
        Elements elementsTr, elementsTable;
        contentr = Jsoup.connect(http).get().select(select);
        boolean table = false, firstInfo = false;
        String info = null;
        for (Element element : contentr) {
            elementsTable = element.children();
            //Log.d("myLogs", elementsTable.toString());
            int size = elementsTable.size();
            for (int i = 0; i < size; i++) {
                if (elementsTable.get(i).nodeName() == "h2") {
                    if (table) {
                        cvB.put(BusTable.COLUMN_INT_INFO, info);
                        cvB.put(BusTable.COLUMN_INT_CITY, city);
                        cvB.put(BusTable.COLUMN_ID_INT_BUS, id);
                        valuesB.add(cvB);
                        cvB = new ContentValues();
                        setId();
                    }
                    info = null;
                    cvB.put(BusTable.COLUMN_INT_ROUTE_TYP, elementsTable.get(i).text());
                    //Log.d("myLogs", elementsTable.get(i).text());
                    table = false;
                    firstInfo = false;
                }
                if (elementsTable.get(i).nodeName() == "table") {
                    //Находим все строки tr и записываем в массив elementsTr
                    elementsTr = elementsTable.get(i).getElementsByTag("tr");
                    Elements elementsTd;
                    table = true;//Индекатор начала обработки таблицы
                    ArrayList<String> name = new ArrayList<>();//массив названий для каждой строки-таблицы
                    int sizeTr = elementsTr.size();
                    for (int j = 0; j < sizeTr; j++) {
                        //В каждом tr находим все столбцы td и записываем в массив elementsTd
                        elementsTd = elementsTr.get(j).getElementsByTag("td");
                        int sizeTd = elementsTd.size();//Колличество столбцов в таблице
                        String route = null;
                        cvB.put(BusTable.COLUMN_INT_TABLE_IND, sizeTd - 1);
                        for (int k = 0; k < sizeTd; k++) {
                            //если это первая строка добавляем содержимое столбца в массив названий
                            if (j == 0) {
                                name.add(k, elementsTd.get(k).text());
                            } else {
                                if (sizeTd == 3) {
                                    if (k == 1) {
                                        route = elementsTd.get(k).text();
                                    } else {
                                        cvS.put(BusTable.COLUMN_INT_BUS_ID, id);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_NAME, name.get(k % sizeTd));
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_ROUTE, route);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_TIME, elementsTd.get(k).text());
                                        valuesS.add(cvS);
                                        cvS = new ContentValues();
                                    }
                                }
                                if (sizeTd == 5) {
                                    if (k == 2) {
                                        route = elementsTd.get(k).text();
                                    } else {
                                        cvS.put(BusTable.COLUMN_INT_BUS_ID, id);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_NAME, name.get(k % sizeTd));
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_ROUTE, route);
                                        cvS.put(BusTable.COLUMN_INT_CITY_STOP_TIME, elementsTd.get(k).text());
                                        valuesS.add(cvS);
                                        cvS = new ContentValues();
                                        //Log.d("myLogs", elementsTd.get(k).text()+" " +Integer.toString(id)+" " +name.get(k%sizeTd)+" " +(route = route==null?"null":route));

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
                        info = info + "<br>" + elementsTable.get(i).text().replaceAll("[\\s]{2,}", " ");
                    } else {
                        if (elementsTable.get(i).text().replaceAll("[\\s]{2,}", " ") != "") {
                            info = elementsTable.get(i).text().replaceAll("[\\s]{2,}", " ");
                            firstInfo = true;
                        }
                    }
                }
            }
            if (table) {
                cvB.put(BusTable.COLUMN_ID_INT_BUS, id);
                cvB.put(BusTable.COLUMN_INT_CITY, city);
                cvB.put(BusTable.COLUMN_INT_INFO, info);
                valuesB.add(cvB);
                cvB = new ContentValues();
                setId();
            }
        }
        BusesRoutsLoader.addListIntBusName(valuesB);
        BusesRoutsLoader.addListIntBusStop(valuesS);
        myProgressDialog.SetProgressCount(ScheduleLoaderService.IncrementProgressCount());
    }
}
