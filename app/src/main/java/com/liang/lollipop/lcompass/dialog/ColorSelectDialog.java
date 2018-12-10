package com.liang.lollipop.lcompass.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable;

/**
 * Created by Lollipop on 2017/08/21.
 * 颜色选择器的对话框
 */
public class ColorSelectDialog extends AlertDialog implements
        SeekBar.OnSeekBarChangeListener,DialogInterface.OnClickListener {

    private static final int LAYOUT_ID = R.layout.dialog_color_select;

    private SeekBar colorAlphaBar;
    private SeekBar colorRedBar;
    private SeekBar colorGreenBar;
    private SeekBar colorBlueBar;

    private TextView colorText;
    private CircleBgDrawable colorDrawable;

    private int color = 0;

    private int requestCode = 0;

    private OnColorSelectCallback colorSelectCallback;

    protected ColorSelectDialog(@NonNull Context context) {
        super(context);
    }

    public static ColorSelectDialog getInstance(Context context,String title,int color,int request,OnColorSelectCallback callback){
        ColorSelectDialog dialog = new ColorSelectDialog(context);
        dialog.setTitle(title);
        dialog.setView(LayoutInflater.from(context).inflate(LAYOUT_ID,null,false));
        dialog.setColor(color);
        dialog.setColorSelectCallback(request,callback);
        dialog.setButton(BUTTON_POSITIVE,context.getString(R.string.dialog_btn_positive),dialog);
        dialog.setButton(BUTTON_NEGATIVE,context.getString(R.string.dialog_btn_negative),dialog);
        dialog.show();
        return dialog;
    }

    public static ColorSelectDialog getInstance(Context context,CharSequence title,int color,int request,OnColorSelectCallback callback){
        ColorSelectDialog dialog = new ColorSelectDialog(context);
        dialog.setTitle(title);
        dialog.setView(LayoutInflater.from(context).inflate(LAYOUT_ID,null,false));
        dialog.setColor(color);
        dialog.setColorSelectCallback(request,callback);
        dialog.setButton(BUTTON_POSITIVE,context.getString(R.string.dialog_btn_positive),dialog);
        dialog.setButton(BUTTON_NEGATIVE,context.getString(R.string.dialog_btn_negative),dialog);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorAlphaBar = (SeekBar) findViewById(R.id.dialog_color_select_a);
        colorRedBar = (SeekBar) findViewById(R.id.dialog_color_select_r);
        colorGreenBar = (SeekBar) findViewById(R.id.dialog_color_select_g);
        colorBlueBar = (SeekBar) findViewById(R.id.dialog_color_select_b);
        colorText = (TextView) findViewById(R.id.dialog_color_select_value);
        ImageView showView = (ImageView) findViewById(R.id.dialog_color_select_show);
        showView.setImageDrawable(colorDrawable = new CircleBgDrawable());

        colorAlphaBar.setOnSeekBarChangeListener(this);
        colorRedBar.setOnSeekBarChangeListener(this);
        colorGreenBar.setOnSeekBarChangeListener(this);
        colorBlueBar.setOnSeekBarChangeListener(this);

        onColorChange(color);
        initColorBar();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(!fromUser)
            return;
        switch (seekBar.getId()){
            case R.id.dialog_color_select_a:
            case R.id.dialog_color_select_r:
            case R.id.dialog_color_select_g:
            case R.id.dialog_color_select_b:
                onColorChange();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void onColorChange(){
        color = Color.argb(
                colorAlphaBar.getProgress(),
                colorRedBar.getProgress(),
                colorGreenBar.getProgress(),
                colorBlueBar.getProgress());
        onColorChange(color);
    }

    public void setColor(int c) {
        this.color = c;
        onColorChange(color);
        initColorBar();
    }

    private void onColorChange(int color){
        if(colorAlphaBar==null)
            return;
        colorDrawable.setColor(color);
        colorText.setText(getColorValue(color));
    }

    private void initColorBar(){
        if(colorAlphaBar==null||
                colorRedBar==null||
                colorGreenBar==null||
                colorBlueBar==null)
            return;
        int alpha = Color.alpha(color);
        colorAlphaBar.setProgress(alpha);
        int red = Color.red(color);
        colorRedBar.setProgress(red);
        int green = Color.green(color);
        colorGreenBar.setProgress(green);
        int blue = Color.blue(color);
        colorBlueBar.setProgress(blue);
    }

    private String getColorValue(int color){
        if(color==0){
            return "#00000000";
        }
        if(Color.alpha(color)==0){
            return "#00"+Integer.toHexString(color).toUpperCase();
        }
        if(Color.alpha(color)<0x10){
            return "#0"+Integer.toHexString(color).toUpperCase();
        }
        return "#"+Integer.toHexString(color).toUpperCase();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(colorSelectCallback==null)
            return;
        switch (which){
            case BUTTON_POSITIVE:
                colorSelectCallback.OnColorSelect(requestCode,color);
                break;
        }
    }

    public interface OnColorSelectCallback{
        void OnColorSelect(int request,int color);
    }

    public void setColorSelectCallback(int requestCode,OnColorSelectCallback colorSelectCallback) {
        this.colorSelectCallback = colorSelectCallback;
        this.requestCode = requestCode;
    }
}
