package pavelsemenkov.bus;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import pavelsemenkov.bus.database.DBHelper;

class GetIntercityBus extends AsyncTask<Void, Void, Void> {
    final String LOG_TAG = "myLogs";
    private Elements contentr;
    private DBHelper dbHelper;
    private String[] http;
    private int id = 0, city = 0;
    private String select = ".main-column";

    public GetIntercityBus(String[] http) {
        this.http = http;
    }

    private void GetList(String http) throws IOException {
        dbHelper = MainActivity.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
                    cvB.put("_id", id);
                    if (table) {
                        cvB.put("info", info);
                        cvB.put("city", city);
                        db.insert("intercity_bus", null, cvB);
                        id++;
                    }
                    info = null;
                    cvB.put("route_typ", elementsTable.get(i).text());
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
                        cvB.put("ind", sizeTd-1);
                        for (int k = 0; k < sizeTd; k++) {
                            if(j==0){
                                name.add(k, elementsTd.get(k).text());
                            }else {
                                if(sizeTd==3){
                                    if(k!=1){
                                        cvS.put("bus_id", id);
                                        cvS.put("name", name.get(k%sizeTd));
                                        cvS.put("route", route);
                                        cvS.put("time", elementsTd.get(k).text());
                                        db.insert("intercity_stop", null, cvS);
                                        //Log.d("myLogs", elementsTd.get(k).text()+" " +Integer.toString(id)+" " +name.get(k%sizeTd)+" " +(route = route==null?"null":route));
                                    }else {
                                        route = elementsTd.get(k).text();
                                    }
                                }
                                if(sizeTd==5){
                                    if(k!=2){
                                        cvS.put("bus_id", id);
                                        cvS.put("name", name.get(k%sizeTd));
                                        cvS.put("route", route);
                                        cvS.put("time", elementsTd.get(k).text());
                                        db.insert("intercity_stop", null, cvS);
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
                cvB.put("_id", id);
                cvB.put("city", city);
                cvB.put("info", info);
                //Log.d("myLogs", info);
                db.insert("intercity_bus", null, cvB);
                id++;
            }
        }
        dbHelper.close();
    }

    protected Void doInBackground(Void... paramVarArgs) {
        int j = http.length;
        for (int i = 0; i < j; i++) {
            try {
                GetList(http[i]);
                GetStop.setH();
                city++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("myLogs", "h.sendEmptyMessage(++k);");
        }
        return null;
    }

    protected void onPostExecute(Void paramString) {
        super.onPostExecute(paramString);
    }
}
