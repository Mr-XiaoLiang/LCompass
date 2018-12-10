package com.liang.lollipop.lcompass.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.liang.lollipop.lcompass.R;

/**
 * Created by Lollipop on 2017/08/22.
 * 参数设置项的碎片
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private BaseAdapter rootAdapter;
    private OnSharedPreferenceChangedListener changedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        ListAdapter listAdapter = preferenceScreen.getRootAdapter();
        if(listAdapter instanceof BaseAdapter)
            rootAdapter = (BaseAdapter) listAdapter;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(rootAdapter!=null)
            rootAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(changedListener!=null)
            changedListener.onSharedPreferenceChanged(sharedPreferences,key);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSharedPreferenceChangedListener)
            this.changedListener = (OnSharedPreferenceChangedListener) context;
    }

    public interface OnSharedPreferenceChangedListener{
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);
    }

}
