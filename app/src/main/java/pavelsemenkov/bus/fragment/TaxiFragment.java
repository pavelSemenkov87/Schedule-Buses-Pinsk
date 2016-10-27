package pavelsemenkov.bus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pavelsemenkov.bus.Adapters.TaxiAdapter;
import pavelsemenkov.bus.R;
import pavelsemenkov.bus.Utils.RecyclerViewUtils;

public class TaxiFragment extends AbstractTabFragment {
    private View v;
    private RecyclerViewUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;
    private String[][] TaxiList;
    private Toolbar toolbarTaxi;
    private RecyclerView.Adapter adapter;
    final String LOG_TAG = "myLogs";

    public void setParametrs(String[][] TaxiList, Toolbar toolbarTaxi) {
        this.TaxiList = TaxiList;
        this.toolbarTaxi = toolbarTaxi;
    }

    public static TaxiFragment getInstance(Context context, String[][] TaxiList, Toolbar toolbarStop) {
        Bundle args = new Bundle();
        TaxiFragment fragment = new TaxiFragment();
        fragment.setParametrs(TaxiList, toolbarStop);
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "StopFragment onCreateView");
        v = inflater.inflate(R.layout.taxi_fragment, container, false);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recycler_view_taxi);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TaxiAdapter(TaxiList, getActivity());
        rv.setAdapter(adapter);
        rv.addOnScrollListener(showHideToolbarListener = new RecyclerViewUtils.ShowHideToolbarOnScrollingListener(toolbarTaxi));

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
            adapter = new TaxiAdapter(TaxiList, getActivity());
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

}
