package pavelsemenkov.bus.fragment.StopFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pavelsemenkov.bus.Adapters.StopAdapter;
import pavelsemenkov.bus.R;
import pavelsemenkov.bus.Utils.RecyclerViewUtils;
import pavelsemenkov.bus.fragment.AbstractTabFragment;

public class StopFragmentDB extends AbstractTabFragment {
    private String pay;
    private View v;
    private RecyclerViewUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;
    private ArrayList<ArrayList<String>> stopList;
    private Toolbar toolbarStop;
    private int i;
    private Activity activity;
    private RecyclerView.Adapter adapter;
    final String LOG_TAG = "myLogs";

    private void setParametrs(ArrayList<ArrayList<String>> stopList, String pay, Toolbar toolbarStop) {
        this.stopList = stopList;
        this.pay = pay;
        this.toolbarStop = toolbarStop;
    }

    public static StopFragmentDB getInstance(Context context, ArrayList<ArrayList<String>> stopList, String pay, Toolbar toolbarStop) {
        Bundle args = new Bundle();
        StopFragmentDB fragment = new StopFragmentDB();
        fragment.setParametrs(stopList, pay, toolbarStop);
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab2));
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "StopFragment onCreateView");
        v = inflater.inflate(R.layout.stop_fragment, container, false);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StopAdapter(getStopList(stopList));
        rv.setAdapter(adapter);
        rv.addOnScrollListener(showHideToolbarListener = new RecyclerViewUtils.ShowHideToolbarOnScrollingListener(toolbarStop));

        if (savedInstanceState != null) {
            showHideToolbarListener.onRestoreInstanceState((RecyclerViewUtils.ShowHideToolbarOnScrollingListener.State) savedInstanceState
                    .getParcelable(RecyclerViewUtils.ShowHideToolbarOnScrollingListener.SHOW_HIDE_TOOLBAR_LISTENER_STATE));
        }

        busListEventListener.busListEvent();
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null){
            adapter = new StopAdapter(getStopList(stopList));
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RecyclerViewUtils.ShowHideToolbarOnScrollingListener.SHOW_HIDE_TOOLBAR_LISTENER_STATE,
                showHideToolbarListener.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(LOG_TAG, "StopFragment onAttach");
        try {
            if (context instanceof Activity) {
                activity = (Activity) context;
                busListEventListener = (busListEventListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    private ArrayList<ArrayList<String>>  getStopList(ArrayList<ArrayList<String>> stopList) {
        String[] time, rTime;
        int hS, mS;
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("k:mm");
        time = format.format(now).split(":");
        hS = Integer.parseInt(time[0]);
        mS = Integer.parseInt(time[1]);
        if (stopList==null)stopList = this.stopList;
        int size = stopList.size();
        for(i = 0; i < size; i++){
            Pattern n = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]", Pattern.MULTILINE);//оставим только время
            Matcher m = n.matcher(stopList.get(i).get(1));
            boolean print = false, setC = false;
            String temp = "";
            while (m.find()) {
                print = true;
                if (setC) {
                    temp = temp + " <font color='#1A237E'>" + m.group() + "</font>";
                } else {
                    rTime = m.group().split(":");
                    int hR = Integer.parseInt(rTime[0]);
                    int mR = Integer.parseInt(rTime[1]);
                    if (hR > hS) {
                        temp = temp + " <font color='#1A237E'> <b>" + m.group() + "</b></font>";
                        setC = true;
                    } else if (hR == hS) {
                        if (mR > mS) {
                            temp = temp + " <font color='#1A237E'> <b>" + m.group() + "</b></font>";
                            setC = true;
                        } else {
                            temp = temp + " <font color='#E57373'>" + m.group() + "</font>";
                        }
                    } else {
                        temp = temp + " <font color='#E57373'>" + m.group() + "</font>";
                    }
                }
            }
            if (print) {stopList.get(i).set(1, temp);
            }else{
                stopList.get(i).set(1, " ");
            }
        }
        return stopList;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public interface busListEventListener {
        void busListEvent();
    }

    busListEventListener busListEventListener;
}
