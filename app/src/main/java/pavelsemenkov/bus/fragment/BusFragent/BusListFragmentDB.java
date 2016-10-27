package pavelsemenkov.bus.fragment.BusFragent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pavelsemenkov.bus.Adapters.BusAdapter;
import pavelsemenkov.bus.R;
import pavelsemenkov.bus.StopActivity;
import pavelsemenkov.bus.database.BusProvider;
import pavelsemenkov.bus.database.BusTable;
import pavelsemenkov.bus.fragment.AbstractTabFragment;

public class BusListFragmentDB extends AbstractTabFragment implements LoaderManager.LoaderCallbacks,BusAdapter.BusListener {

    private Activity activity;
    private BusAdapter adapter;
    final String LOG_TAG = "myLogs";
    RecyclerView recyclerView;
    private final int LOADER_ID_UPDATE_SCHEDULE = 3;
    private final int LOADER_ID_GET_SCHEDULE = 4;


    public static BusListFragmentDB getInstance(Context context) {
        Bundle args = new Bundle();
        BusListFragmentDB fragment = new BusListFragmentDB();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab1));

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bus_fragment, container, false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID_GET_SCHEDULE, null, this);
    }

    @Override
    public Loader onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case LOADER_ID_UPDATE_SCHEDULE:
                //return new ScheduleLoader(getActivity());
            case LOADER_ID_GET_SCHEDULE:
                return new CursorLoader(getActivity(),
                        BusProvider.CONTENT_BUS_URI,
                        BusTable.BUS_PROJECTION,
                        null,
                        null,
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_UPDATE_SCHEDULE:
                break;
            case LOADER_ID_GET_SCHEDULE:
                Cursor cursor = (Cursor) data;

                    adapter = new BusAdapter(cursor, this);
                    recyclerView.setAdapter(adapter);
               /* } else {
                    adapter.swapCursor(cursor);
                    adapter.notifyDataSetChanged();
                }*/
                break;
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        if (adapter!=null) adapter.swapCursor(null);
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

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onBusClicked(long BusId, String title) {
        Intent intent = new Intent(activity, StopActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("id", Long.toString(BusId));
        startActivity(intent);
    }
}
