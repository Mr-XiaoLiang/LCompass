package com.liang.lollipop.lcompass.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.liang.lollipop.lcompass.R;

/**
 * 主页
 * @author Lollipop
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
    }

}
