package com.menposun.calambawonders.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.menposun.calambawonders.R;

import org.jetbrains.annotations.NotNull;

public class AboutCalamba extends Fragment {

    View vue;
    MaterialButton mbVisitCalamba;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        vue = inflater.inflate(R.layout.layout_about_calamba, container, false);

        mbVisitCalamba = vue.findViewById(R.id.learn_more);
        mbVisitCalamba.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.calambacity.gov.ph/index.php/visit-calamba/tourist-attractions"));
            startActivity(intent);
        });
        return vue;
    }
}