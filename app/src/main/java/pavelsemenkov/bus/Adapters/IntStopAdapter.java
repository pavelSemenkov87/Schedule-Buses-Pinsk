package pavelsemenkov.bus.Adapters;

import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pavelsemenkov.bus.R;

public class IntStopAdapter extends RecyclerView.Adapter<IntStopAdapter.StopHolder> {
    private ArrayList<ArrayList<String>> stopList;
    private String pay = "", day;
    private int index;
    public IntStopAdapter(ArrayList<ArrayList<String>> stopList, int index) {
        this.stopList = stopList;
        this.index = index;
    }

    @Override
    public StopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = 0;
        if(index==4){
            layout = R.layout.item_4_intercity_stop;
        }else {
            layout = R.layout.item_2_intercity_stop;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new StopHolder(view, index);
    }

    @Override
    public void onBindViewHolder(StopHolder holder, int position) {
        if (index==4){
            holder.route.setText(stopList.get(position).get(8));
            holder.hed_tab1.setText(stopList.get(position).get(0));
            holder.time_tab1.setText(stopList.get(position).get(1));
            holder.hed_tab2.setText(stopList.get(position).get(2));
            holder.time_tab2.setText(stopList.get(position).get(3));
            holder.hed_tab3.setText(stopList.get(position).get(4));
            holder.time_tab3.setText(stopList.get(position).get(5));
            holder.hed_tab4.setText(stopList.get(position).get(6));
            holder.time_tab4.setText(stopList.get(position).get(7));
        }else {
            holder.route2.setText(stopList.get(position).get(4));
            holder.hed2_tab1.setText(stopList.get(position).get(0));
            holder.time2_tab1.setText(stopList.get(position).get(1));
            holder.hed2_tab2.setText(stopList.get(position).get(2));
            holder.time2_tab2.setText(stopList.get(position).get(3));
        }
        CardView.LayoutParams lp =  new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT>=21){
            if(position==0){
                lp.setMargins(0,48,0,4);//Добавляем отступ перед первым элементом
            }else if (stopList.size()-1==position){
                lp.setMargins(0,4,0,70);//добавляет отступ после последнего элемента чтобы можно было нажимать
            }else {
                lp.setMargins(0,4,0,4);
            }
        }else if (position==0){
            lp.setMargins(0,38,0,0);//Добавляем отступ перед первым элементом
        }
        holder.cardView.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return stopList == null ? 0 : stopList.size();
    }

    public static class StopHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView route, hed_tab1, hed_tab2, hed_tab3, hed_tab4, time_tab1, time_tab2, time_tab3, time_tab4,
                route2, hed2_tab1, hed2_tab2, time2_tab1, time2_tab2;
        public StopHolder(View itemView, int index) {
            super(itemView);
            if (index==4){
                route = (TextView) itemView.findViewById(R.id.route);
                hed_tab1 = (TextView) itemView.findViewById(R.id.hed_tab1);
                hed_tab2 = (TextView) itemView.findViewById(R.id.hed_tab2);
                hed_tab3 = (TextView) itemView.findViewById(R.id.hed_tab3);
                hed_tab4 = (TextView) itemView.findViewById(R.id.hed_tab4);
                time_tab1 = (TextView) itemView.findViewById(R.id.time_tab1);
                time_tab2 = (TextView) itemView.findViewById(R.id.time_tab2);
                time_tab3 = (TextView) itemView.findViewById(R.id.time_tab3);
                time_tab4 = (TextView) itemView.findViewById(R.id.time_tab4);
            }else {
                route2 = (TextView) itemView.findViewById(R.id.route2);
                hed2_tab1 = (TextView) itemView.findViewById(R.id.hed2_tab1);
                hed2_tab2 = (TextView) itemView.findViewById(R.id.hed2_tab2);
                time2_tab1 = (TextView) itemView.findViewById(R.id.time2_tab1);
                time2_tab2 = (TextView) itemView.findViewById(R.id.time2_tab2);
            }
            cardView = (CardView) itemView.findViewById(R.id.int_stop_card);
        }
    }
}
