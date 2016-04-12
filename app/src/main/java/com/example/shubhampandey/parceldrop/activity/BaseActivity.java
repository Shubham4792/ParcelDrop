package com.example.shubhampandey.parceldrop.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shubhampandey.parceldrop.R;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long NOTIFICATION_TIME = 3000;
    protected boolean mNotificationStatus;
    protected boolean mNotificationClosing;
    private FrameLayout mPageContentHolder;
    private FrameLayout mInAppNotification;
    private boolean isAlive;
    private ImageView mNotificationIcon;
    private TextView mNotificationMessage;
    protected Runnable mAppNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            hideNotification();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        LinearLayout mPageContent = (LinearLayout) findViewById(R.id.page_content);
        mPageContentHolder = (FrameLayout) findViewById(R.id.page_content_holder);
        View childView = getLayoutInflater().inflate(getContentPageLayoutId(), mPageContent, false);
        mPageContent.addView(childView, 0);
        initAppNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAlive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAlive = false;
    }

    public boolean isActivityForeground() {
        return isAlive;
    }

    public void hideNotification() {
        if (mNotificationStatus && !mNotificationClosing) {
            mNotificationClosing = true;
            Animation hidePopup = AnimationUtils.loadAnimation(this, R.anim.hide_notification);
            mInAppNotification.startAnimation(hidePopup);
            hidePopup.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mPageContentHolder.removeView(mInAppNotification);
                    mNotificationStatus = false;
                    mNotificationClosing = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }


    @LayoutRes
    protected abstract int getContentPageLayoutId();

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled("gps");
        boolean isNetworkEnabled = locationManager.isProviderEnabled("network");
        return isGPSEnabled || isNetworkEnabled;
    }


    protected void initAppNotification() {
        LayoutInflater inflater = getLayoutInflater();
        mInAppNotification = (FrameLayout) inflater.inflate(R.layout.in_app_notification, mPageContentHolder, false);
        mNotificationIcon = (ImageView) mInAppNotification.findViewById(R.id.notification_icon);
        mNotificationMessage = (TextView) mInAppNotification.findViewById(R.id.notification_message);
        mInAppNotification.setOnClickListener(this);
    }

    public void showErrorNotification(String message, boolean errorIconVisible) {
        mNotificationMessage.setText(message);
        if (errorIconVisible) {
            mNotificationIcon.setVisibility(View.VISIBLE);
        } else {
            mNotificationIcon.setVisibility(View.GONE);
        }
        if (!mNotificationStatus) {
            mNotificationStatus = true;
            mPageContentHolder.removeView(mInAppNotification);
            mPageContentHolder.addView(mInAppNotification, FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            Animation showPopup = AnimationUtils.loadAnimation(this, R.anim.show_notification);
            mInAppNotification.startAnimation(showPopup);
            showPopup.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startNotificationTimer();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private void startNotificationTimer() {
        mInAppNotification.removeCallbacks(mAppNotificationRunnable);
        mInAppNotification.postDelayed(mAppNotificationRunnable, NOTIFICATION_TIME);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager
                    = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(activity.getCurrentFocus(), InputMethodManager
                .SHOW_IMPLICIT);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            View listItem = listView.getAdapter().getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listView.getAdapter().getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void replaceFragment(Fragment fragment, String backStackTag) {
        if (TextUtils.isEmpty(backStackTag)) {
            getFragmentManager().beginTransaction()
                    .replace(getFragmentContainerId(), fragment)
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(getFragmentContainerId(), fragment)
                    .addToBackStack(backStackTag)
                    .commit();
        }
    }

    protected abstract int getFragmentContainerId();

    @Override
    public void onBackPressed() {
        hideKeyboard(BaseActivity.this);
        int backStackCount = getFragmentManager().getBackStackEntryCount();
        //if there are multiple fragments added to the activity, pop them first
        if (backStackCount > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.in_app_notification) {
            //Do nothing
        }
    }
}