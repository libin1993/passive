package com.synway.passive.location;

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

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
    private List<Integer> dataList = new ArrayList<>();
    private BaseQuickAdapter<Integer, BaseViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.layout_item,dataList) {
            @Override
            protected void convert(BaseViewHolder helper, Integer item) {
                ProgressBar progressBar = helper.getView(R.id.pb);
                setProgress(progressBar,item);

                helper.setText(R.id.tv_value,String.valueOf(item));
            }
        };

        recyclerView.setAdapter(adapter);


        new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                dataList.add((int) (Math.random()*100));
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(dataList.size());
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


    public void setProgress(ProgressBar progressBar,int value) {
        //Background
        int colorInt;
        if (value < 33){
            colorInt = Color.GREEN;
        }else if (value < 66){
            colorInt = Color.BLUE;
        }else {
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
