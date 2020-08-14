package com.synway.passive.location;

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import com.synway.passive.location.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author：Libin on 2020/8/6 08:31
 * Email：1993911441@qq.com
 * Describe：
 */
public class TestActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.line_chart)
    LineChart lineChart;
    @BindView(R.id.view_ring)
    ImageView viewRing;
    private List<Integer> dataList = new ArrayList<>();
    private BaseQuickAdapter<Integer, BaseViewHolder> adapter;
    private LineDataSet lineDataSet;
    private LineData lineData;

    private List<Integer> valueList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.layout_item, dataList) {
            @Override
            protected void convert(BaseViewHolder helper, Integer item) {
                ProgressBar progressBar = helper.getView(R.id.pb);
                setProgress(progressBar, item);

                helper.setText(R.id.tv_value, String.valueOf(item));
            }
        };

        recyclerView.setAdapter(adapter);


        new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                dataList.add((int) (Math.random() * 100));
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(dataList.size());
            }

            @Override
            public void onFinish() {

            }
        }.start();


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
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.chart_fill));
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

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnimation.setInterpolator(lin);
        viewRing.startAnimation(rotateAnimation);
    }

    /**
     * 动态添加数据（一条折线图）
     *
     * @param number
     */
    public void addEntry(int number) {

        //最开始的时候才添加 lineDataSet（一个lineDataSet 代表一条线）
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        if (valueList.size() <= 6) {
            lineChart.getXAxis().setLabelCount(valueList.size(), true);
        } else {
            lineChart.getXAxis().setLabelCount(6, true);
        }

        lineChart.setData(lineData);
        //避免集合数据过多，及时清空（做这样的处理，并不知道有没有用，但还是这样做了）

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


    public void setProgress(ProgressBar progressBar, int value) {
        //Background
        int colorInt;
        if (value < 33) {
            colorInt = Color.GREEN;
        } else if (value < 66) {
            colorInt = Color.BLUE;
        } else {
            colorInt = Color.RED;
        }

        ClipDrawable bgClipDrawable = new ClipDrawable(new ColorDrawable(Color.parseColor("#EEEEEE")), Gravity.BOTTOM, ClipDrawable.VERTICAL);
        bgClipDrawable.setLevel(10000);
        //Progress
        ClipDrawable progressClip = new ClipDrawable(new ColorDrawable(colorInt), Gravity.BOTTOM, ClipDrawable.VERTICAL);
        //Setup LayerDrawable and assign to progressBar
        Drawable[] progressDrawables = {bgClipDrawable, progressClip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.progress);

        progressBar.setProgressDrawable(progressLayerDrawable);
        progressBar.setProgress(value);
    }
}
