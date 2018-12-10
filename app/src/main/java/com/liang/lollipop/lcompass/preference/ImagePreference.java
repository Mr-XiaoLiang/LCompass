package com.liang.lollipop.lcompass.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.activity.ImagePreferenceSelectActivity;

/**
 * Created by Lollipop on 2017/08/22.
 * 图片选择的偏好设置
 */
public class ImagePreference extends Preference {

    private static final int IMAGE_WIDGET = R.layout.preference_image;
    private ImageView imageView;

    public ImagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWidgetLayoutResource(IMAGE_WIDGET);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ImagePreference, defStyleAttr, defStyleRes);
        float aspectRatioWidth = a.getFloat(R.styleable.ImagePreference_aspectRatioWidth,1);
        float aspectRatioHeight = a.getFloat(R.styleable.ImagePreference_aspectRatioHeight,1);
        int maxHeight = a.getInt(R.styleable.ImagePreference_maxHeight,512);
        int maxWidth = a.getInt(R.styleable.ImagePreference_maxWidth,512);
        boolean circle = a.getBoolean(R.styleable.ImagePreference_circle,false);
        boolean cropPhoto = a.getBoolean(R.styleable.ImagePreference_cropPhoto,false);

        Intent intent = new Intent(getContext(), ImagePreferenceSelectActivity.class);
        intent.putExtra(ImagePreferenceSelectActivity.ARG_ASPECT_RATIO_HEIGHT,aspectRatioHeight);
        intent.putExtra(ImagePreferenceSelectActivity.ARG_ASPECT_RATIO_WIDTH,aspectRatioWidth);
        intent.putExtra(ImagePreferenceSelectActivity.ARG_MAX_HEIGHT,maxHeight);
        intent.putExtra(ImagePreferenceSelectActivity.ARG_MAX_WIDTH,maxWidth);
        intent.putExtra(ImagePreferenceSelectActivity.ARG_CIRCLE,circle);
        intent.putExtra(ImagePreferenceSelectActivity.ARG_CROP_PHOTO,cropPhoto);
        intent.putExtra(ImagePreferenceSelectActivity.ARG_KEY,getKey());
        setIntent(intent);

        a.recycle();
    }

    public ImagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public ImagePreference(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImagePreference(Context context) {
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
        imageView = (ImageView) view.findViewById(R.id.preference_image_show);
        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if(getContext()==null)
            return;
        String path = getPersistedString("");
        Glide.with(getContext()).load(path).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
    }
}
