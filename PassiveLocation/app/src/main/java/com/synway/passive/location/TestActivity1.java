package com.synway.passive.location;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;


public class TestActivity1 extends AppCompatActivity {

    @BindView(R.id.bar_chart)
    BarChart barChart;
    @BindView(R.id.chart_view)
    ColumnChartView chartView;

    private BarDataSet barDataSet;
    private BarData barData;
    private List<BarEntry> dataList = new ArrayList<>();


    private ColumnChartData data;             //存放柱状图数据的对象



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        ButterKnife.bind(this);


        for (int i = 0; i < 10; i++) {
            dataList.add(new BarEntry(i, 0));
        }

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDragXEnabled(true);
        barChart.setDrawBorders(false);
        barChart.setNoDataText("");


        // 获取 x 轴
        XAxis xAxis = barChart.getXAxis();
        // 设置 x 轴显示位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 取消 垂直 网格线
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(10);

        // 横坐标文字


        // 获取 右边 y 轴
        YAxis mRAxis = barChart.getAxisRight();
        // 隐藏 右边 Y 轴
        mRAxis.setEnabled(false);
        // 获取 左边 Y轴
        YAxis mLAxis = barChart.getAxisLeft();
        // 取消 横向 网格线
        mLAxis.setDrawGridLines(false);
        mLAxis.setAxisMinimum(0);
        // 设置 Y轴 的刻度数量


        BarDataSet barDataSet = new BarDataSet(dataList, "");
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);
        if (dataList.size() < 10) {
            barData.setBarWidth(dataList.size() / 10f);
            barChart.setData(barData);
        } else {
            barChart.setData(barData);
            Matrix m = new Matrix();
            m.postScale(dataList.size() / 10f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
            barChart.getViewPortHandler().refresh(m, barChart, false);//将图表动画显示之前进行缩放
        }

//        barDataSet = new BarDataSet(dataList,"");
//        barDataSet.setDrawValues(true);
//        barData = new BarData(barDataSet);
//        barChart.setData(barData);

        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.d("libin", "getFormattedValue: " + value);
                if (value >= 0 && value < dataList.size()) {
                    return (int) dataList.get((int) value).getY() + "";

                } else {
                    return null;
                }
            }
        });


        new CountDownTimer(200000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {

//                if (dataList.size() > 0){
//                    barChart.clear();
//                }

                BarEntry entry = new BarEntry(dataList.size(), (int) (Math.random() * 100));
                dataList.add(0, entry);


                BarDataSet barDataSet = new BarDataSet(dataList, "");
                barDataSet.setDrawValues(false);

                BarData barData = new BarData(barDataSet);
                barData.setHighlightEnabled(false);
                if (dataList.size() < 10) {
                    barData.setBarWidth(dataList.size() / 10f);
                    barChart.setData(barData);
                } else {
                    barChart.setData(barData);
//                    Matrix m = new Matrix();
//                    m.postScale(dataList.size() / 10f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
//                    barChart.getViewPortHandler().refresh(m, barChart, false);//将图表动画显示之前进行缩放
                }

                barData.notifyDataChanged();
                barChart.notifyDataSetChanged();
                barChart.invalidate();


//                barChart.moveViewToX(0);


//                barData.addEntry(entry,0);
//                barData.setDrawValues(false);
//                barData.setHighlightEnabled(false);
//
//                if (dataList.size() < 10){
//                    barChart.getData().setBarWidth(dataList.size()/10f);
//                }else {
//                    Matrix m = new Matrix();
//                    m.postScale(dataList.size()/10f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
//                    barChart.getViewPortHandler().refresh(m, barChart, false);//将图表动画显示之前进行缩放
//                }
//
//
//                barData.notifyDataChanged();
//                barChart.notifyDataSetChanged();
//                barChart.invalidate();
//                barChart.moveViewToX(dataList.size());
//
//                Log.d("libin", "onTick: "+dataList.size());

            }

            @Override
            public void onFinish() {

            }
        }.start();



        int numSubcolumns = 1;
        int numColumns =20;

        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<AxisValue> y = new ArrayList<AxisValue>();
        for (int i = 0; i < 10; i++) {
            y.add(new AxisValue(i).setLabel(String.valueOf(10*i)));
        }
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue(i+50, ChartUtils.pickColor()));
            }

            Column column = new Column(values);
            column.setHasLabels(false);
            columns.add(column);
            axisValues.add(new AxisValue(i).setLabel(String.valueOf(i+50)));
        }

        data = new ColumnChartData(columns);

        data.setAxisXBottom(new Axis(axisValues).setHasLines(true)
                .setTextColor(Color.BLACK));

        data.setAxisYLeft(new Axis(axisValues).setHasLines(true)
                .setTextColor(Color.BLACK));

//        data.setFillRatio(0.1f);
        chartView.setMaxZoom(numColumns/10);//按照柱体数量增加缩放次数
        chartView.setZoomEnabled(false);
        chartView.setColumnChartData(data);

        //下面的代码放在setColumnChartData之前是无法得到有效执行的
        Viewport v = new Viewport(chartView.getMaximumViewport());
//        v.top = 105;
        v.bottom= 0;
        chartView.setMaximumViewport(v);
        v.left= 0;//从最右边最新的数据开始进行显示
        v.right=0;
        chartView.setCurrentViewport(v);

    }
}
