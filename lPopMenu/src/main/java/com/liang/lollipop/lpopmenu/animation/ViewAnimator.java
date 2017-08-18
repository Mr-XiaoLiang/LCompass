package com.liang.lollipop.lpopmenu.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Lollipop on 2017/08/17.
 * View 的动画操纵者
 */
public class ViewAnimator implements ValueAnimator.AnimatorUpdateListener,ValueAnimator.AnimatorListener {
    //真正执行的动画
    private ValueAnimator valueAnimator;
    //操作的View
    private View targetView;

    private ArrayList<ValueAnimator.AnimatorUpdateListener> animatorUpdateListeners;
    private ArrayList<ValueAnimator.AnimatorListener> animatorListeners;

    public ViewAnimator(View targetView) {
        if(targetView==null)
            throw new RuntimeException("View is Null");
        this.targetView = targetView;
        valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.addUpdateListener(this);
        valueAnimator.addListener(this);
    }

    public static ViewAnimator copyOf(ViewAnimator animator){
        ViewAnimator viewAnimator = new ViewAnimator(animator.getTargetView());
        viewAnimator.setDuration(animator.getDuration());
        return viewAnimator;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }

    public View getTargetView() {
        return targetView;
    }

    public ViewAnimator setDuration(long duration){
        valueAnimator.setDuration(duration);
        return this;
    }

    public long getDuration(){
        return valueAnimator.getDuration();
    }

    public void addUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
        if (animatorUpdateListeners == null) {
            animatorUpdateListeners = new ArrayList<>();
        }
        animatorUpdateListeners.add(listener);
    }

    public void addListener(ValueAnimator.AnimatorListener listener) {
        if (animatorListeners == null) {
            animatorListeners = new ArrayList<>();
        }
        animatorListeners.add(listener);
    }

}
