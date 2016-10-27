package pavelsemenkov.bus.views.MainActivty;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pavelsemenkov.bus.R;
import pavelsemenkov.bus.views.ViewMvc;

/**
 * Very simple MVC view containing just single FrameLayout
 */
public class MainActivityViewMvcImpl implements ViewMvc {

    private View mRootView;

    public MainActivityViewMvcImpl(Context context, ViewGroup container) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.activity_main, container);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        // This MVC view has no state that could be retrieved
        return null;
    }
}
