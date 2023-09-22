package com.menposun.calambawonders.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.menposun.calambawonders.fragments.History;
import com.menposun.calambawonders.fragments.Reviews;


public class FragmentAdapter extends FragmentStateAdapter {

    String dbReference;
    String DB_REFERENCE_TOURIST_SPOT;
    String refName;

    public FragmentAdapter(FragmentManager fragmentManager,
                           Lifecycle lifecycle,
                           String DB_REFERENCE_TOURIST_SPOT,
                           String dbreference,
                           String refName
    ) {
        super(fragmentManager, lifecycle);
        this.DB_REFERENCE_TOURIST_SPOT = DB_REFERENCE_TOURIST_SPOT;
        this.dbReference = dbreference;
        this.refName = refName;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new Reviews(DB_REFERENCE_TOURIST_SPOT, dbReference);
        }

        return new History(DB_REFERENCE_TOURIST_SPOT, dbReference, refName);
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}