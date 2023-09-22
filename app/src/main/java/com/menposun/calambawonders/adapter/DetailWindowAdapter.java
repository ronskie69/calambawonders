package com.menposun.calambawonders.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.fragments.FragmentFact;

public class DetailWindowAdapter implements MapboxMap.InfoWindowAdapter {

    public View view;
    public Context context;
    private TextView snippet, title;
    private ImageView image_window;
    private LatLng mylocation;
    private MaterialButton mbtn, btnSelectMode;

    public DetailWindowAdapter(Context context, LatLng mylocation){
        this.context = context;
        this.mylocation = mylocation;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        view = LayoutInflater.from(context).inflate(R.layout.detail_window, null);

        snippet = view.findViewById(R.id.snippet);
        title = view.findViewById(R.id.title_window);
        image_window = view.findViewById(R.id.image_window);
        mbtn = view.findViewById(R.id.btnFact);

        if(TextUtils.isEmpty(marker.getSnippet())){
            image_window.setVisibility(View.GONE);
            mbtn.setVisibility(View.GONE);
            snippet.setVisibility(View.GONE);
            title.setAllCaps(true);
            title.setTextSize(12f);
        }

        mbtn.setOnClickListener(v-> goTo(marker));

        title.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());

        setupImageOnWindow(marker);

