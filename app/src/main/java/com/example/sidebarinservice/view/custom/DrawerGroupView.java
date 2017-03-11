package com.example.sidebarinservice.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Kimcy929 on 21/02/2017.
 */
public class DrawerGroupView extends FrameLayout {

    private OnBackKeyListener backKeyListener;

    public static final String TAG = DrawerGroupView.class.getSimpleName();

    public DrawerGroupView(Context context) {
        super(context);
    }

    public DrawerGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawerGroupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Handle click out side of panel Layout to hide Drawer if we need
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "dispatchKeyEvent: back key");
            this.backKeyListener.onBackPressed();
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnBackKeyListener(DrawerGroupView.OnBackKeyListener backKeyListener) {
        this.backKeyListener = backKeyListener;
    }

    public interface OnBackKeyListener {
        void onBackPressed();
    }
}
