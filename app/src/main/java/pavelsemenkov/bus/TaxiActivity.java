package pavelsemenkov.bus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pavelsemenkov.bus.fragment.TaxiFragment;

public class TaxiActivity extends AppCompatActivity{


    private String TaxiName;
    private ArrayList<ArrayList<String>> TaxiList;
    private String[][] TaxiListCity = {
            {"Альфа", "Velcom +375 (29) 696-90-50"},
            {"Аэлита", "Velcom +375 (29) 950-38-00", "МТС +375 (29) 521-80-88", "life +375 (25) 724-34-54"},
            {"Вояж", "Velcom +375 (29) 907-22-22", "Velcom +375 (29) 663-60-00", "Velcom +375 (44) 479-36-74"},
            {"Карат", "Velcom +375 (29) 135-9-135"},
            {"Мотор", "Белтелеком +375 (165) 35-43-64"},
            {"Престиж", "Velcom +375 (29) 951-22-22",  "MTS +375 (29) 851-22-22"},
            {"Тропа", "MTS +375 (29) 754-87-87"}
    }, TaxiListInt = {
            {"ЧТУП \"КрисантДорТранс\" Пинск - Минск - Пинск", "MTS +375 29 206-55-55", "Velcom +375 29 306-55-55", "Velcom +375 29 626-45-11", "MTS +375 29 826-45-11"},
            {"МАРШРУТКА Пинск - Минск - Пинск", "MTS +375 29 795-88-88", "Velcom +375 44 795-88-88"},
            {"Маршрутка \"Vminsk\" Пинск - Минск - Пинск", "MTS +375 29 527-66-38", "MTS +375 29 809-22-22", "Velcom +375 29 333-98-89", "Velcom +375 29 102-69-71", "Velcom +375 29 320-88-88 ", "Velcom +375 44 744-16-12"},
            {"minsk-pinsk.by Пинск - Минск - Пинск", "Velcom +375 44 536-44-44", "Velcom +375 29 328-66-66", "Velcom +375 44 721-99-99", "MTS +375 29 536-44-44", "MTS +375 33 328-66-66"},
            {"МАРШРУТКА Пинск - Минск - Пинск", "Velcom +375 44 560-70-60", "MTS +375 29 560-70-60", "MTS +375 33 600-32-33"},
            {"\"ЯгуарТурТранс\" Пинск - Минск - Пинск", "Velcom +375 44 5364444", "Velcom +375 29 3286666", "Velcom +375 44 7215555", "MTS +375 29 5364444", "MTS +375 29 3286666"},
            {"Маршрутка \"Таксичка.BY\" Пинск - Минск - Пинск", "Velcom +375 29 340-88-88", "Velcom/MTS +375 29 520-88-88", "Velcom/MTS +375 44 5804222", "Life +375 25 520-88-88", "Белтелеком +375 165 356666"}
    }, s;
    private Map<Integer, ArrayList<ArrayList<String>>> stopData = new HashMap<>();
    private int index;
    private FragmentTransaction fTrans;
    private Toolbar toolbarTaxi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);
        Intent intent = getIntent();
        TaxiName = intent.getStringExtra("title");
        index = intent.getIntExtra("index", 1);

        initToolbar();
        initTaxiView(getTaxi());

        Toast.makeText(getApplicationContext(), this.getString(R.string.massage), Toast.LENGTH_LONG).show();
    }

    private String [][] getTaxi() {
        if(index==1){
            s = TaxiListCity;
        }else {
            s = TaxiListInt;
        }
        return s;
    }

    private void initTaxiView(String [][] Data) {
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.activity_taxi_frame, TaxiFragment.getInstance(this, Data, toolbarTaxi));
        fTrans.commit();
    }

    private void initToolbar() {
        toolbarTaxi = (Toolbar) findViewById(R.id.taxi_toolbar);
        setSupportActionBar(toolbarTaxi);
        getSupportActionBar().setTitle(TaxiName);

    }

    View.OnClickListener oclBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int ind1, ind2, id = v.getId();
            ind1 = id/10;
            ind2 = id%10;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            String [] telephon = s[ind1][ind2].split("\\+");
            intent.setData(Uri.parse("tel:+"+telephon[1]));
            startActivity(intent);
        }
    };
}
