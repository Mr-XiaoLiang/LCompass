package com.liang.lollipop.lcompass.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.liang.lollipop.lcompass.R;

/**
 * 关于我们
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_about_toolbar);
        setSupportActionBar(toolbar);
    }

}
