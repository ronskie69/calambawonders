package com.menposun.calambawonders.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.menposun.calambawonders.fragments.Visited;
import com.menposun.calambawonders.fragments.Favorites;
import com.menposun.calambawonders.fragments.PlanningToVisit;


public class FragmentAdapterWishlists extends FragmentStateAdapter {


    public FragmentAdapterWishlists(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new Visited();
            case 2:
                return new PlanningToVisit();
        }

        return new Favorites();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}