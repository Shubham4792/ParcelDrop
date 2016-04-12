package com.example.shubhampandey.parceldrop.activity;
import android.os.Bundle;

import com.example.shubhampandey.parceldrop.R;

public class ThanksActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showErrorNotification(getString(R.string.order_confirmed_msg),false);
    }

    @Override
    protected int getContentPageLayoutId() {
        return R.layout.activity_thanks;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }
}
