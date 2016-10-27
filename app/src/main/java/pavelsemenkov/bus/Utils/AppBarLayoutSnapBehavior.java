package pavelsemenkov.bus.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class AppBarLayoutSnapBehavior extends AppBarLayout.Behavior {

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private boolean mNestedScrollStarted = false;
    private static final float TOOLBAR_ELEVATION = 14f;
    private TabLayout tabLayout;
    private boolean hide = true;
    int appBarLayoutHeight;

    public AppBarLayoutSnapBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        mNestedScrollStarted = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);

        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);

        int ofsetBottom = child.getBottom();
        appBarLayoutHeight = child.getHeight();
        int topAndBottomOffset = getTopAndBottomOffset();
        toolbar = (Toolbar) child.getChildAt(0);
        tabLayout = (TabLayout) child.getChildAt(1);
        appBarLayout = child;
        ViewPager viewPager = (ViewPager) coordinatorLayout.getChildAt(1);
        int toolbarHight = toolbar.getHeight();
        if (ofsetBottom <= 136) {
            //AnimateHide();
            if(hide){
                setMarginTopTabLayout(38);
            }
            hide = false;
            doS();
        } else {
            //AnimateShow();
            setMarginTopTabLayout(0);
            hide = true;
            doS();
        }
    }

    private void AnimateShow() {
        /*appBarLayout.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);*/
        setMarginTopAppBarLayout(0);
        //setMarginTopTabLayout(0);
    }

    private void AnimateHide() {
        /*appBarLayout.animate()
                .translationY(-appBarLayoutHeight)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);*/
        setMarginTopAppBarLayout(-192);
        setMarginTopTabLayout(24);
    }
    public void setMarginTopTabLayout(int topMargin){
        final int newTopMargin = topMargin;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) tabLayout.getLayoutParams();
                lp.topMargin = (int)(newTopMargin * interpolatedTime);
                tabLayout.setLayoutParams(lp);
            }
        };
        a.setInterpolator(new LinearInterpolator());
        a.setDuration(280);
        tabLayout.startAnimation(a);
    }
    public void setMarginTopAppBarLayout(int topMargin){
        final int newTopMargin = topMargin;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) appBarLayout.getLayoutParams();
                lp.topMargin = (int)(newTopMargin * interpolatedTime);
                appBarLayout.setLayoutParams(lp);
            }
        };
        a.setInterpolator(new LinearInterpolator());
        a.setDuration(180);
        appBarLayout.startAnimation(a);
    }
    void doS(){}
}
