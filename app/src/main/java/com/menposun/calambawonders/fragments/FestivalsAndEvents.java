package com.menposun.calambawonders.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.menposun.calambawonders.R;

import java.util.ArrayList;
import java.util.List;


public class FestivalsAndEvents  extends Fragment {

    View vue;
    ImageSlider slider_for_events;
    List<SlideModel> buyahanis;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vue = inflater.inflate(R.layout.layout_festivals_events, container, false);
        slider_for_events = vue.findViewById(R.id.slider_for_events);

        initSlider();
        return vue;
    }

    public void initSlider(){
        buyahanis = new ArrayList<>();
        buyahanis.add(new SlideModel(R.drawable.buhayani_logo, ScaleTypes.FIT));
        buyahanis.add(new SlideModel(R.drawable.buhayani, ScaleTypes.FIT));
        buyahanis.add(new SlideModel(R.drawable.buhayani_people, ScaleTypes.FIT));
        buyahanis.add(new SlideModel(R.drawable.buhayani_sm, ScaleTypes.FIT));
        slider_for_events.setImageList(buyahanis, ScaleTypes.FIT);
    }
}