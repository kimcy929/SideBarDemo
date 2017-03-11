package com.example.sidebarinservice.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.sidebarinservice.R;
import com.example.sidebarinservice.utils.Utils;
import com.example.sidebarinservice.adapter.NumberAdapter;
import com.example.sidebarinservice.utils.LogUtils;
import com.example.sidebarinservice.view.custom.DrawerGroupView;
import com.example.sidebarinservice.view.custom.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SideBarService extends Service implements
        DrawerGroupView.OnBackKeyListener, MyRecyclerView.RecyclerViewTouchListener {

    public static final String TAG = SideBarService.class.getSimpleName();

    private WindowManager windowManager;

    private FrameLayout triggerView;

    //For drawer sidebar
    private DrawerGroupView drawerGroupView;
    private DrawerLayout drawerLayout;
    private FrameLayout panelLayout;

    private MyRecyclerView recyclerView;
    private ListView listView;

    private float slideOffset;

    private boolean isSwipeFromRight = true;

    public static SideBarService sideBarService;

    public SideBarService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sideBarService = this;

        isSwipeFromRight = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getBoolean("swipe_orientation", true);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //getWindowDisplaySize();

        initTriggerView();
        initSideBar();

        createNotification();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        if (drawerGroupView != null) {
            windowManager.removeView(drawerGroupView);
        }

        if (triggerView != null) {
            windowManager.removeView(triggerView);
        }

        sideBarService = null;

        stopForeground(true);

        super.onDestroy();
    }

    private Point point;

    private void getWindowDisplaySize() {
        point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
    }

    private void initTriggerView() {
        triggerView = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.trigger_layout, null);
        triggerView.setOnTouchListener(new View.OnTouchListener() {
            private ViewConfiguration vc = ViewConfiguration.get(getApplicationContext());

            public boolean onTouch(View v, MotionEvent event) {
                openDrawer(event);
                return true;
            }
        });

        windowManager.addView(triggerView, getTriggerViewLayoutParams());
    }


    private void initSideBar() {
        drawerGroupView = (DrawerGroupView) LayoutInflater.from(this).inflate(R.layout.sidebar_layout, null);

        drawerLayout = (DrawerLayout) drawerGroupView.findViewById(R.id.drawerLayout);
        panelLayout = (FrameLayout) drawerGroupView.findViewById(R.id.panelLayout);
        recyclerView = (MyRecyclerView) drawerGroupView.findViewById(R.id.recyclerView);

        drawerGroupView.setOnBackKeyListener(this); // Handle when user press back key
        drawerGroupView.setVisibility(View.GONE);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Log.d(TAG, "onDrawerSlide: -> " + slideOffset);
                SideBarService.this.slideOffset = slideOffset;
                if (slideOffset == 0.0f &&
                        !drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    onDrawerClosed(drawerView);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.d(TAG, "onDrawerClosed: ");
                drawerGroupView.setVisibility(View.GONE);
            }

        });

        configPanelLayout();

        configRecyclerView();

        configListView();


        windowManager.addView(drawerGroupView, getDrawerViewParams());
    }

    private void configPanelLayout() {
        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(
                DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);

        if (!isSwipeFromRight) {
            params.gravity = Gravity.START;
        } else {
            params.gravity = Gravity.END;
        }
        panelLayout.setLayoutParams(params);
    }

    private void configListView() {
        listView = (ListView) drawerGroupView.findViewById(R.id.listView);

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 50; ++i) {
            stringList.add("Item " + i);
        }
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item, stringList));
    }

    private void configRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setRecyclerViewTouchListener(this);
        NumberAdapter adapter = new NumberAdapter();
        recyclerView.setAdapter(adapter);
    }

    private WindowManager.LayoutParams getDrawerViewParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        layoutParams.format = PixelFormat.TRANSLUCENT;

        layoutParams.dimAmount = 0.2f;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.TOP | Gravity.START;

        return layoutParams;
    }

    private WindowManager.LayoutParams getTriggerViewLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.width = (int) Utils.convertDpToPixel(8.0f, getApplicationContext());
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

        layoutParams.format = PixelFormat.TRANSLUCENT;

        layoutParams.dimAmount = 0f;

        if (isSwipeFromRight) {
            layoutParams.gravity = Gravity.TOP | Gravity.END;
        } else {
            layoutParams.gravity = Gravity.TOP | Gravity.START;
        }

        return layoutParams;
    }

    private void openDrawer(final MotionEvent event) {
        int action = event.getAction() & MotionEventCompat.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            if (drawerGroupView.getVisibility() == View.GONE) {
                drawerGroupView.setVisibility(View.VISIBLE);
            }
        } else if ((action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL)
                && this.slideOffset == 0.0f) {
            drawerGroupView.setVisibility(View.GONE);
        }

        // Simulator touch event for DrawerLayout
        drawerLayout.onTouchEvent(MotionEvent.obtain(event.getDownTime(),
                event.getEventTime(),
                event.getAction(),
                isSwipeFromRight ? drawerLayout.getWidth() + event.getX() : event.getX(), // for right -> drawerLayout.getWidth() + event.getX()
                event.getY(),
                event.getMetaState()));

    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawers();
    }

    @Override
    public void onTouchEvent(boolean isLock) {
        if (isLock) {
            LogUtils.d("Lock Drawer");
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        } else {
            LogUtils.d("Unlock Drawer");
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText("Use DrawerLayout in service")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(1, builder.build());
    }
}
