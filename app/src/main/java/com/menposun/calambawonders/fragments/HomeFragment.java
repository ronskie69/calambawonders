package com.menposun.calambawonders.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.mapbox.android.core.location.LocationEngineRequest;
import com.menposun.calambawonders.R;


public class HomeFragment extends Fragment {

    private View vue;
    private Button btnViewMap;
    private BounceInterpolator interpolator = new BounceInterpolator();
    private TextView welcome_text, welcome_title;
    private Animation anim, anim2, anim3;
    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vue =  inflater.inflate(R.layout.fragment_home, container, false);
        btnViewMap = vue.findViewById(R.id.btnViewMap);
        anim = AnimationUtils.loadAnimation(getContext(),R.anim.bounce);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGPSEnabled()){
                    redirect(new MapFragment());
                } else {
                    goToSettings();
                }
            }
        });

        return vue;
    }

    private boolean isGPSEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void redirect(Fragment frag){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, frag).commit();
    }

    private void goToSettings(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setMessage("Please open GPS or Location Services to continue.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        dialog.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnViewMap.startAnimation(anim);
    }
}