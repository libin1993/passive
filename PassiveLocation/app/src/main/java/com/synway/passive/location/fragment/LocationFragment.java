package com.synway.passive.location.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hrst.sdk.HrstSdkCient;
import com.hrst.sdk.callback.RequestCallBack;
import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;
import com.synway.passive.location.bean.LocationInfoBean;
import com.synway.passive.location.socket.BluetoothSocketUtils;
import com.synway.passive.location.socket.LteSendManager;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LogUtils;
import com.synway.passive.location.utils.OSUtils;
import com.synway.passive.location.utils.SPUtils;
import com.synway.passive.location.utils.ToastUtils;
import com.synway.passive.location.widget.CircleProgressView;
import com.synway.passive.location.widget.MyCountDownTimer;
import com.synway.passive.location.widget.RVDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author：Libin on 2020/8/8 14:28
 * Email：1993911441@qq.com
 * Describe：定位
 */
public class LocationFragment extends BaseFragment {
    @BindView(R.id.rgLocateMode)
    RadioGroup rgLocateMode;

    @BindView(R.id.chart)
    LineChart lineChart;
    @BindView(R.id.bntStartInduction)
    Button bntStartInduction;
    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.rv_trigger_status)
    RecyclerView rvTriggerStatus;
    @BindView(R.id.tvInductionHitCount)
    TextView tvInductionHitCount;
    @BindView(R.id.circle_progress)
    CircleProgressView circleProgress;
    private Unbinder unbinder;
    private LineDataSet lineDataSet;
    private LineData lineData;

    private List<Integer> valueList = new ArrayList<>();
    private boolean startTrigger = false;

    private List<Boolean> triggerList = new ArrayList<>();
    private BaseQuickAdapter<Boolean, BaseViewHolder> adapter;
    private int triggerTimes = 0; //诱发次数
    private int replyTimes = 0; //诱发开始、结束诱发回复次数

    private MyCountDownTimer countDownTimer;

//    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    private void initView() {

        rgLocateMode.setOnCheckedChangeListener(onCheckedChangeListener);

        lineChart.setNoDataText("");
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerTapEnabled(false);//隐藏图表内的点击十字线
        lineChart.setHighlightPerDragEnabled(false);
        //显示边界
        lineChart.setDrawBorders(false);
        //设置缩放
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        //保证Y轴从0开始，不然会上移一点
        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setEnabled(false);
        axisLeft.setAxisMinimum(0);
        axisLeft.setAxisMaximum(120);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false);
//        xAxis.setPosition(XAxis.XAxisPosition.TOP);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if (value >= 0 && value < valueList.size()) {
//                    return valueList.get((int) value) + "";
//                } else {
//                    return "";
//                }
//
//            }
//        });
//        xAxis.setDrawAxisLine(false);
//        xAxis.setDrawGridLines(false);
//        xAxis.setTextColor(Color.RED);
//        xAxis.setLabelCount(1, true);


        lineDataSet = new LineDataSet(null, "");
        lineDataSet.setLineWidth(2f);
        lineDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value);
            }
        });
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setCircleColorHole(Color.RED);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setHighLightColor(Color.RED);
        //设置曲线填充
        lineDataSet.setDrawValues(true);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.chart_fill));
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setValueTextColor(Color.RED);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();

        rvTriggerStatus.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvTriggerStatus.addItemDecoration(new RVDividerItemDecoration(getActivity(), true, R.drawable.rv_divider_black_vertical));
        adapter = new BaseQuickAdapter<Boolean, BaseViewHolder>(R.layout.layout_trigger_item, triggerList) {
            @Override
            protected void convert(BaseViewHolder helper, Boolean item) {
                TextView tvStatus = helper.getView(R.id.tv_trigger_status);
                tvStatus.setText(String.valueOf(helper.getAdapterPosition() + 1));
                if (item) {
                    tvStatus.setBackgroundResource(R.color.blue_288);
                } else {
                    tvStatus.setBackgroundResource(R.color.red_e38);
                }

            }
        };
        rvTriggerStatus.setAdapter(adapter);

//        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//                    textToSpeech.setPitch(1f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
//                    textToSpeech.setSpeechRate(1f);
//                    int result = textToSpeech.setLanguage(Locale.CHINESE);
//                    if (result == TextToSpeech.LANG_MISSING_DATA
//                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        LogUtils.log("不支持语音播报中文");
//                    }
//                }
//            }
//        });

