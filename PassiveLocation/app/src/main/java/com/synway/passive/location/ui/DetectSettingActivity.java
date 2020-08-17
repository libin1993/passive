package com.synway.passive.location.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.socket.LteSendManager;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.SPUtils;
import com.synway.passive.location.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DetectSettingActivity extends BaseActivity {


    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.tv_title_name)
    TextView tvTitleName;
    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.spinner_detect_interval)
    Spinner spinner;
    @BindView(R.id.et_notify_number)
    EditText etNotifyNumber;
    @BindView(R.id.tv_detect_result)
    TextView tvDetectResult;
    @BindView(R.id.btn_start_test)
    Button btnStartTest;
    private Unbinder unbinder;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_setting);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        StatusBarUtils.getInstance().setStatusBarHeight(viewStatusBar);
        tvTitleName.setText("检测设置");

        ArrayAdapter<String> adapter =new ArrayAdapter<>(this,R.layout.spinner_dropdown_item);

        adapter.addAll();
        for (int i = 0; i < CacheManager.detectArr.length; i++) {
            adapter.add(String.valueOf(CacheManager.detectArr[i]));
        }
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int defaultInterval= SPUtils.getInstance().getDetectInterval();

        spinner.setSelection(defaultInterval);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SPUtils.getInstance().put(SPUtils.DETECT_INTERVAL,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void startDetect() {
        LteSendManager.sendMonitor(CacheManager.phoneNumber);
        LoadingUtils.getInstance().showLoading(this, "检测中");
        countDownTimer = new CountDownTimer(CacheManager.detectArr[SPUtils.getInstance().getDetectInterval()], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                LoadingUtils.getInstance().dismiss();
                tvDetectResult.setText("检测超时");
            }
        }.start();
    }

    /**
     * 检测成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanResult(String result) {
        if (MsgType.MONITOR_SUCCESS.equals(result)) {
            LoadingUtils.getInstance().dismiss();
            countDownTimer.cancel();
            tvDetectResult.setText("检测成功");
        } else if (MsgType.MONITOR_FAIL.equals(result)) {
            LoadingUtils.getInstance().dismiss();
            countDownTimer.cancel();
            tvDetectResult.setText("检测失败");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.iv_title_back, R.id.btn_start_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_start_test:
                startDetect();
                break;
        }
    }
}

