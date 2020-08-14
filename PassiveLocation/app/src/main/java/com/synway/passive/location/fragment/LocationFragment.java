package com.synway.passive.location.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

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
import com.synway.passive.location.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private Unbinder unbinder;
    private LineDataSet lineDataSet;
    private LineData lineData;

    private List<Integer> valueList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        unbinder = ButterKnife.bind(this, view);

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
                LogUtils.log("aaaa" + value);
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


        new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int value = (int) (Math.random() * 100);
                valueList.add(value);
                addEntry(value);
            }

            @Override
            public void onFinish() {

            }
        }.start();
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
    }
}
