package pavelsemenkov.bus.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pavelsemenkov.bus.R;

public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.StopHolder> {
    private String[][] TaxiList;
    private static Activity activity;
    private static int lenght, pos;

    public TaxiAdapter(String[][] TaxiList, Activity activity) {
        this.TaxiList = TaxiList;
        this.activity = activity;
        pos = 0;
    }

    @Override
    public StopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taxi_item, parent, false);
        lenght = TaxiList[pos].length;
        pos++;
        return new StopHolder(view);
    }

    @Override
    public void onBindViewHolder(StopHolder holder, int position) {
        ArrayList <TextView> TA = new ArrayList<>();
        int length = TaxiList[position].length;
        TA.add(holder.taxi_name);
        TA.add(holder.taxi_coll1);
        TA.add(holder.taxi_coll2);
        TA.add(holder.taxi_coll3);
        TA.add(holder.taxi_coll4);
        TA.add(holder.taxi_coll5);
        TA.add(holder.taxi_coll6);
        for (int i=0; i < length; i++){
            TA.get(i).setText(TaxiList[position][i]);
            TA.get(i).setPadding(7, 7, 7, 7);
            TA.get(i).setTextSize(16f);
            if(i==0)continue;
            TA.get(i).setId(position*10+i);
            TA.get(i).setTextColor(Color.BLACK);
            TA.get(i).setOnClickListener(oclBtn);
        }
    }

    @Override
    public int getItemCount() {
        return TaxiList == null ? 0 : TaxiList.length;
    }

    public static class StopHolder extends RecyclerView.ViewHolder{
        private long StopId;
        TextView taxi_name, taxi_coll1 = new TextView(activity), taxi_coll2 = new TextView(activity),
                taxi_coll3 = new TextView(activity), taxi_coll4 = new TextView(activity),
                taxi_coll5 = new TextView(activity), taxi_coll6 = new TextView(activity);
        LinearLayout taxi_layout;
        public void setStopId(long id) {
            StopId = id;
        }
        public StopHolder(View itemView) {
            super(itemView);
            taxi_name = (TextView) itemView.findViewById(R.id.taxi_name);
            taxi_layout = (LinearLayout) itemView.findViewById(R.id.taxi_layout);
            taxi_layout.addView(taxi_coll1);
            if (lenght==3){
                taxi_layout.addView(taxi_coll2);
            }else if (lenght==4){
                taxi_layout.addView(taxi_coll2);
                taxi_layout.addView(taxi_coll3);
            }else if (lenght==5){
                taxi_layout.addView(taxi_coll2);
                taxi_layout.addView(taxi_coll3);
                taxi_layout.addView(taxi_coll4);
            }else if (lenght==6){
                taxi_layout.addView(taxi_coll2);
                taxi_layout.addView(taxi_coll3);
                taxi_layout.addView(taxi_coll4);
                taxi_layout.addView(taxi_coll5);
            }else if (lenght==7){
                taxi_layout.addView(taxi_coll2);
                taxi_layout.addView(taxi_coll3);
                taxi_layout.addView(taxi_coll4);
                taxi_layout.addView(taxi_coll5);
                taxi_layout.addView(taxi_coll6);
            }
        }
    }
    View.OnClickListener oclBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int ind1, ind2, id = v.getId();
            ind1 = id/10;
            ind2 = id%10;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            String [] telephon = TaxiList[ind1][ind2].split("\\+");
            intent.setData(Uri.parse("tel:+"+telephon[1]));
            activity.startActivity(intent);
        }
    };
}
