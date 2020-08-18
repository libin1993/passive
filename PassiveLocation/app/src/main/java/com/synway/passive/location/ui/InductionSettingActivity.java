package com.synway.passive.location.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.SPUtils;
import com.synway.passive.location.utils.StatusBarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class InductionSettingActivity extends BaseActivity {

    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.tv_title_name)
    TextView tvTitleName;
    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.spinner_trigger_style)
    Spinner spinnerTriggerStyle;
    @BindView(R.id.spinner_trigger_times)
    Spinner spinnerTriggerTimes;
    @BindView(R.id.spinner_trigger_timeout)
    Spinner spinnerTriggerTimeout;
    @BindView(R.id.spinner_trigger_interval)
    Spinner spinnerTriggerInterval;
    private Unbinder unbinder;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_induction);
        unbinder = ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        StatusBarUtils.getInstance().setStatusBarHeight(viewStatusBar);
        tvTitleName.setText("诱发设置");


        initAdapter(spinnerTriggerStyle, CacheManager.styleArr);
        initAdapter(spinnerTriggerTimes,CacheManager.timesArr);
        initAdapter(spinnerTriggerTimeout,CacheManager.timeoutArr);
        initAdapter(spinnerTriggerInterval,CacheManager.intervalArr);


        int defaultStyle = SPUtils.getInstance().getTriggerStyle();
        int defaultTimes = SPUtils.getInstance().getTriggerTimes();
        int defaultTimeout = SPUtils.getInstance().getTriggerTimeout();
        int defaultInterval= SPUtils.getInstance().getTriggerInterval();

        spinnerTriggerStyle.setSelection(defaultStyle);
        spinnerTriggerTimes.setSelection(defaultTimes);
        spinnerTriggerTimeout.setSelection(defaultTimeout);
        spinnerTriggerInterval.setSelection(defaultInterval);


        spinnerTriggerStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SPUtils.getInstance().put(SPUtils.TRIGGER_STYLE,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTriggerTimes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPUtils.getInstance().put(SPUtils.TRIGGER_TIMES,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTriggerTimeout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPUtils.getInstance().put(SPUtils.TRIGGER_TIMEOUT,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTriggerInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPUtils.getInstance().put(SPUtils.TRIGGER_INTERVAL,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void initAdapter(Spinner spinner, Object[] values) {
        ArrayAdapter<String> adapter =new ArrayAdapter<>(this,R.layout.spinner_select_item);

        for (int i = 0; i < values.length; i++) {
            adapter.add(String.valueOf(values[i]));
        }
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @OnClick(R.id.iv_title_back)
    public void onViewClicked() {
        finish();
    }
}

