package com.liang.lollipop.lcompass.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.dialog.ColorSelectDialog;
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable;

/**
 * Created by Lollipop on 2017/08/22.
 * 颜色选择的Preference
 */
public class ColorPreference extends DialogPreference implements ColorSelectDialog.OnColorSelectCallback{

    private static final int COLOR_WIDGET = R.layout.preference_color;
    private static final int REQUEST_SELECT_COLOR = 256;

    private CircleBgDrawable colorDrawable;
    private TextView valueView;
    private int defaultColor;

    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWidgetLayoutResource(COLOR_WIDGET);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ColorPreference, defStyleAttr, defStyleRes);
        defaultColor = a.getColor(R.styleable.ColorPreference_defaultColor,Color.GRAY);
        a.recycle();
    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorPreference(Context context) {
        this(context,null);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        //暂时没有找到原因，因为自定义的Preference的Title字体会很大，所以这里手动进行矫正
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        if(titleView!=null){
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.preference_color_show);
        imageView.setImageDrawable(colorDrawable = new CircleBgDrawable());
        valueView = (TextView) view.findViewById(R.id.preference_color_value);

        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        onColorChange();
    }

    @Override
    protected void onClick() {
//        super.onClick();
        ColorSelectDialog.getInstance(
                getContext(),
                getTitle(),
                getColor(),
                REQUEST_SELECT_COLOR,
                this);
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
    public void OnColorSelect(int request, int color) {
        persistInt(color);
        onColorChange();
    }

    private void onColorChange(){
        int color = getColor();
        colorDrawable.setColor(color);
        valueView.setText(getColorValue(color));
    }

    private int getColor(){
        return getPersistedInt(defaultColor);
    }

}
