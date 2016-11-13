package pavelsemenkov.bus.fragment.InterсityFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pavelsemenkov.bus.IntStopActivity;
import pavelsemenkov.bus.R;
import pavelsemenkov.bus.database.BusDbHelper;
import pavelsemenkov.bus.database.BusTable;
import pavelsemenkov.bus.fragment.AbstractTabFragment;

public class IntBusListFragmentDB extends AbstractTabFragment {

    private BusDbHelper dbHelper;
    private ArrayList<ArrayList<String>> busList = new ArrayList<ArrayList<String>>();
    private Activity activity;
    private int city;
    final String LOG_TAG = "myLogs";

    public IntBusListFragmentDB (){ }

    public void setCity(int city) {
        this.city = city;
    }

    public static IntBusListFragmentDB getInstance(Context context, int city) {
        Bundle args = new Bundle();
        IntBusListFragmentDB fragment = new IntBusListFragmentDB();
        fragment.setCity(city);
        fragment.setArguments(args);
        fragment.setContext(context);
        if (city==0){
            fragment.setTitle(context.getString(R.string.tab2));
        }else if (city==1){
            fragment.setTitle(context.getString(R.string.tab3));
        }else if (city==2){
            fragment.setTitle(context.getString(R.string.tab4));
        }


        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intercity_bus, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.IntRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new BusAdapter(getBusDB()));
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof Activity) {
                activity = (Activity) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    private ArrayList<ArrayList<String>> getBusDB(){
        if(busList.size()!=0)return busList;
        String selection = null;
        String[] selectionArgs = null;
        dbHelper = new BusDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        selection = "city = ?";
        selectionArgs = new String[]{Integer.toString(city)};
        int i;
        Cursor c = db.query(BusTable.TABLE_INTERCITY_BUS_NAME, null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            i = 0;
            int idColIndex = c.getColumnIndex(BusTable.COLUMN_ID_INT_BUS);
            int routeColIndex = c.getColumnIndex(BusTable.COLUMN_INT_ROUTE_TYP);
            int indColIndex = c.getColumnIndex(BusTable.COLUMN_INT_TABLE_IND);
            int infoColIndex = c.getColumnIndex(BusTable.COLUMN_INT_INFO);
            do {
                busList.add(new ArrayList<String>());
                busList.get(i).add(0, c.getString(routeColIndex));
                busList.get(i).add(1, Integer.toString(c.getInt(idColIndex)));
                busList.get(i).add(2, Integer.toString(c.getInt(indColIndex)));
                busList.get(i).add(3, c.getString(infoColIndex));
                i++;
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "GetBus 0 rows");
        dbHelper.close();
        return busList;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusHolder> {
        ArrayList<ArrayList<String>> busList;
        public BusAdapter(ArrayList<ArrayList<String>> busList) {
            this.busList = busList;
        }

        @Override
        public BusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.int_bus_item, parent, false);
            return new BusHolder(view);
        }

        @Override
        public void onBindViewHolder(BusHolder holder, int position) {
            holder.BusRoute.setText(busList.get(position).get(0));
        }

        @Override
        public int getItemCount() {
            return busList == null ? 0 : busList.size();
        }

        public class BusHolder extends RecyclerView.ViewHolder{
            TextView BusRoute;
            public BusHolder(View itemView) {
                super(itemView);
                //cardView = (CardView) itemView.findViewById(R.id.bus_card);
                BusRoute = (TextView) itemView.findViewById(R.id.route_name);
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        ItemClick(pos);
                    }
                });
            }
        }
        public void ItemClick(int position) {
            Intent intent = new Intent(activity, IntStopActivity.class);
            intent.putExtra("title", busList.get(position).get(0));
            intent.putExtra("id", busList.get(position).get(1));
            intent.putExtra("index", busList.get(position).get(2));
            intent.putExtra("info", busList.get(position).get(3));
            startActivity(intent);
        }
    }

}
