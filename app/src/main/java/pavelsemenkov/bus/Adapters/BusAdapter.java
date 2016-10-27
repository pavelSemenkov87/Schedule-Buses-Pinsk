package pavelsemenkov.bus.Adapters;

import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pavelsemenkov.bus.R;
import pavelsemenkov.bus.database.BusTable;

public class BusAdapter extends CursorRecyclerAdapter {

    public interface BusListener {
        void onBusClicked(long headlineId, String num);
    }

    private BusListener mCallback;

    public BusAdapter(Cursor cursor, BusListener callback) {
        super(cursor);
        mCallback = callback;
    }

    @Override
    public BusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item, parent, false);
        return new BusHolder(view);
    }

    @Override
    public void onBindViewHolderCursor(RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof BusHolder) {}
        BusHolder busHolder = (BusHolder) holder;
        long id  = cursor.getInt(cursor.getColumnIndex(BusTable.COLUMN_ID_BUS));
        String numBus = cursor.getString(cursor.getColumnIndex(BusTable.COLUMN_BAS_NUMBER));
        busHolder.setBusId(id);
        busHolder.setBusNum(numBus);
        busHolder.BusNum.setText(numBus);
        busHolder.BusName.setText(cursor.getString(cursor.getColumnIndex(BusTable.COLUMN_BAS_TEXT)));
        if (Build.VERSION.SDK_INT >= 21) {
            CardView.LayoutParams lp = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
            if (cursor.isLast()) {
                lp.setMargins(0, 4, 0, 70);//добавляет отступ после последнего элемента чтобы можно было нажимать
            } else {
                lp.setMargins(0, 4, 0, 4);
            }
            busHolder.cardView.setLayoutParams(lp);
        }
    }

    public class BusHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView BusName, BusNum;

        private long BusId;
        private String NumBus;

        public BusHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.bus_card);
            BusNum = (TextView) itemView.findViewById(R.id.bus_num);
            BusName = (TextView) itemView.findViewById(R.id.bus_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onBusClicked(BusId, NumBus);
                }
            });
        }
        public void setBusId(long id) {
            BusId = id;
        }
        public void setBusNum(String numBus) {
            NumBus = numBus;
        }
    }
}