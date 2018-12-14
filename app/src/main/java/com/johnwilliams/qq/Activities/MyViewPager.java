package com.johnwilliams.qq.Activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
    private boolean scrollable = false;

    public MyViewPager(Context context){
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean isScrollable){
        scrollable = isScrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        if (scrollable){
            return super.onTouchEvent(ev);
        }
        else{
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if (scrollable){
            return super.onInterceptTouchEvent(ev);
        }
        else{
            return false;
        }
    }
}
