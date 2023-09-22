package com.menposun.calambawonders.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.adapter.FragmentAdapter;
import com.menposun.calambawonders.adapter.FragmentAdapterWishlists;


public class WishlistFragment extends Fragment {

    //views
    View vue;
    TabLayout tab_layout;
    FragmentAdapterWishlists fragmentAdapter;
    ViewPager2 pager;
    //animation
    Animation anim1, anim2;



    public WishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vue =  inflater.inflate(R.layout.fragment_wishlist, container, false);

        pager = vue.findViewById(R.id.pager_wishlist);
        tab_layout = vue.findViewById(R.id.tab_layout_wishlist);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();



        fragmentAdapter = new FragmentAdapterWishlists(fragmentManager, getLifecycle());
        pager.setAdapter(fragmentAdapter);

        tab_layout.addTab(tab_layout.newTab().setText("Favorites"));
        tab_layout.addTab(tab_layout.newTab().setText("Visited"));
        tab_layout.addTab(tab_layout.newTab().setText("Planning to Visit"));

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab_layout.selectTab(tab_layout.getTabAt(position));
            }
        });


        return vue;
    }
    private void redirect(Fragment frag){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, frag).commit();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle("Calamba Wonders");
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle("Wishlists");
    }
}