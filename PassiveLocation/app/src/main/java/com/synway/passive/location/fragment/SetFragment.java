package com.synway.passive.location.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;
import com.synway.passive.location.ui.DetectSettingActivity;
import com.synway.passive.location.ui.InductionSettingActivity;
import com.synway.passive.location.ui.NameListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author：Libin on 2020/8/8 14:29
 * Email：1993911441@qq.com
 * Describe：设置 */
public class SetFragment extends BaseFragment {
    @BindView(R.id.tvInductionSetting)
    TextView tvInductionSetting;
    @BindView(R.id.tvDetectSetting)
    TextView tvDetectSetting;
    @BindView(R.id.tvManagePhoneNumberList)
    TextView tvManagePhoneNumberList;

    private Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void initViews(){
        tvInductionSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InductionSettingActivity.class);
                getActivity().startActivity(intent);
            }
        });
        tvDetectSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetectSettingActivity.class);
                getActivity().startActivity(intent);
            }
        });
        tvManagePhoneNumberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NameListActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }
    public static SetFragment newInstance() {

        Bundle args = new Bundle();

        SetFragment fragment = new SetFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