//        circleProgress.startAnim();
//
//
//
//        new MyCountDownTimer(50000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                int dbm = (int) (Math.random()*120);
//                valueList.add(Integer.valueOf(dbm));
//                addEntry(dbm);
//                circleProgress.setValue("" + dbm);
//                tvInductionHitCount.setText("命中" + valueList.size() + "次");
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        }.start();
    }


    /**
     * 动态添加数据（一条折线图）
     *
     * @param number
     */
    public void addEntry(int number) {

        //添加 lineDataSet
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        if (valueList.size() <= 6) {
            lineChart.getXAxis().setLabelCount(valueList.size(), true);
        } else {
            lineChart.getXAxis().setLabelCount(6, true);
        }

        lineChart.setData(lineData);

        Entry entry = new Entry(lineDataSet.getEntryCount(), number);
        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
        lineChart.setVisibleXRangeMaximum(5);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 5);

    }

    /**
     * 定位成功
     */
    private void startLocation() {
        valueList.clear();
        circleProgress.setValue("0");
        circleProgress.startAnim();
        lineDataSet.clear();
        lineChart.clear();
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        triggerList.clear();
        adapter.notifyDataSetChanged();
        tvPhoneNumber.setText(CacheManager.phoneNumber);
        startTrigger = false;
        bntStartInduction.setText("开始诱发");
        tvInductionHitCount.setText("命中  次");

        ((RadioButton) rgLocateMode.getChildAt(2)).setChecked(true);

    }


    @OnClick(R.id.bntStartInduction)
    public void onViewClicked() {
        if (!CacheManager.isLocation) {
            ToastUtils.getInstance().showToast("请先定位目标");
            return;
        }

        if (!startTrigger) {
            triggerTimes = 0;
            replyTimes = 0;
            triggerList.clear();
            adapter.notifyDataSetChanged();
            bntStartInduction.setText("停止诱发");
            startTrigger = true;

            countDownTimer = new MyCountDownTimer(CacheManager.timesArr[SPUtils.getInstance().getTriggerTimes()]
                    * CacheManager.intervalArr[SPUtils.getInstance().getTriggerInterval()] * 1000,
                    CacheManager.intervalArr[SPUtils.getInstance().getTriggerInterval()] * 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    LogUtils.log("时间：" + millisUntilFinished);
                    if (triggerTimes < CacheManager.timesArr[SPUtils.getInstance().getTriggerTimes()]) {

                        if (triggerList.size() < triggerTimes) {
                            replyTimes = triggerTimes * 2;
                            triggerList.add(false);
                            adapter.notifyDataSetChanged();
                            rvTriggerStatus.scrollToPosition(triggerList.size() - 1);
                        }

                        if (CacheManager.is5G){
                            HrstSdkCient.sendTriggerStart(1,CacheManager.phoneNumber);
                            EventBus.getDefault().post(MsgType.TRIGGER_SUCCESS);
                        }else {
                            LteSendManager.startTrigger();
                        }

                        triggerTimes++;

                    }
                }

                @Override
                public void onFinish() {
                    if (triggerList.size() < triggerTimes) {
                        triggerList.add(false);
                        adapter.notifyDataSetChanged();
                        rvTriggerStatus.scrollToPosition(triggerList.size() - 1);
                    }
                    bntStartInduction.setText("开始诱发");
                    startTrigger = false;
                    replyTimes = 0;
                    triggerTimes = 0;
                }
            }.start();


        } else {
            bntStartInduction.setText("开始诱发");
            startTrigger = false;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }

    }


    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (!BluetoothSocketUtils.getInstance().isConnected()) {
                ToastUtils.getInstance().showToast("请先连接蓝牙");
                return;
            }

            byte power = 0;
            switch (checkedId) {
                case R.id.rbHighInputGain:
                    power = 0;
                    break;
                case R.id.rbMediumInputGain:
                    power = 1;
                    break;
                case R.id.rbLowInputGain:
                    power = 2;
                    break;
                case R.id.rbSmart:
                    power = 3;
                    break;
            }



            if (CacheManager.is5G){
                HrstSdkCient.setGainMode(power, new RequestCallBack<Boolean>() {
                    @Override
                    public void onAck(Boolean aBoolean) {

                    }
                });
            }else {
                LteSendManager.setPower(power);
            }

        }
    };


    /**
     * 定位命令下发成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationSuccess(String result) {
        if (MsgType.LOCATION_SUCCESS.equals(result)) {

            startLocation();
        } else if (MsgType.TRIGGER_SUCCESS.equals(result)) {
            if (!startTrigger) {
                return;
            }

            if (replyTimes >= CacheManager.timesArr[SPUtils.getInstance().getTriggerTimes()] * 2) {
                return;
            }

            replyTimes++;
            if (replyTimes % 2 == 0) {
                triggerList.add(true);
                adapter.notifyDataSetChanged();
                rvTriggerStatus.scrollToPosition(triggerList.size() - 1);
            } else {
                OSUtils.getInstance().sendMsg(CacheManager.phoneNumber, FormatUtils.getInstance().getSafeSms());
            }

        } else if (MsgType.TRIGGER_FAIL.equals(result)) {
            if (!startTrigger) {
                return;
            }

            if (replyTimes >= CacheManager.timesArr[SPUtils.getInstance().getTriggerTimes()] * 2) {
                return;
            }
            replyTimes++;
            if (replyTimes % 2 == 0) {
                triggerList.add(false);
                adapter.notifyDataSetChanged();
                rvTriggerStatus.scrollToPosition(triggerList.size() - 1);
            } else {
                OSUtils.getInstance().sendMsg(CacheManager.phoneNumber, FormatUtils.getInstance().getSafeSms());
            }
        }
    }

    /**
     * 定位上报
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationReport(LocationInfoBean locationInfoBean) {
        for (Short dbm : locationInfoBean.getDbm()) {
            valueList.add(Integer.valueOf(dbm));
            addEntry(dbm);
            circleProgress.setValue("" + dbm);
//            textToSpeech.speak(dbm+"", TextToSpeech.QUEUE_ADD, null);
            tvInductionHitCount.setText("命中" + valueList.size() + "次");

        }
    }


    public static LocationFragment newInstance() {

        Bundle args = new Bundle();

        LocationFragment fragment = new LocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);

    }

}