        return view;
    }

    public interface ModeSet {
        void setMode(@NonNull String mode);
    }

    private void setupImageOnWindow(Marker marker) {
        switch(marker.getTitle()){
            case "Laurel Mansion":
                setImageResource(R.drawable.laurelmansion);
                break;
            case "SM Calamba":
                setImageResource(R.drawable.sm_window);
                break;
            case "Calamba Baywalk":
                setImageResource(R.drawable.baywalk_window);
                break;
            case "Rizal Shrine":
                setImageResource(R.drawable.rizal_shrine);
                break;
            case "Rizal Monument":
                setImageResource(R.drawable.rizal_window);
                break;
            case "Saint John The Baptist Church":
                setImageResource(R.drawable.church1);
                break;
            case "Naruto's Statue (Anime Street)":
                setImageResource(R.drawable.naruto_gate);
                break;
            case "Kastilyo ng Bucal":
                setImageResource(R.drawable.kastilyo_bucal);
                break;
            case "Palisam Leisure Park":
                setImageResource(R.drawable.palisam);
                break;
            case "Wingsilicious":
                setImageResource(R.drawable.wingsi3);
                break;
            case "Expressive Food Park (NEW!)":
                setImageResource(R.drawable.expressive2);
                break;
            case "El Fili Cafe":
                setImageResource(R.drawable.elfili1);
                break;
            case "Boodle Fight By Hungry Boss":
                setImageResource(R.drawable.boodle_fight);
                break;
            case "Onyang's Street Wings":
                setImageResource(R.drawable.onyangs);
                break;
            case "Laong Laan's Restaurant":
                setImageResource(R.drawable.laong_laan);
                break;
            case "Lutong Pilipino Ni Aling Ely":
                setImageResource(R.drawable.lutong_pilipino);
                break;
            case "Mom's Garden Cafe":
                setImageResource(R.drawable.mommy_garden);
                break;
            case "Kurimi Milk Tea Bar":
                setImageResource(R.drawable.kurimi);
                break;
            case "Bertito's Anyhaw Restaurant":
                setImageResource(R.drawable.bertito);
                break;
            case "Floating Restaurant":
                setImageResource(R.drawable.floating);
                break;
            case "Checkpoint Mall":
                setImageResource(R.drawable.checkpoint_mall);
                break;
            case "iMall Canlubang":
                setImageResource(R.drawable.imall);
                break;
            case "Calamba City Police Station":
                setImageResource(R.drawable.calamba_police);
                break;
            case "CityMALL Calamba":
                setImageResource(R.drawable.citymall);
                break;
            case "Calamba Doctor's Hospital":
                setImageResource(R.drawable.cdh);
                break;
            case "Riverview Resort":
                setImageResource(R.drawable.riverview_resort);
                break;
            case "Pansol Resorts":
                setImageResource(R.drawable.pansol_resorts);
                break;
            case "Monte Vista Hotsprings Resort":
                setImageResource(R.drawable.monte_vista);
                break;
            case "Makiling Highlands Resort":
                setImageResource(R.drawable.makiling_highlands);
                break;
            case "Don Jovita's Garden Resort":
                setImageResource(R.drawable.jovita);
                break;
            case "Wonder Islands Resort":
                setImageResource(R.drawable.wonder);
                break;
            case "Villa Carmen Resort":
                setImageResource(R.drawable.villa_carmen);
                break;
            case "Makiling Hot Springs":
                setImageResource(R.drawable.makiling_hotsprings);
                break;
            case "Calamba Central Terminal":
                setImageResource(R.drawable.central_terminal);
                break;
            case "Calamba Megatrans Terminal":
                setImageResource(R.drawable.calamba_megatrans);
                break;
            case "Turbina Bus Terminal":
                setImageResource(R.drawable.turbina_bus);
                break;
            default:
                setImageResource(R.drawable.image_unavailable);
        }
    }
    private void goTo(Marker marker){
        Bundle extras = new Bundle();
        extras.putString("title", marker.getTitle());
        float distance = getDistanceFromLocation(marker.getPosition());
        Toast.makeText(context, "" + distance, Toast.LENGTH_SHORT).show();

        if(marker.getTitle().equals("Rizal Shrine")){
            extras.putString("place", "rizalshrine");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Touristspot"));
        } else if (marker.getTitle().equals("SM Calamba")){
            extras.putString("place", "sm_calamba");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Touristspot"));
        } else if (marker.getTitle().equals("Rizal Monument")){
            extras.putString("place", "rizal_monument");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Touristspot"));
        } else if (marker.getTitle().equals("Kastilyo ng Bucal")){
            extras.putString("place", "kastilyo_bucal");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Touristspot"));
        } else if (marker.getTitle().equals("Saint John The Baptist Church")){
            extras.putString("place", "st_john");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Touristspot"));
        } else if (marker.getTitle().equals("Calamba Baywalk")){
            extras.putString("place", "baywalk");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact());
        } else if (marker.getTitle().equals("Naruto's Statue (Anime Street)")){
            extras.putString("place", "naruto");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Touristspot"));
        } else if (marker.getTitle().equals("Palisam Leisure Park")){
            extras.putString("place", "palisam");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact());
        } else if (marker.getTitle().equals("Laurel Mansion")){
            extras.putString("place", "laurel_mansion");
            extras.putString("type", "Tourist Spot");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Touristspot"));
        } else if (marker.getTitle().equals("Expressive Food Park (NEW!)")){
            extras.putString("place", "expressive");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Wingsilicious")){
            extras.putString("place", "wingsilicious");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("El Fili Cafe")){
            extras.putString("place", "el_fili");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact());
        } else if (marker.getTitle().equals("Floating Restaurant")) {
            extras.putString("place", "floating_resto");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Boodle Fight By Hungry Boss")){
            extras.putString("place", "boodle_fight");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Kurimi Milk Tea Bar")){
            extras.putString("place", "kurimi");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Laong Laan's Restaurant")){
            extras.putString("place", "laong_laan");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Lutong Pilipino Ni Aling Ely")){
            extras.putString("place", "lutong_pilipino");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Bertito's Anyhaw Restaurant")){
            extras.putString("place", "bertito");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Mom's Garden Cafe")){
            extras.putString("place", "mommy_garden");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Onyang's Street Wings")){
            extras.putString("place", "onyangs");
            extras.putString("type", "Restaurant");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Checkpoint Mall")){
            extras.putString("place", "checkpoint");
            extras.putString("type", "Malls");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Restaurants"));
        } else if (marker.getTitle().equals("Riverview Resort")){
            extras.putString("place", "riverview");
            extras.putString("type", "Resort");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Resorts"));
        } else if (marker.getTitle().equals("CityMALL Calamba")){
            extras.putString("place", "citymall");
            extras.putString("type", "Malls");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact());
        } else if (marker.getTitle().equals("Calamba Doctor's Hospital")){
            extras.putString("place", "cdh");
            extras.putString("type", "Hospital");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Public"));
        } else if (marker.getTitle().equals("Monte Vista Hotsprings Resort")){
            extras.putString("place", "monte_vista");
            extras.putString("type", "Resort");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Resorts"));
        } else if (marker.getTitle().equals("Don Jovita's Garden Resort")){
            extras.putString("place", "jovita");
            extras.putString("type", "Resort");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Resorts"));
        } else if (marker.getTitle().equals("Calamba City Police Station")){
            extras.putString("place", "police");
            extras.putString("type", "Police Station");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Police Station"));
        } else if (marker.getTitle().equals("Makiling Highlands Resort")){
            extras.putString("place", "makiling");
            extras.putString("type", "Resort");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Resorts"));
        } else if (marker.getTitle().equals("Turbina Bus Terminal")){
            extras.putString("place", "turbina");
            extras.putString("type", "Terminals");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Resorts"));
        } else if (marker.getTitle().equals("Calamba Central Terminal")){
            extras.putString("place", "central");
            extras.putString("type", "Terminals");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Terminals"));
        } else if (marker.getTitle().equals("Wonder Islands Resort")){
            extras.putString("place", "wonder");
            extras.putString("type", "Resort");
            extras.putFloat("distance", distance);
            redirect(extras, new FragmentFact("Resorts"));
        } else {
            Toast.makeText(context, "Soon to be added", Toast.LENGTH_SHORT).show();
        }
    }
    private float getDistanceFromLocation(LatLng position) {
        SharedPreferences myloc = context.getSharedPreferences("location", Context.MODE_PRIVATE);

        final double km = 0.001;

        if(myloc != null){
            //MY LOCATION
            Location currentLoc = new Location("");
            currentLoc.setLatitude(myloc.getFloat("latitude", 0));
            currentLoc.setLongitude(myloc.getFloat("longitude", 0));

            Location destination = new Location("");
            destination.setLatitude(position.getLatitude());
            destination.setLongitude(position.getLongitude());

            float distanceMeters = (float) getDistance(currentLoc, destination);
            float distanceM = currentLoc.distanceTo(destination);
            Log.v("DISTANCE IN METERS", ""+distanceM);

            if(currentLoc.getLatitude() == 0 || currentLoc.getLongitude() == 0){
                return distanceM;
            }
            return (float) (distanceM * km);
        } else {
            return 0;
        }
    }

    public double getDistance(Location current, Location destination){

        if((current.getLatitude() == destination.getLatitude()) && (current.getLongitude() == destination.getLongitude())){
            return 0;
        } else {
            double theta = current.getLatitude() - destination.getLatitude();
            double dist = Math.sin(Math.toRadians(current.getLatitude()) * Math.sin(destination.getLatitude())
            + Math.cos(Math.toRadians(current.getLatitude())) *Math.cos(Math.toRadians(destination.getLatitude()))*Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return dist;

        }
    }
    private void redirect(Bundle bundler, Fragment fragmentInfo) {
        fragmentInfo.setArguments(bundler);
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragmentInfo).commit();
    }
    private void setImageResource(int drawable){
        Glide.with(context).load(drawable).fitCenter().into(image_window);
    }
}