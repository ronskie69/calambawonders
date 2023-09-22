package com.menposun.calambawonders.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.menposun.calambawonders.PopularitySetters;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.adapter.CityAdapter;
import com.menposun.calambawonders.adapter.FragmentAdapter;
import com.menposun.calambawonders.adapter.HomePageAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {

    private View vue;
    private TabLayout menu_tab;
    private TextView whatsup;
    private ViewPager2 pager;
    private FloatingActionButton toggle_menu;
    private FragmentManager fragmentManager;
    private LinearLayout titleCalamba;
    private Animation anim2, anim3, bounce;
    private MaterialButton btnLeft, btnCenter, btnRight, book_now;
    private ImageView banga_pic, cal_locator, makiling;
    private LocationManager locationManager;



    public HomePage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //for nougat and below
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1){
            vue = inflater.inflate(R.layout.fragment_homepage2, container, false);
        }
        vue=  inflater.inflate(R.layout.fragment_homepage, container, false);
        menu_tab = vue.findViewById(R.id.menu_tabs);
        pager = vue.findViewById(R.id.home_pager);
        toggle_menu = vue.findViewById(R.id.toggle_menu);
        titleCalamba = vue.findViewById(R.id.titleCalamba);
        whatsup = vue.findViewById(R.id.whatsup);
        btnLeft = vue.findViewById(R.id.btnLeft);
        btnCenter = vue.findViewById(R.id.btnCenter);
        btnRight = vue.findViewById(R.id.btnRight);
        cal_locator = vue.findViewById(R.id.cal_locator);
        banga_pic = vue.findViewById(R.id.banga_pic);
        makiling = vue.findViewById(R.id.makiling);
        book_now = vue.findViewById(R.id.book_now);

        //load images anti-lag
        Glide.with(getContext()).load(R.drawable.banga).fitCenter().into(banga_pic);
        Glide.with(getContext()).load(R.drawable.calamba_locate).fitCenter().into(cal_locator);
        Glide.with(getContext()).load(R.drawable.makiling).fitCenter().into(makiling);
//        Glide.with(getContext()).load(R.drawable.makiling).fitCenter()
//                .into(new ImageViewTarget<Drawable>(makiling) {
//                    @Override
//                    protected void setResource(@Nullable @org.jetbrains.annotations.Nullable Drawable resource) {
//                        RoundedBitmapDrawable corneredDrawable =
//                                RoundedBitmapDrawableFactory.create(getResources(), String.valueOf(resource));
//                        corneredDrawable.setCornerRadius(32.0f);
//                        makiling.setImageDrawable(corneredDrawable);
//                    }
//                });

        //init animatons
        anim2 = AnimationUtils.loadAnimation(getContext(),R.anim.left_to_right);
        anim3 = AnimationUtils.loadAnimation(getContext(),R.anim.right_to_left);
        bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        //apply animations
        btnLeft.setAnimation(anim2);
        btnRight.setAnimation(anim3);
        whatsup.setAnimation(anim2);
        book_now.setAnimation(bounce);

        SharedPreferences prefs = getActivity().getSharedPreferences("firstUse", Context.MODE_PRIVATE);
        boolean tutorialDone = prefs.getBoolean("tutorialDone",false);

        btnCenter.setOnClickListener(v-> {
            if(isGPSEnabled()){
                if(tutorialDone){
                    redirect(new MapFragment());
                } else {
                    redirect(new Tutorial());
                }
            } else {
                goToSettings();
            }
        });
        btnRight.setOnClickListener(v-> {
            if(isGPSEnabled()){
                if(tutorialDone){
                    redirect(new MapFragment());
                } else {
                    redirect(new Tutorial());
                }
            } else {
                goToSettings();
            }
        });
        btnLeft.setOnClickListener(v-> {
            if(isGPSEnabled()){
                if(tutorialDone){
                    redirect(new MapFragment());
                } else {
                    redirect(new Tutorial());
                }
            } else {
                goToSettings();
            }
        });

        toggle_menu.setOnClickListener(v -> {
            DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer);
            drawerLayout.openDrawer(GravityCompat.START);
        });

        book_now.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://m.traveloka.com/en-ph/hotel/philippines/city/calamba-10009444"));
            startActivity(intent);
        });

        menu_tab.addTab(menu_tab.newTab().setText("About Calamba"));
        menu_tab.addTab(menu_tab.newTab().setText("Festivals and Events"));
        menu_tab.addTab(menu_tab.newTab().setText("Top Places"));
        menu_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                menu_tab.selectTab(menu_tab.getTabAt(position));
            }
        });

        fragmentManager = getActivity().getSupportFragmentManager();

        HomePageAdapter homePageAdapter
                = new HomePageAdapter(fragmentManager, getLifecycle());

        pager.setAdapter(homePageAdapter);

        //location
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);



        return vue;
    }
    private void redirect(Fragment frag){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, frag).commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().hide();
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().show();
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private boolean isGPSEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
}