package com.example.shreyanshjain.ekta;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.shreyanshjain.ekta.adapters.PagerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Main2Activity extends AppCompatActivity {

    @BindView(R.id.main_page)
    TextView mainText;

    @BindView(R.id.notifications)
    TextView notificationText;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private PagerViewAdapter pagerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerViewAdapter);

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
}