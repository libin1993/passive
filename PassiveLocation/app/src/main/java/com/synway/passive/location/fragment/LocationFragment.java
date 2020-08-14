package com.synway.passive.location.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;
import com.synway.passive.location.bean.LocationInfoBean;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    @BindView(R.id.tvEnergyInfo)
    TextView tvEnergyInfo;
    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    private Unbinder unbinder;
    private LineDataSet lineDataSet;
    private LineData lineData;

    private List<Integer> valueList = new ArrayList<>();
    private boolean startTrigger = false;

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
        rgLocateMode.check(R.id.rbLowInputGain);
        rgLocateMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });
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
        lineChart.getAxisLeft().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= 0 && value < valueList.size()) {
                    return valueList.get((int) value) + "";
                } else {
                    return "";
                }

            }
        });
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.RED);
        xAxis.setLabelCount(1, true);


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
        lineDataSet.setDrawValues(false);
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

    private void startLocation() {
        valueList.clear();
        tvEnergyInfo.setText("0");
        lineDataSet.clear();
        lineChart.clear();
        lineChart.invalidate();
        tvPhoneNumber.setText(CacheManager.phoneNumber);
        startTrigger = false;
        bntStartInduction.setText("开始诱发");
    }


    @OnClick(R.id.bntStartInduction)
    public void onViewClicked() {
        if (!CacheManager.isLocation) {
            ToastUtils.getInstance().showToast("请先定位目标");
            return;
        }

        if (startTrigger) {
            LoadingUtils.getInstance().showLoading(getActivity(), "正在诱发中");
            bntStartInduction.setText("停止诱发");
            startTrigger = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                }
            }, 0, 5000);
        } else {
            LoadingUtils.getInstance().showLoading(getActivity(), "停止诱发中");
            bntStartInduction.setText("开始诱发");
            startTrigger = false;
        }


    }

    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanResult(LocationInfoBean locationInfoBean) {
        for (Short dbm : locationInfoBean.getDbm()) {
            valueList.add(Integer.valueOf(dbm));
            addEntry(dbm);
            tvEnergyInfo.setText("" + dbm);
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
