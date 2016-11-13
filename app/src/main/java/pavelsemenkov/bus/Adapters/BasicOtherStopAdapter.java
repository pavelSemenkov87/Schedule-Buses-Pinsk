package pavelsemenkov.bus.Adapters;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import pavelsemenkov.bus.R;
import pavelsemenkov.bus.database.BusDbHelper;
import pavelsemenkov.bus.database.BusTable;
import pavelsemenkov.bus.model.BasicStop;
import pavelsemenkov.bus.model.myEditText;

public class BasicOtherStopAdapter extends RecyclerView.Adapter<BasicOtherStopAdapter.StopHolder> {
    private static AppCompatActivity activity;
    private Map<Integer, BasicStop> BasicStop;

    public interface OtherStopListener {
        void onBusClicked(int headlineId, String num);
    }

    private OtherStopListener mCallback;

    public BasicOtherStopAdapter(AppCompatActivity context, Map<Integer, BasicStop> BasicStop, OtherStopListener callback) {
        this.BasicStop = BasicStop;
        activity = context;
        mCallback = callback;
    }

    @Override
    public StopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_stop_item, parent, false);
        return new StopHolder(view);
    }

    @Override
    public void onBindViewHolder(StopHolder holder, int position) {
        holder.EditTexts = new ArrayList<>();
        holder.TextViews = new ArrayList<>();
        holder.Layouts = new ArrayList<>();
        holder.setStopId(position);
        String rootName = BasicStop.get(position).getBasicStopName();
        int j = 0;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, (float) 1.0
        );
        int lenght = BasicStop.get(position).getNextStopSet().size();
        holder.main_layout.removeAllViews();
        for (int i = 0; i < lenght; i++) {
            holder.Layouts.add(new ArrayList<LinearLayout>());
            holder.TextViews.add(new TextView(new ContextThemeWrapper(activity, R.style.TextViewChild)));
            holder.EditTexts.add(new myEditText(new ContextThemeWrapper(activity, R.style.TextViewChild)));
            holder.Layouts.get(i).add(new LinearLayout(new ContextThemeWrapper(activity, R.style.LinearLayoutRootStyle)));
            if (i == lenght - 1) {
                holder.Layouts.get(i).add(new LinearLayout(new ContextThemeWrapper(activity, R.style.LinearLayoutChildLeftBut)));
                holder.Layouts.get(i).get(1).setLayoutParams(params);
                holder.Layouts.get(i).add(new LinearLayout(new ContextThemeWrapper(activity, R.style.LinearLayoutChildRightBut)));
                holder.Layouts.get(i).get(2).setLayoutParams(params);
            } else {
                holder.Layouts.get(i).add(new LinearLayout(new ContextThemeWrapper(activity, R.style.LinearLayoutChildCentr)));
                holder.Layouts.get(i).get(1).setLayoutParams(params);
                holder.Layouts.get(i).add(new LinearLayout(new ContextThemeWrapper(activity, R.style.LinearLayoutChildCentr)));
                holder.Layouts.get(i).get(2).setLayoutParams(params);
            }
            holder.Layouts.get(i).get(1).addView(holder.TextViews.get(i));
            holder.Layouts.get(i).get(2).addView(holder.EditTexts.get(i));
            holder.Layouts.get(i).get(0).addView(holder.Layouts.get(i).get(1));
            holder.Layouts.get(i).get(0).addView(holder.Layouts.get(i).get(2));
            holder.main_layout.addView(holder.Layouts.get(i).get(0));
        }
        holder.root_stop_name.setText(rootName);
        for (Map.Entry<String, ArrayList<String>> rec : BasicStop.get(position).getNextStopSet().entrySet()) {
            holder.TextViews.get(j).setText(rec.getKey());
            holder.EditTexts.get(j).setText(rec.getValue().get(0));
            holder.EditTexts.get(j).setMyEditTextId(j);
            j++;
        }
        CardView.LayoutParams lp = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= 21) {
            if (BasicStop.size() - 1 == position) {
                lp.setMargins(0, 4, 0, 70);//добавляет отступ после последнего элемента чтобы можно было нажимать
            } else if (position == 0) {
                lp.setMargins(0, 48, 0, 4);//Добавляем отступ перед первым элементом
            } else {
                lp.setMargins(0, 4, 0, 4);
            }
        } else if (position == 0) {
            lp.setMargins(0, 38, 0, 0);//Добавляем отступ перед первым элементом
        }
        holder.cardView.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return BasicStop == null ? 0 : BasicStop.size();
    }

    public class StopHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView root_stop_name;
        LinearLayout main_layout;
        ArrayList<ArrayList<LinearLayout>> Layouts;
        ArrayList<myEditText> EditTexts;
        ArrayList<TextView> TextViews;

        private int StopId;
        private String NumBus;

        public void setStopId(int id) {
            StopId = id;
        }

        public void setEditId(String numBus) {
            NumBus = numBus;
        }

        public StopHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.other_stop_card);
            root_stop_name = (TextView) itemView.findViewById(R.id.root_stop_name);
            main_layout = (LinearLayout) itemView.findViewById(R.id.MainLinearLayout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusDbHelper dbHelper = new BusDbHelper(activity);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    boolean toastBool = false;
                    ContentValues values = new ContentValues();
                    double[] coodrinats;
                    for (int i = 0; i < EditTexts.size(); i++) {
                        coodrinats = getCoordinats(EditTexts.get(i).getText().toString());
                        if(coodrinats[0]!=0d&&coodrinats[1]!=0d){
                            String newCoord = Double.toString(coodrinats[0])+","+Double.toString(coodrinats[1]),
                                    Coord = BasicStop.get(StopId).getNextStopSet().get(TextViews.get(i).getText().toString()).get(0);
                            if(!Coord.equals(newCoord)){
                                BasicStop.get(StopId).setNextStopCoord(TextViews.get(i).getText().toString(), EditTexts.get(i).getText().toString());
                                values.put(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LAT, coodrinats[0]);
                                values.put(BusTable.COLUMN_BASIC_ROOT_STOP_TARGET_LNG, coodrinats[1]);
                                db.update(BusTable.TABLE_BASIC_OTHER_STOP_NAME, values, BusTable.COLUMN_BASIC_NEXT_STOP_ID + " = ?", new String[]{newCoord});
                                values.clear();
                                toastBool = true;
                            }
                        }
                    }
                    dbHelper.close();
                    if(toastBool){
                        Toast toast = Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.otherStopChanges), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 150);
                        toast.show();
                    }
                    //mCallback.onBusClicked(StopId, NumBus);
                }
            });
        }

        private double[] getCoordinats(String coodrinatsString) {
            double[] coodrinats = new double[]{0d, 0d};
            double Lat, Lng;
            coodrinatsString = coodrinatsString.replaceAll("\\s", "");//уберем пробелы
            String[] coordStr = coodrinatsString.split(",");
            if (coordStr.length != 2) return coodrinats;
            else if (isNumeric(coordStr[0]) && isNumeric(coordStr[1])) {
                Lat = Double.parseDouble(coordStr[0]);
                Lng = Double.parseDouble(coordStr[1]);
                if(isValidLatLng(Lat, Lng)){
                    coodrinats[0] = Lat;
                    coodrinats[1] = Lng;
                }else {
                    return coodrinats;
                }
            } else return coodrinats;
            return coodrinats;
        }

        public boolean isValidLatLng(double lat, double lng) {
            if (lat < -90 || lat > 90) {
                return false;
            } else if (lng < -180 || lng > 180) {
                return false;
            }
            return true;
        }

        private boolean isNumeric(String str) {
            return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
        }
    }
}
