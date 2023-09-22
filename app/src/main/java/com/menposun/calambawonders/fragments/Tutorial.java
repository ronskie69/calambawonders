package com.menposun.calambawonders.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.menposun.calambawonders.R;


public class Tutorial extends Fragment {

    MaterialButton btnUnderstand;
    View vue;

    public Tutorial() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences.Editor firstRun = getActivity().getSharedPreferences("firstUse", Context.MODE_PRIVATE).edit();
        firstRun.putBoolean("tutorialDone", true);
        firstRun.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vue= inflater.inflate(R.layout.fragment_tutorial, container, false);
        btnUnderstand = vue.findViewById(R.id.btnUnderstand);


        btnUnderstand.setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setCancelable(false);
            dialog.setMessage("Please wait...");
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                    redirect(new HomeFragment());
                }
            }, 3000);
        });
        return  vue;
    }
    private void redirect(Fragment frag){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, frag).commit();
    }
}