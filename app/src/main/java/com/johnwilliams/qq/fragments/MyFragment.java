package com.johnwilliams.qq.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class MyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View findViewById(int id){
        return getView().findViewById(id);
    }
}
