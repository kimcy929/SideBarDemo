package com.example.sidebarinservice;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.example.sidebarinservice.service.SideBarService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.switchSideBar)
    SwitchCompat switchSideBar;

    @BindView(R.id.switchSwipe)
    SwitchCompat switchSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        switchSideBar.setChecked(SideBarService.sideBarService != null);
        switchSwipe.setChecked(PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getBoolean("swipe_orientation", true));

        switchSideBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Intent sideBarService = new Intent(getApplicationContext(), SideBarService.class);
                if (isChecked) {
                    startService(sideBarService);
                } else {
                    stopService(sideBarService);
                }
            }
        });

        switchSwipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean issChecked) {
                PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext())
                        .edit()
                        .putBoolean("swipe_orientation", issChecked)
                        .apply();
            }
        });
    }

}
