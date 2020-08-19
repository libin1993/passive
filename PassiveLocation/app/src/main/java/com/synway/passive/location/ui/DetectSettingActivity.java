package com.synway.passive.location.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.synway.passive.location.socket.BluetoothSocketUtils;
import com.synway.passive.location.socket.LteSendManager;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.OSUtils;
import com.synway.passive.location.utils.SPUtils;
import com.synway.passive.location.utils.StatusBarUtils;
import com.synway.passive.location.utils.ToastUtils;

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
    @BindView(R.id.et_target_number)
    EditText etTargetNumber;
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

        if (!TextUtils.isEmpty(CacheManager.phoneNumber)){
            etTargetNumber.setText(CacheManager.phoneNumber);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_select_item);

        adapter.addAll();
        for (int i = 0; i < CacheManager.detectArr.length; i++) {
            adapter.add(String.valueOf(CacheManager.detectArr[i]));
        }
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int defaultInterval = SPUtils.getInstance().getDetectInterval();

        spinner.setSelection(defaultInterval);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SPUtils.getInstance().put(SPUtils.DETECT_INTERVAL, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void startDetect() {
        tvDetectResult.setText(null);
        String phoneNumber = etTargetNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11){
            ToastUtils.getInstance().showToast("请输入11位目标手机号码");
            return;
        }

        OSUtils.getInstance().sendMsg(phoneNumber, FormatUtils.getInstance().getDetectSms());
        tvDetectResult.setText("检测短信已发送\n");
        countDownTimer = new CountDownTimer(CacheManager.detectArr[SPUtils.getInstance().getDetectInterval()]*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                tvDetectResult.append("检测超时\n");
            }
        }.start();
    }

    /**
     * 检测成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanResult(String result) {
        if (MsgType.DETECT_SEND_MSG.equals(result)) {

            if (countDownTimer ==null){
                return;
            }

            tvDetectResult.append("目标号码："+etTargetNumber.getText().toString().trim()+"目标开机\n");

            String notifyNumber = etNotifyNumber.getText().toString().trim();
            if (!TextUtils.isEmpty(notifyNumber) && notifyNumber.length() ==11){
                OSUtils.getInstance().sendCommonMsg(notifyNumber, "目标号码："+etTargetNumber.getText().toString().trim()+"目标开机");
                tvDetectResult.append("通知短信已发送\n");
            }

            if (countDownTimer !=null){
                countDownTimer.cancel();
                countDownTimer = null;
            }
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

