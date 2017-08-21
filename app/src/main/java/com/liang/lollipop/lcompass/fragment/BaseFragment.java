package com.liang.lollipop.lcompass.fragment;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lsnackbar.LSnackBar;
import com.liang.lollipop.lsnackbar.LToastUtil;

/**
 * Created by Lollipop on 2017/08/15.
 * 基础的碎片
 */
public class BaseFragment extends Fragment implements View.OnClickListener {

    private View rootView = null;

    protected RequestManager glide;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findRootView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(glide==null){
            glide = Glide.with(this);
        }
    }

    private void findRootView(){
        //获取根节点View，用于弹出SnackBar
        rootView = getView();
    }

    protected void S(View view,String msg, String btnName, View.OnClickListener btnClick){
        if(view==null){
            LToastUtil.T(getContext(),msg,Color.WHITE,ContextCompat.getColor(getContext(),R.color.colorPrimary),R.mipmap.ic_launcher_round);
            if(btnClick!=null)
                btnClick.onClick(null);
            return;
        }
        int d;
        if(TextUtils.isEmpty(btnName)||btnClick==null){
            d = LSnackBar.LENGTH_SHORT;
        }else{
            d = LSnackBar.LENGTH_LONG;
        }
        LSnackBar.make(view,msg,d).setAction(btnName,btnClick)
                .setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .setMessageTextColor(Color.WHITE)
                .setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary))
                .setLogo(R.mipmap.ic_launcher_round).show();
    }

    protected void S(String msg, String btnName, View.OnClickListener btnClick){
        S(rootView,msg,btnName,btnClick);
    }

    protected void S(View view,String msg){
        S(view,msg, "", null);
    }

    protected void S(String msg){
        S(msg, "", null);
    }

    @Override
    public void onClick(View v) {

    }

    public void onLocationUpdate(Location location){

    }

    public void onSensorUpdate(float z,float x,float y){

    }

    public void onSensorStateUpdate(int i){

    }

    public void onTypeChange(boolean b){}
    public void onModelChange(boolean b){}
    public void on3DModelChange(boolean b){}
    public void onCameraModelChange(boolean b){}

    public void onCameraOpened(){}

    public void onCameraClosed(){}

    public void onPressureChange(float hPa,float altitude){}

}
