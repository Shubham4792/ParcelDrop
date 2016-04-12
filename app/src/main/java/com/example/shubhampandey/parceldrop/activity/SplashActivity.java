package com.example.shubhampandey.parceldrop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.shubhampandey.parceldrop.R;

public class SplashActivity extends Activity {
    private static int SPLASH_TIME_OUT = 2000;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv = (TextView) findViewById(R.id.fullscreen_content);
        final Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.zoom);
        tv.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashActivity.this, AddressActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
