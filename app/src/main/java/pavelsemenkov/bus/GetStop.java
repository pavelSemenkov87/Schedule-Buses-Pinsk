package pavelsemenkov.bus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pavelsemenkov.bus.database.DBHelper;

class GetStop extends AsyncTask<Void, Void, Void> {

    private ArrayList<String> stopList = new ArrayList<String>();
    private int  stop_int;
    private static int k;
    private String stop_day;
    private boolean stop_pay = false, day = false;
    private static Handler h;
    private DBHelper dbHelper;
    private String[] http;
    final String LOG_TAG = "myLogs";
    private Elements contentr;

    public GetStop(String[] http) {
        this.http = http;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Document doc;
        // Подключаемся к БД
        dbHelper = MainActivity.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // создаем объект для данных
        ContentValues cv = new ContentValues();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("bus", null, null, null, null, null, null);
        //ставим позицию курсора на первую строку выборки если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("_id");
            int numColIndex = c.getColumnIndex("bus_num");
            int httpColIndex = c.getColumnIndex("http");
            do {
                stop_int = 8;
                stop_pay = false;
                day = false;
                String pay = null;
                for (String contents : GetStopList(c.getString(httpColIndex))) {
                    cv.clear();
                    if (stop_pay) cv.put("bus_pay", pay);
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
                        cv.put("bus_time", timeD);
                        cv.put("bus_stop", stopD);
                    } else {
                        if (getStopPeriod(stopD)) {
                            day = true;
                        } else if (stop_pay) {
                            pay = stopD;
                        }
                        continue;
                    }
                    if (day) {
                        cv.put("bus_stop_int", stop_int);
                        cv.put("bus_stop_day", stop_day);
                        cv.put("bus_pay", pay);
                        cv.put("bus_name", c.getString(numColIndex));
                        cv.put("bus_id", c.getInt(idColIndex));
                        //String p = pay==null ? "null": pay;
                        //Log.d(LOG_TAG, "stop rows " + stopD + timeD+" "+Integer.toString(stop_int)+" "+stop_day+" "+p+" "+c.getString(numColIndex)+" "+c.getInt(idColIndex));
                        db.insert("stop", null, cv);
                    }
                }
                h = MainActivity.getH();
                h.sendEmptyMessage(++k);
                //Log.d(LOG_TAG, "k = " + String.valueOf(k));
                stopList.clear();
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "GetStop 0 rows");
        c.close();
        Log.d(LOG_TAG, "h.sendEmptyMessage(++k);");
        h.sendEmptyMessage(++k);
        dbHelper.close();
        return null;
    }

    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        new GetIntercityBus(http).execute();
        Log.d("myLogs", "h.sendEmptyMessage(++k);");
    }

    public static void setH() {
        h.sendEmptyMessage(++k);
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

    /*private String SetSelector(String num) {
        String select;
        switch (num) {
            case "3A":
            case "9":
            case "13A":
                select = " .main-column > h1, " +
                        ".main-column > div > span";
                break;
            case "15":
                select = " .main-column > span, " +
                        ".main-column > b";
                break;
            case "8A":
                select = " .main-column > h2, " +
                        " .main-column > li";
                break;
            case "4":
            case "5":
            case "10":
            case "11":
            case "17":
            case "23":
                select = " .main-column > h1, " +
                        ".main-column > span";
                break;
            case "32":
                select = " .main-column > h1, " +
                        ".main-column > div > span";
                break;
            case "10T":
            case "11T"://менялся
                select = ".main-column > b > span," +
                        ".main-column > span > span";
                break;
            case "24":
                select = ".main-column > span > ul > li," +
                        " .main-column > h2, " +
                        " .main-column > h1, " +
                        ".main-column > font," +
                        ".main-column > font > b";
                break;
            case "19":
            case "23A":
                select = ".main-column > font";
                flag = 23;
                break;
            case "36":
                select = ".main-column > ul > li," +
                        " .main-column > h2, " +
                        " .main-column > h1, " +
                        ".main-column > span";
                break;
            default:
                select = ".main-column > span > span," +
                        " .main-column > b," +
                        ".main-column > div > span," +
                        ".main-column > span > ul > li," +
                        " .main-column > li," +
                        " .main-column > h2, " +
                        " .main-column > h1, " +
                        ".main-column > font," +
                        ".main-column > font > b," +
                        ".main-column > span";
                break;
        }
        return select;
    }*/
}


