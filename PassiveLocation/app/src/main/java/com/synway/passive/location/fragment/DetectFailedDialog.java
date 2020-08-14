package com.synway.passive.location.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.synway.passive.location.R;
import com.synway.passive.location.bean.CellBean;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.StatusBarUtils;
import com.synway.passive.location.widget.RVDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DetectFailedDialog extends DialogFragment {

    @BindView(R.id.rvPhoneHardwareInfo)
    RecyclerView rvPhoneHardwareInfo;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_research)
    Button btnResearch;
    private Unbinder unbinder;
    private List<CellBean> cellList = new ArrayList<>();
    private BaseQuickAdapter<CellBean, BaseViewHolder> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detect_failed, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();

        return view;
    }

    private void initView() {
        for (Map.Entry<String, CellBean> entry : CacheManager.cellMap.entrySet()) {
            cellList.add(entry.getValue());
        }
        rvPhoneHardwareInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPhoneHardwareInfo.addItemDecoration(new RVDividerItemDecoration(getContext(),0,R.drawable.rv_divider_black));
        adapter = new BaseQuickAdapter<CellBean, BaseViewHolder>(R.layout.layout_cell_item, cellList) {
            @Override
            protected void convert(BaseViewHolder helper, CellBean item) {
                helper.setText(R.id.tv_cell_no, (helper.getAdapterPosition() + 1) + "");
                helper.setText(R.id.tv_cell_fcn, item.getFreq() + "");
                helper.setText(R.id.tv_cell_lac, item.getLac() + "");
                helper.setText(R.id.tv_cell_cid, item.getCid() + "");
                helper.setText(R.id.tv_cell_pci, item.getPci() + "");
            }
        };
        rvPhoneHardwareInfo.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_research})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_research:
                dismiss();
                EventBus.getDefault().post("research");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(StatusBarUtils.getInstance().getScreenWidth(getDialog().getOwnerActivity())
                - FormatUtils.getInstance().dp2px(40), ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

