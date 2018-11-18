package com.example.shreyanshjain.ekta;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.shreyanshjain.ekta.adapters.PagerViewAdapter;
import com.example.shreyanshjain.ekta.service.OpenAppService;
import com.example.shreyanshjain.ekta.service.RecorderService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Main2Activity extends AppCompatActivity{

    @BindView(R.id.main_page)
    TextView mainText;

    @BindView(R.id.notifications)
    TextView notificationText;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private PagerViewAdapter pagerViewAdapter;
    BroadcastReceiver mReceiver;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

//        startService(new Intent(this, OpenAppService.class));
        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerViewAdapter);

        tabLayout.setupWithViewPager(viewPager);

        /*
            TODO: Create a service that can start the app by pressing power button 5 times -- Created a BroadcastReceiver
            TODO: Also add a feature of voice recognition in that service which can trigger the app while it receives a particular voice note
         */

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new PowerButtonBroadcast();
        registerReceiver(mReceiver, filter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        intent = new Intent(Main2Activity.this,RecorderService.class);
        intent.putExtra(RecorderService.INTENT_VIDEO_PATH, "/video/camera/");//eg: "/video/camera/"
        startService(intent);
    }

    @OnClick(R.id.main_page)
    void setPosition()
    {
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.notifications)
    void setNotification()
    {
        viewPager.setCurrentItem(1);
    }

    private void changeTabs(int position)
    {
        if(position == 0){
            mainText.setTextColor(getResources().getColor(R.color.textBright));
            mainText.setTextSize(22);

            notificationText.setTextColor(getResources().getColor(R.color.textLight));
            notificationText.setTextSize(16);
        }


        if(position == 1){
            mainText.setTextColor(getResources().getColor(R.color.textLight));
            mainText.setTextSize(16);

            notificationText.setTextColor(getResources().getColor(R.color.textBright));
            notificationText.setTextSize(22);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_guard:
                Log.i("Set Guardians", "clicked");
//                return true;
                break;
            default:
                Log.e("Default", "error");

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(mReceiver != null)
        {
            unregisterReceiver(mReceiver);
            mReceiver = null;
            stopService(intent);
        }
        super.onDestroy();
    }
}
