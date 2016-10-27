package pavelsemenkov.bus.model;


import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Павел on 16.10.2016.
 */
public class ModelNavigationView {

    private SharedPreferences sPref;
    private static final int MODE_PRIVATE = 0x0000;
    public static ModelNavigationView instance;
    private static final Object lock = new Object();
    private final String FIRST_LOAD = "saved_text";
    private final String CITY_TEXT = "cityBus";
    private final String PINSK_TEXT = "pinskBus";
    private final String IVANAVA_TEXT = "ivanavaBus";
    private final String LOGISHIN_TEXT = "logishinBus";
    private final String DATE_LOAD = "dataLoad";
    private final String SET_REMIND = "setRemind";
    private Boolean cityVisible;
    private Boolean pinskVisible;
    private Boolean ivanavaVisible;
    private Boolean logishinVisible;
    private Boolean firstLoad;
    private Long dateLoad;
    private int remind;

    private boolean[] checketBus;

    private ModelNavigationView(AppCompatActivity activity) {
        sPref = activity.getPreferences(MODE_PRIVATE);
    }

    public static ModelNavigationView getInstance(AppCompatActivity activity) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ModelNavigationView(activity);
                    instance.getOptions();
                }
            }
        }
        return instance;
    }

    private void getOptions() {
        setCityVisible(Boolean.valueOf(sPref.getString(CITY_TEXT, "true")));
        setPinskVisible(Boolean.valueOf(sPref.getString(PINSK_TEXT, "true")));
        setIvanavaVisible(Boolean.valueOf(sPref.getString(IVANAVA_TEXT, "false")));
        setLogishinVisible(Boolean.valueOf(sPref.getString(LOGISHIN_TEXT, "false")));
        setRemind(Integer.valueOf(sPref.getString(SET_REMIND, "0")));
        setFirstLoad(Boolean.valueOf(sPref.getString(FIRST_LOAD, "true")));
        setDateLoad(Long.valueOf(sPref.getString(DATE_LOAD, "0")));
        checketBus = new boolean[]{getCityVisible(),getPinskVisible(),getIvanavaVisible(),getLogishinVisible()};
    }
    public void saveMemuProperty(boolean[] checket, int rem) {
        setChecketBus(checket);
        setRemind(rem);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(CITY_TEXT, Boolean.toString(checketBus[0]));
        ed.putString(PINSK_TEXT, Boolean.toString(checketBus[1]));
        ed.putString(IVANAVA_TEXT, Boolean.toString(checketBus[2]));
        ed.putString(LOGISHIN_TEXT, Boolean.toString(checketBus[3]));
        ed.putString(SET_REMIND, Integer.toString(getRemind()));
        ed.commit();
    }
    public void saveDateLoad (long date){
        setDateLoad(date);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(DATE_LOAD, Long.toString(date));
        ed.commit();
    }
    public void setNeedLoadDB (boolean need){
        setFirstLoad(need);
        SharedPreferences.Editor ed = sPref.edit();
        if(need)ed.putString(FIRST_LOAD, "true");
        else ed.putString(FIRST_LOAD, "false");
        ed.commit();
    }

    public boolean[] getChecketBus() {
        return checketBus;
    }

    public void setChecketBus(boolean[] checketBus) {
        this.checketBus = checketBus;
        setCityVisible(checketBus[0]);
        setPinskVisible(checketBus[1]);
        setLogishinVisible(checketBus[2]);
        setLogishinVisible(checketBus[3]);
    }
    public Boolean getCityVisible() {
        return cityVisible;
    }

    public void setCityVisible(Boolean cityVisible) {
        this.cityVisible = cityVisible;
    }

    public Boolean getPinskVisible() {
        return pinskVisible;
    }

    public void setPinskVisible(Boolean pinskVisible) {
        this.pinskVisible = pinskVisible;
    }

    public Boolean getIvanavaVisible() {
        return ivanavaVisible;
    }

    public void setIvanavaVisible(Boolean ivanavaVisible) {
        this.ivanavaVisible = ivanavaVisible;
    }

    public Boolean getLogishinVisible() {
        return logishinVisible;
    }

    public void setLogishinVisible(Boolean logishinVisible) {
        this.logishinVisible = logishinVisible;
    }

    public Boolean getFirstLoad() {
        return firstLoad;
    }

    public void setFirstLoad(Boolean firstLoad) {
        this.firstLoad = firstLoad;
    }

    public Long getDateLoad() {
        return dateLoad;
    }

    public void setDateLoad(Long dateLoad) {
        this.dateLoad = dateLoad;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

}
