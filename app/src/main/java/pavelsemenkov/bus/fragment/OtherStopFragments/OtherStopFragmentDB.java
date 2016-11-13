package pavelsemenkov.bus.fragment.OtherStopFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import pavelsemenkov.bus.Adapters.BasicOtherStopAdapter;
import pavelsemenkov.bus.R;
import pavelsemenkov.bus.Utils.RecyclerViewUtils;
import pavelsemenkov.bus.fragment.AbstractTabFragment;
import pavelsemenkov.bus.model.BasicStop;

public class OtherStopFragmentDB extends AbstractTabFragment implements BasicOtherStopAdapter.OtherStopListener {
    private String pay;
    private View v;
    private RecyclerViewUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;
    private Map<Integer, BasicStop> BasicStop;
    private Toolbar toolbarStop;
    private RecyclerView.Adapter adapter;
    final String LOG_TAG = "myLogs";

    private void setParametrs(Map<String, BasicStop> BasicStop, Toolbar toolbarStop) {
        this.BasicStop = getBasicStop(BasicStop);
        this.toolbarStop = toolbarStop;
    }

    private Map<Integer, BasicStop> getBasicStop(Map<String, BasicStop> BasicStopString){
        Map<Integer, BasicStop> BasicStop = new HashMap<>();
        int i = 0;
        for (Map.Entry<String, BasicStop> rec : BasicStopString.entrySet()) {
            BasicStop.put(i++, rec.getValue());
        }
        return BasicStop;
    }

    public static OtherStopFragmentDB getInstance(Context context, Map<String, BasicStop> BasicStop, Toolbar toolbarStop) {
        Bundle args = new Bundle();
        OtherStopFragmentDB fragment = new OtherStopFragmentDB();
        fragment.setParametrs(BasicStop, toolbarStop);
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OtherStopFragmentDB onCreateView");
        v = inflater.inflate(R.layout.stop_fragment, container, false);

        busListEventListener = (busListEventListener) getActivity();
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BasicOtherStopAdapter((AppCompatActivity) getActivity(), BasicStop, this);
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
            adapter = new BasicOtherStopAdapter((AppCompatActivity) context, BasicStop, this);
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

    @Override
    public void onBusClicked(int headlineId, String num) {

    }

    public interface busListEventListener {
        void busListEvent();
    }

    busListEventListener busListEventListener;
}
