package com.example.sidebarinservice.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.sidebarinservice.utils.LogUtils;

/**
 * Created by Kimcy929 on 22/02/2017.
 */
public class MyRecyclerView extends RecyclerView {

    private RecyclerViewTouchListener recyclerViewTouchListener;

    public void setRecyclerViewTouchListener(RecyclerViewTouchListener recyclerViewTouchListener) {
        this.recyclerViewTouchListener = recyclerViewTouchListener;
    }

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // always first call
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtils.d("dispatchTouchEvent in RecyclerView");
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                this.recyclerViewTouchListener.onTouchEvent(true);
                LogUtils.d("ACTION_DOWN");
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtils.d("ACTION_MOVE");
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (action == MotionEvent.ACTION_UP) {
                    LogUtils.d("ACTION_UP");
                } else {
                    LogUtils.d("ACTION_CANCEL");
                }
                this.recyclerViewTouchListener.onTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev); // return true if you want not dispatch further
    }

    // second
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }

    // third
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //return false; // as well as RecyclerView will can't work. It stops but when having action move then button click not working
        //return true; // as well as setOnTouch consumed onTouch return true => can't event click view
        return super.onTouchEvent(e);
    }

    public interface RecyclerViewTouchListener {
        void onTouchEvent(boolean isLock);
    }
}
