package com.menposun.calambawonders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.menposun.calambawonders.fragments.AccountFragment;
import com.menposun.calambawonders.fragments.FeedbackFragment;
import com.menposun.calambawonders.fragments.HelpFragment;
import com.menposun.calambawonders.fragments.HomeFragment;
import com.menposun.calambawonders.fragments.HomePage;
import com.menposun.calambawonders.fragments.MapFragment;
import com.menposun.calambawonders.fragments.WishlistFragment;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout nav_drawer;

    private NavigationView nav_view;
    CircleImageView drawer_profile;
    private TextView drawer_email, drawer_nickname, drawer_user_type;
    private Toolbar toolbar;
    View nav_header;
    AlertDialog.Builder alert;
    private LocationManager locationManager;

    //STRINGS
    String image;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;
    FirebaseDatabase db;
    DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_maps);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        dbReference = db.getReference("Users");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        nav_drawer = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        
        nav_header = nav_view.getHeaderView(0);
        drawer_email = nav_header.findViewById(R.id.drawer_email);
        drawer_nickname = nav_header.findViewById(R.id.drawer_nickname);
        drawer_profile = nav_header.findViewById(R.id.drawer_profile);
        drawer_user_type = nav_header.findViewById(R.id.drawer_user_type);
        toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.Poppins);

        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(this, nav_drawer, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        nav_drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //initialize homepage

        if(!isConnectedToInternet()){
            showNoInternetDialog();
        }
        //set landing page
        setFragment(new HomePage());


        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.homepage:
                        nav_drawer.closeDrawer(GravityCompat.START);
                        //setFragment(new LandingFragment(null, null));
                        setFragment(new HomePage());
                        //setFragment(new HomeFragment());
                        return true;
                    case R.id.view_map:
                        nav_drawer.closeDrawer(GravityCompat.START);
                        if(isGPSEnabled()){
                            setFragment(new MapFragment());
                        } else {
                            goToSettings();
                        }
                        return true;
                    case R.id.back_home:
                        Logout(this);
                        return true;
                    case R.id.accnt:
                        if(user.isAnonymous()){
                            Toast.makeText(HomeActivity.this, "This is not available for Guests! Login to unlock this feature.", Toast.LENGTH_LONG).show();
                        } else {
                            nav_drawer.closeDrawer(GravityCompat.START);
                            setFragment(new AccountFragment());
                        }
                        return true;
                    case R.id.wish:
                        if(user.isAnonymous()){
                            Toast.makeText(HomeActivity.this, "This is not available for Guests! Login to unlock this feature.", Toast.LENGTH_LONG).show();
                        } else {
                            nav_drawer.closeDrawer(GravityCompat.START);
                            setFragment(new WishlistFragment());
                        }
                        return true;
                    case R.id.help:
                        nav_drawer.closeDrawer(GravityCompat.START);
                        setFragment(new HelpFragment());
                        return true;
                    case R.id.feedback:
                        nav_drawer.closeDrawer(GravityCompat.START);
                        setFragment(new FeedbackFragment());
                        return true;
                }
                return true;
            }
        });
        alert = new AlertDialog.Builder(HomeActivity.this);

        //load profile and profile checkup
        if(user.isAnonymous()){
            drawer_nickname.setText("Guest"+Math.floor(Math.random()*999));
            drawer_email.setText("No Email Provided");
            drawer_user_type.setText("Guest");
            Glide.with(getApplicationContext()).load(R.drawable.man1).fitCenter().into(drawer_profile);
            Toast.makeText(this, "You logged in as Guest. Your data won't be saved.", Toast.LENGTH_LONG).show();
        } else {
            loadProfile();
            Toast.makeText(this, "" + user.getEmail() + " is logged in", Toast.LENGTH_LONG).show();
        }
    }

    public void entranceReminder(){
        alert.setTitle("Set up profile");
        alert.setCancelable(false);
        alert.setMessage("Seems like you haven't set your account profile yet. Do you want to take a few seconds while setting up your profile? It wont last long");
        alert.setPositiveButton("Yes, thanks!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setFragment(new AccountFragment());
                dialog.cancel();
                dialog.dismiss();
            }
        }).setNegativeButton("Skip for this time", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                Toast.makeText(HomeActivity.this, "You may set your profile " +
                        "at 'View Profile' section. Swipe left", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    private void loadProfile() {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        dialog.setMessage("Loading profile...");
        dialog.setCancelable(false);
        Query load = dbReference.orderByChild("email").equalTo(user.getEmail());
        load.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    drawer_email.setText(ds.child("email").getValue().toString());
                    drawer_nickname.setText(ds.child("nickname").getValue().toString());
                    drawer_user_type.setText(ds.child("type").getValue().toString());

                    //String UUID = "" + ds.child("UUID").getValue();

                    image = ds.child("profile_pic").getValue().toString();
//                    boolean onceDilaog = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("once", true);

                    if(drawer_nickname.getText().toString().equals("Unknown")){
                        entranceReminder();
//                        getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//                                .edit()
//                                .putBoolean("once", false)
//                                .commit();
                    }

                    try {
                        Glide.with(getApplicationContext()).load(image).fitCenter().into(drawer_profile);
                    } catch (Exception e){
                        Glide.with(getApplicationContext()).load(R.drawable.man1).fitCenter().into(drawer_profile);
                    }
                }
                dialog.cancel();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void Logout(NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Calamba Wonders");
        dialog.setMessage("Are you sure to logout? Your account will not be saved.");
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nav_drawer.closeDrawer(GravityCompat.START);
                if(user.isAnonymous()){
                    firebaseAuth.getCurrentUser().delete();
                }
                firebaseAuth.signOut();
                Toast.makeText(HomeActivity.this.getApplicationContext(),
                        "Logout success!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomeActivity.this, AccountLogin.class));
                        finish();
                        CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
                    }
                }, 2500);
            }
        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.search_view:
                setFragment(new MapFragment());
                return false;
            case R.id.help_map:
                 HelpMap();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.close_app:
                CloseApp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void HelpMap() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_guide);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(user.isAnonymous()){
//            drawer_nickname.setText("Tourist"+Math.random()*99);
//        }
    }

    @Override
    public void onBackPressed() {
        if(nav_drawer.isDrawerOpen(GravityCompat.START)){
            nav_drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void setFragment(Fragment frag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, frag).commit();
    }

    public boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void showNoInternetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("Internet Required");
        dialog.setCancelable(false);
        dialog.setMessage("Apologies, but unfortunately you don't have an internet access. Please enable WiFi or Data Connection to continue.");
        dialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        }).show();
    }
    private boolean isGPSEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void goToSettings(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this)
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