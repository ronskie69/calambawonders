package com.menposun.calambawonders.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.menposun.calambawonders.fragments.AboutCalamba;
import com.menposun.calambawonders.fragments.FestivalsAndEvents;
import com.menposun.calambawonders.fragments.History;
import com.menposun.calambawonders.fragments.Reviews;
import com.menposun.calambawonders.fragments.TopPlaces;

import org.jetbrains.annotations.NotNull;

public class HomePageAdapter extends FragmentStateAdapter {

    public HomePageAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new FestivalsAndEvents();
            case 2:
                return new TopPlaces();
        }

        return new AboutCalamba();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}