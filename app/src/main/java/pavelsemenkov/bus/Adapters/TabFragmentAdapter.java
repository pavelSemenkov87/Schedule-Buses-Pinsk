package pavelsemenkov.bus.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

import pavelsemenkov.bus.fragment.AbstractTabFragment;
import pavelsemenkov.bus.fragment.BusFragent.BusListFragmentDB;
import pavelsemenkov.bus.fragment.InterсityFragment.IntBusListFragmentDB;

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs, setTabs;
    private Context context;
    private boolean[] basSet;
    private String busName = "", busHttp = "";

    public TabFragmentAdapter(Context context, FragmentManager fm, boolean[] basSet) {
        super(fm);
        this.context = context;
        this.basSet = basSet;
        initTabMap();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabMap() {
        tabs = new HashMap<>();
        int size = basSet.length, j=0;
        for (int i=0; i<size; i++){
            switch (i){
                case 0:
                    if (basSet[i]){
                        tabs.put(j, BusListFragmentDB.getInstance(context));
                        j++;
                    }
                    break;
                case 1:
                    if (basSet[i]){
                        tabs.put(j, IntBusListFragmentDB.getInstance(context, 0));
                        j++;
                    }
                    break;
                case 2:
                    if (basSet[i]){
                        tabs.put(j, IntBusListFragmentDB.getInstance(context, 1));
                        j++;
                    }
                    break;
                case 3:
                    if (basSet[i]){
                        tabs.put(j, IntBusListFragmentDB.getInstance(context, 2));
                        j++;
                    }
                    break;
            }
        }

    }
}
