package pavelsemenkov.bus.fragment.IntersitiFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pavelsemenkov.bus.Adapters.IntStopAdapter;
import pavelsemenkov.bus.R;
import pavelsemenkov.bus.Utils.RecyclerViewUtils;
import pavelsemenkov.bus.fragment.AbstractTabFragment;

public class IntStopFragmentDB extends AbstractTabFragment {
    private View v;
    private RecyclerViewUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;
    private ArrayList<ArrayList<String>> stopList;
    private Toolbar toolbarStop;
    private int  index;
    private RecyclerView.Adapter adapter;
    final String LOG_TAG = "myLogs";

    public void setParametrs (ArrayList<ArrayList<String>> stopList, int index, Toolbar toolbarStop){
        this.stopList = stopList;
        this.index = index;
        this.toolbarStop = toolbarStop;
    }

    public static IntStopFragmentDB getInstance(Context context, ArrayList<ArrayList<String>> stopList, int index, Toolbar toolbarStop) {
        Bundle args = new Bundle();
        IntStopFragmentDB fragment = new IntStopFragmentDB();
        fragment.setParametrs(stopList,index, toolbarStop);
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab2));
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "StopFragment onCreateView");
        v = inflater.inflate(R.layout.int_stop_fragment, container, false);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recycler_view_int_stop);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new IntStopAdapter(stopList, index);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(showHideToolbarListener = new RecyclerViewUtils.ShowHideToolbarOnScrollingListener(toolbarStop));

        if (savedInstanceState != null) {
            showHideToolbarListener.onRestoreInstanceState((RecyclerViewUtils.ShowHideToolbarOnScrollingListener.State) savedInstanceState
                    .getParcelable(RecyclerViewUtils.ShowHideToolbarOnScrollingListener.SHOW_HIDE_TOOLBAR_LISTENER_STATE));
        }

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null){
            adapter = new IntStopAdapter(stopList, index);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RecyclerViewUtils.ShowHideToolbarOnScrollingListener.SHOW_HIDE_TOOLBAR_LISTENER_STATE,
                showHideToolbarListener.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public interface busListEventListener {
        void busListEvent();
    }

    busListEventListener busListEventListener;
}
