package pavelsemenkov.bus.controllers.loaders;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;

class CityBusRoutAsyncLoader extends AsyncTask<Void, Void, List<ContentValues>> {

    private ArrayList<String> stopList = new ArrayList<String>();
    private int stop_int;
    private String stop_day, http, num;
    private boolean stop_pay = false, loadFinish;
    private int id;
    private BusProvider provider;
    final String LOG_TAG = "myLogs";
    private Elements contentr;

    public CityBusRoutAsyncLoader(BusProvider provider, int id, String num, String http) {
        this.provider = provider;
        this.id = id;
        this.num = num;
        this.http = http;
        loadFinish = false;
    }
    public boolean getIsFinish(){return loadFinish;}

    protected List<ContentValues> doInBackground(Void... params) {
        List<ContentValues> values = new ArrayList<>();
        ContentValues cv = new ContentValues();
        stop_int = 8;
        stop_pay = false;
        boolean day = false;
        String pay = null;
        for (String contents : GetStopList(http)) {
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
                cv.put(BusTable.COLUMN_BUS_STOP_NAME, num);
                cv.put(BusTable.COLUMN_INT_BUS_ID, id);
                values.add(cv);
                //String p = pay==null ? "null": pay;
                //Log.d(LOG_TAG, "stop rows " + stopD + timeD+" "+Integer.toString(stop_int)+" "+stop_day+" "+p+" "+c.getString(numColIndex)+" "+c.getInt(idColIndex));
            }
        }
        //Log.d(LOG_TAG, "k = " + String.valueOf(k));
        stopList.clear();
        Log.d(LOG_TAG, "CityBusRoutAsyncLoader");
        return values;
    }

    protected void onPostExecute(List<ContentValues> value) {
        if (value.size()>0){
            Log.d(LOG_TAG, "CityBusRoutAsyncLoader provider.bulkInsert");
            provider.bulkInsert(BusProvider.CONTENT_STOP_CITY_URI, value.toArray(new ContentValues[value.size()]));
            loadFinish = true;
        }
        //myProgressDialog.SetProgressCount();
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
                    if (elements.get(i).toString().contains("<br>")) {
                        String[] sel;
                        sel = elements.get(i).toString().split("<br>");
                        for (String r : sel) {
                            Document text = Jsoup.parse(r);
                            //r = r.replaceAll("<\\/?font.+?>|<b>|&nbsp;|<hr>|</font>", "");
                            stopList.add(text.text());
                        }
                    } else {
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
                        if (size2 == 0) {
                            stopList.add(elements.get(i).text());
                        }
                    }
                }
            }
            /*switch (flag) {
                case 23:
                    String[] sel;
                    sel = contentr.toString().split("<br>");
                    for (String r : sel) {
                        r = r.replaceAll("<\\/?font.+?>|<b>|&nbsp;|<hr>|</font>", "");
                        stopList.add(r);
                    }
                    break;
                default:
                    for (Element r : contentr) {
                        //Log.d(LOG_TAG, r.toString());
                        stopList.add(r.text());
                    }
                    break;
            }*/
            //Log.d(LOG_TAG, contentr.text());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopList;
    }
}


