package com.menposun.calambawonders.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.adapter.CityAdapter;


public class TopPlaces  extends Fragment {

    View vue;
    ImageView bay, mall, monument;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vue = inflater.inflate(R.layout.layout_top_places, container, false);
        bay = vue.findViewById(R.id.bay);
        mall = vue.findViewById(R.id.mall);
        monument = vue.findViewById(R.id.monument);

        //anti -lag
        Glide.with(getContext()).load(R.drawable.baywalk_window).fitCenter().into(bay);
        Glide.with(getContext()).load(R.drawable.sm_window).fitCenter().into(mall);
        Glide.with(getContext()).load(R.drawable.rizal_window).fitCenter().into(monument);

        return vue;
    }
}