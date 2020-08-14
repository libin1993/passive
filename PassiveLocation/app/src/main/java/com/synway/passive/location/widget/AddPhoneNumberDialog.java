<<<<<<< HEAD
package com.synway.passive.location.widget;

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

public class AddPhoneNumberDialog extends DialogFragment {

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.dialog_add_phone_number,container,false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }


}
=======
package com.synway.passive.location.widget;

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

public class AddPhoneNumberDialog extends DialogFragment {

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.dialog_add_phone_number,container,false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }


}
>>>>>>> c628df228c0de7242fcb722add8c7a769319314d
