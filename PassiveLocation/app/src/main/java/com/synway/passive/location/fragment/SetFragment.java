package com.synway.passive.location.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author：Libin on 2020/8/8 14:29
 * Email：1993911441@qq.com
 * Describe：设置 */
public class SetFragment extends BaseFragment {
    private Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
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
