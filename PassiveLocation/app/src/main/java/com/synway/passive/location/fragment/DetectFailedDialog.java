package com.synway.passive.location.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synway.passive.location.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetectFailedDialog extends DialogFragment {

    private Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_detect_failed,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

}
