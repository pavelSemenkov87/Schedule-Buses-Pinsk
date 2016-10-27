package pavelsemenkov.bus.Adapters;

import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pavelsemenkov.bus.R;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopHolder> {
    ArrayList<ArrayList<String>> stopList;
    private String pay = "", day;
    public StopAdapter(ArrayList<ArrayList<String>> stopList) {
        this.stopList = stopList;
        this.pay = stopList.get(0).get(4) == null ? "" : " "+stopList.get(0).get(4);
        this.day = stopList.get(0).get(3);
    }

    @Override
    public StopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_item, parent, false);
        return new StopHolder(view);
    }

    @Override
    public void onBindViewHolder(StopHolder holder, int position) {
        holder.stopN.setText(stopList.get(position).get(0)+" ("+day+")"+pay);
        holder.stopT.setText(Html.fromHtml(stopList.get(position).get(1)));
        CardView.LayoutParams lp =  new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT>=21){
            if(stopList.size()-1==position){
                lp.setMargins(0,4,0,70);//добавляет отступ после последнего элемента чтобы можно было нажимать
            }else if (position==0){
                lp.setMargins(0,48,0,4);//Добавляем отступ перед первым элементом
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
        TextView stopN, stopT;
        public StopHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.stop_card);
            stopN = (TextView) itemView.findViewById(R.id.stop_name);
            stopT = (TextView) itemView.findViewById(R.id.stop_time);
        }
    }
}
