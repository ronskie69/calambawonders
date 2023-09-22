package com.menposun.calambawonders.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.menposun.calambawonders.HomeActivity;
import com.menposun.calambawonders.LocationSetters;
import com.menposun.calambawonders.NearestLocation;
import com.menposun.calambawonders.Panoramic;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.adapter.DetailWindowAdapter;
import com.menposun.calambawonders.firebase.MyLocation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import static com.menposun.calambawonders.Notifications.CHANNEL_ID;

public class MapFragment extends Fragment implements OnMapReadyCallback, PermissionsListener, Style.OnStyleLoaded, MapboxMap.OnMarkerClickListener, MapboxMap.OnMapClickListener {


    public ArrayList<LocationSetters> lcoations = new ArrayList<>();
    List <Double> distances = new ArrayList<>();
    NearestLocation nearestLocation;
    private View vue;
    private ImageView toursit, bldgs, beach, restaurant, terminals;
    private ImageButton zoom_in, zoom_out;
    private FloatingActionButton fab,
            tilt,
            inflater_options,
            nearest_location,
            getDirection,
            map_style,
            vr_mode,
            getNavigation;

    boolean isOpenOptions = false;
    float translateY = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    Toolbar toolbar;
    List <NearestLocation> locationList = new ArrayList<>();

    //MAPBOX
    private MapView map;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionmng;
    private final String MAP_STYLE_CUSTOM = "mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7";
    private SymbolManager symbolMng;
    private BuildingPlugin buildingPlugin;
    private static String ROUTE_SOURCE_ID ="route-source-id";
    private static final String ROUTE_LAYER_ID ="route-layer-id";
    private static final String ICON_LAYER_ID ="icon-layer-id";
    private static final String ICON_SOURCE_ID ="icon-source-id";
    private static final String FAVES_ICON = "faves_icon";
    private static final String MALLS_ICON = "mall_icon";
    private static final String RESORT_ICON = "resort_icon";
    private static final String RESTO_ICON = "resto_icon";
    Location mylocation;
    boolean tilted = true;
    private DirectionsRoute currentRoute;
    private MapboxDirections directionsClient;
    private Style loadedMapStyle;
    private Point destinationPoint, originPoint;
    private String profileMode;
    private CarmenFeature resto;
    private CarmenFeature rizalShrine;
    private CarmenFeature beachx;
    private static final int REQUEST_CODE = 1;
    private String currentPlace = null;
    private NotificationManagerCompat notificationManager;
    private EditText search_loc;
    private MarkerOptions nearestMarker;

    //FIREBASE
    private FirebaseDatabase database;

    //CLICKING RESTRICTIONS
    private static long clicktime;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vue = inflater.inflate(R.layout.fragment_map, container, false);
        map = vue.findViewById(R.id.mapView);
        toursit = vue.findViewById(R.id.tourists);
        terminals = vue.findViewById(R.id.terminals);
        bldgs = vue.findViewById(R.id.bldgs);
        beach = vue.findViewById(R.id.beaches);
        restaurant = vue.findViewById(R.id.restos);
        fab = vue.findViewById(R.id.fab_detect);
        tilt = vue.findViewById(R.id.tilt);
        zoom_in = vue.findViewById(R.id.zoom_in);
        zoom_out = vue.findViewById(R.id.zoom_out);
        inflater_options = vue.findViewById(R.id.inflater_options);
        search_loc = vue.findViewById(R.id.search_loc);
        getDirection = vue.findViewById(R.id.getDirection);
        map_style = vue.findViewById(R.id.map_style);
        vr_mode = vue.findViewById(R.id.vr_mode);
        getNavigation = vue.findViewById(R.id.getNavigation);
        nearest_location = vue.findViewById(R.id.nearest_location);

        notificationManager = NotificationManagerCompat.from(getContext());


        //set onclick
        zoom_in.setOnClickListener(v -> mapboxMap.animateCamera(CameraUpdateFactory.zoomIn()));
        zoom_out.setOnClickListener(v -> mapboxMap.animateCamera(CameraUpdateFactory.zoomOut()));
        toursit.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Loading tourist spots...", Toast.LENGTH_SHORT).show();
            addToursitSpot("Tourist Spot");
        });
        terminals.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Loading terminals...", Toast.LENGTH_SHORT).show();
            addToursitSpot("Terminal");
        });
        bldgs.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Loading public malls...", Toast.LENGTH_SHORT).show();
            addToursitSpot("Mall");
        });
        beach.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Loading popular resorts...", Toast.LENGTH_SHORT).show();
            addToursitSpot("Resort");
        });
        restaurant.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Loading popular restaurants...", Toast.LENGTH_SHORT).show();
            addToursitSpot("Restaurant");
        });
        map_style.setOnClickListener(v -> setMapStyle());
        getDirection.setOnClickListener(v -> showDirections());
        vr_mode.setOnClickListener(v -> openPanorama());
        getNavigation.setOnClickListener(v -> getClosestPath());

        nearest_location.setOnClickListener(v-> {
            if(nearestMarker != null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showNearestTouristSpotLocation();
                        Toast.makeText(getContext(), "Successfully found the nearest tourist spot!",
                                Toast.LENGTH_SHORT).show();
                    }
                }, 500);
            } else {
                Toast.makeText(getContext(), "Cannot find the nearest tourist spot! Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });



        //hide get direction get navigatin, and vr fabs
        getDirection.setAlpha(0f);
        getDirection.setTranslationY(translateY);
        getNavigation.setAlpha(0f);
        getNavigation.setTranslationY(translateY);
        vr_mode.setAlpha(0f);
        vr_mode.setTranslationY(translateY);

        search_loc.setOnClickListener(v-> {
            createSuggestionsForSearchbox();

            Intent intnet = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EDAC1A"))
                            .limit(10)
                            .country("PH")
                            .addInjectedFeature(rizalShrine)
                            .addInjectedFeature(resto)
                            .addInjectedFeature(beachx)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(getActivity());

            startActivityForResult(intnet, REQUEST_CODE);

        });

        fab.setOnClickListener(v -> {
           try {
               mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylocation), 14f));
           } catch (NullPointerException e){
               e.printStackTrace();
               Toast.makeText(getContext(), "Unknown Error!", Toast.LENGTH_SHORT).show();
           }
        });
        tilt.setOnClickListener(v -> {
            float tilt;
            tilted = !tilted;
            try {

                if(tilted){
                    tilt = (float) MapboxConstants.MAXIMUM_TILT;
                } else {
                    tilt = 0f;
                }

                CameraPosition myplace = new CameraPosition.Builder()
                        .tilt(tilt)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(myplace));
            } catch (NullPointerException e){
                e.printStackTrace();
                //Toast.makeText(getContext(), "Unable to do that... might be an error or something", Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "" + mylocation, Toast.LENGTH_SHORT).show();
            }
        });
        inflater_options.setOnClickListener(v-> {
            if (isOpenOptions) {
                closeOptions();
            } else {
                openOptions();
            }
        });


        map.onSaveInstanceState(savedInstanceState);
        map.getMapAsync(this);

        if(mapboxMap == null){
            //loading first
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setMessage("Loading Calamba Wonders...");
            dialog.setCancelable(false);
            dialog.show();

           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   dialog.cancel();
               }}, 1500);
        }
        nearest_location.setVisibility(View.GONE);

        return vue;
    }

    private void openPanorama() {
        if(currentPlace != null){
            Intent intent = new Intent(getContext(), Panoramic.class);
            intent.putExtra("title", currentPlace);
            startActivity(intent);
            CustomIntent.customType(getContext(),
                    "fadein-to-fadeout");
        } else {
            Toast.makeText(getContext(), "Unable to get Panorama! Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    // get current lcoation
    @SuppressWarnings({"MissingPermission"})
    private void locationEnable(Style style) {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().recreate();
                    }
                },2000);
            }
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getContext(), style).build());
            locationComponent.getLocationEngine().getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    mylocation = result.getLastLocation();

                    MyLocation myloc = new MyLocation();
                    myloc.setMy_lat(result.getLastLocation().getLatitude());
                    myloc.setMy_long(result.getLastLocation().getLongitude());

                    SharedPreferences.Editor editor = getContext().getSharedPreferences("location", Context.MODE_PRIVATE)
                            .edit();
                    editor.putFloat("latitude", (float) result.getLastLocation().getLatitude());
                    editor.putFloat("longitude", (float) result.getLastLocation().getLongitude());
                    editor.apply();

                    showNotification("Your location has been tracked!");

                    //load tourist places
                    addToursitSpot("Tourist Spot");


                    // check if user is anonymous or else save it to database
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(!user.isAnonymous()){
                        addCurrentLocationToDatabase(mylocation);
                    }
                }
                @Override
                public void onFailure(Exception exception) {
                    restartFragmentDialog();
                }
            });
            if(!locationComponent.isLocationComponentEnabled()){
                locationComponent.setLocationComponentEnabled(true);
            }

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionmng = new PermissionsManager(MapFragment.this);
        }
    }

    private void restartFragmentDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Detecting User Location");
        dialog.setCancelable(false);
        dialog.setMessage("We are now detecting your current location.");
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog dialog2 = new ProgressDialog(getContext());
                dialog2.setMessage("Please wait...");
                dialog2.setCancelable(false);
                dialog2.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog2.cancel();
                        try {
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_layout, MapFragment.class.newInstance())
                                    .commit();
                        } catch (IllegalAccessException e) {
                            getActivity().recreate();
                            Toast.makeText(getContext(), "Something went wroing...Sorry", Toast.LENGTH_LONG).show();
                        } catch (java.lang.InstantiationException e) {
                            Toast.makeText(getContext(), "Something went wroing...Sorry", Toast.LENGTH_LONG).show();
                            getActivity().recreate();
                        }
                    }
                },1500);
            }
        }).show();
    }

    private void addCurrentLocationToDatabase(Location mylocation) {

        final HashMap<Object,Double> userLocation = new HashMap<>();
        userLocation.put("latitude", mylocation.getLatitude());
        userLocation.put("longitude", mylocation.getLongitude());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database.getReference("Users").child(user.getUid())
                .child("current_location").setValue(userLocation);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        permissionmng.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    locationEnable(loadedMapStyle);
                }
            });
        } else {
            System.exit(0);
        }
    }

    private void setMapStyle() {
        Random random = new Random();
        int randomNum = random.nextInt(8);
        switch (randomNum){
            case 0:
                mapboxMap.setStyle(Style.MAPBOX_STREETS);
                break;
            case 1:
                mapboxMap.setStyle(Style.LIGHT);break;
            case 2:
                mapboxMap.setStyle(Style.SATELLITE);break;
            case 3:
                mapboxMap.setStyle(Style.SATELLITE_STREETS);break;
            case 4:
                mapboxMap.setStyle(Style.OUTDOORS);break;
            case 5:
                mapboxMap.setStyle(Style.DARK);break;
            case 6:
                mapboxMap.setStyle(Style.TRAFFIC_DAY);break;
            case 7:
                mapboxMap.setStyle(Style.TRAFFIC_NIGHT);break;
            case 8:
                mapboxMap.setStyle(new Style.Builder().fromUri(MAP_STYLE_CUSTOM));break;
            default:
                mapboxMap.setStyle(Style.TRAFFIC_DAY);break;
        }
    }

    private void openOptions() {
        isOpenOptions = !isOpenOptions;
        inflater_options.setImageResource(R.drawable.down_icon);
        nearest_location.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
        fab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
        tilt.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
        map_style.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
    }

    private void closeOptions() {
        isOpenOptions = !isOpenOptions;
        inflater_options.setImageResource(R.drawable.up_icon);
        nearest_location.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
        fab.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
        tilt.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
        map_style.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
    }

    public void addToursitSpot(String type){
        lcoations.clear();
        FirebaseDatabase.getInstance().getReference("Locations")
                .orderByChild("type").equalTo(type)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot data : snapshot.getChildren()){
                                lcoations.add(
                                        new LocationSetters(
                                                Double.parseDouble(data.child("latitude").getValue().toString()),
                                                Double.parseDouble(data.child("longitude").getValue().toString()),
                                                data.child("info").getValue().toString(),
                                                data.child("type").getValue().toString()
                                        )
                                );
                            }
                            loadTouristAttractions();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        //instantiate custom mapboxmap as the new function mapboxmap
        if(this.mapboxMap == null){
            this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(new Style.Builder().fromUri(MAP_STYLE_CUSTOM), this::onStyleLoaded);
            //titled
            CameraPosition myplace = new CameraPosition.Builder()
                    .tilt(MapboxConstants.MAXIMUM_TILT)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(myplace));

            mapboxMap.getUiSettings().setLogoEnabled(false);
            mapboxMap.getUiSettings().setAttributionEnabled(false);

            //map gesture listeners
            mapboxMap.addOnMapClickListener(this);
            mapboxMap.setOnMarkerClickListener(this);

            MyLocation myloc = new MyLocation();
            LatLng latLng = new LatLng(myloc.getMy_lat(), myloc.getMy_long());
            mapboxMap.setInfoWindowAdapter(new DetailWindowAdapter(getContext(), latLng));
        }
    }

    private double getCalculatedDistance(double latitude, double longitude){
        if(mylocation == null) {
            return 0;
        }
        Location dest = new Location("");
        dest.setLatitude(latitude);
        dest.setLongitude(longitude);
        double distance=(mylocation.distanceTo(dest) * 0.001);
        return distance;

    }

    public void getNearestLocation(List<NearestLocation> locationList){
        Drawable nearest_icon = ContextCompat.getDrawable(getContext(), R.drawable.nearest_red);
        double minimum = locationList.size() > 0 ? locationList.get(0).getDistance() : 3.0;
        LatLng latLng = null;
        String locationName = "";


        for(int i = 0; i < locationList.size(); i++){

            if((locationList.get(i).getDistance() > minimum)
            && locationList.get(i).getDistance() <= 1.0){
                minimum = locationList.get(i).getDistance();
                latLng = new LatLng(locationList.get(i).getLatLng());
                locationName = locationList.get(i).getTitle();

            } else if(locationList.get(i).getDistance() <= minimum){
                minimum = locationList.get(i).getDistance();
                latLng = new LatLng(locationList.get(i).getLatLng());
                locationName = locationList.get(i).getTitle();
            }
        }
        nearestMarker = new MarkerOptions()
                .position(latLng)
                .setTitle(locationName)
                .icon(IconFactory.getInstance(getContext()).
                        fromBitmap(BitmapUtils.getBitmapFromDrawable(nearest_icon)))
                .setSnippet("Nearest From You");
        nearest_location.setVisibility(View.VISIBLE);
        Log.v("MINIMUM DISTANCE", "" + minimum);
    }

    public void showNearestTouristSpotLocation(){
        mapboxMap.clear();
        if(nearestMarker != null){
            mapboxMap.addMarker(nearestMarker);
        }
        nearestMarker = null;
    }

    private void loadTouristAttractions() {
        distances.clear();
        locationList.clear();
        Drawable faves = ContextCompat.getDrawable(getContext(), R.drawable.faves);
        Drawable malls = ContextCompat.getDrawable(getContext(), R.drawable.malls_icon);
        Drawable restos = ContextCompat.getDrawable(getContext(), R.drawable.restaurant_color);
        Drawable resorts = ContextCompat.getDrawable(getContext(), R.drawable.beach_colored);
        Drawable policeStation = ContextCompat.getDrawable(getContext(), R.drawable.police);
        Drawable hospitals = ContextCompat.getDrawable(getContext(), R.drawable.hospital);
        Drawable terminal = ContextCompat.getDrawable(getContext(), R.drawable.terminal);
        if(mapboxMap != null){
            mapboxMap.clear();
        }

        for(int i = 0; i < lcoations.size(); i++){
            if(lcoations.get(i).getType().equals("Mall")){
                mapboxMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()))
                                .title(lcoations.get(i).getInfo())
                                .snippet(lcoations.get(i).getType())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(malls)))
                        );
            } else if(lcoations.get(i).getType().equals("Restaurant")){
                mapboxMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()))
                                .title(lcoations.get(i).getInfo())
                                .snippet(lcoations.get(i).getType())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(restos)))
                        );
            } else if(lcoations.get(i).getType().equals("Tourist Spot")){
                mapboxMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()))
                                .title(lcoations.get(i).getInfo())
                                .snippet(lcoations.get(i).getType())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(faves)))
                        );
            } else if(lcoations.get(i).getType().equals("Resort")){
                mapboxMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()))
                                .title(lcoations.get(i).getInfo())
                                .snippet(lcoations.get(i).getType())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(resorts)))
                        );
            } else if(lcoations.get(i).getType().equals("Police Station")){
                mapboxMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()))
                                .title(lcoations.get(i).getInfo())
                                .snippet(lcoations.get(i).getType())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(policeStation)))
                        );
            } else if(lcoations.get(i).getType().equals("Hospital")){
                mapboxMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()))
                                .title(lcoations.get(i).getInfo())
                                .snippet(lcoations.get(i).getType())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(hospitals)))
                        );
            } else if(lcoations.get(i).getType().equals("Terminal")){
                mapboxMap.addMarker
                        (new MarkerOptions()
                                .position(new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()))
                                .title(lcoations.get(i).getInfo())
                                .snippet(lcoations.get(i).getType())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(terminal)))
                        );
            }

            locationList.add(
                    new NearestLocation(lcoations.get(i).getInfo(),
                            lcoations.get(i).getType(),
                            new LatLng(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude()),
                            getCalculatedDistance(lcoations.get(i).getLatitude(), lcoations.get(i).getLongitude())));
        }
        getNearestLocation(locationList);
        locationList.clear();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        //check if fulloy loaded the new styles added or if styles are loading
        if(mapboxMap.getStyle().isFullyLoaded()){
            //get loaded mapbox style once fully loaded
            //once new styles added
            loadedMapStyle = mapboxMap.getStyle();

            // then reset if sources and layers exist for new/future sources and layer make their way
            if(loadedMapStyle.getSources().size() > 0 || loadedMapStyle.getLayers().size() > 0){
                clearSources();
            }

            //hide the getdirection and vr fab once again...
            getNavigation.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
            vr_mode.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
            getDirection.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
        }

        return false;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        //check if fully loaded the new styles added or styles are loading
        if(mapboxMap.getStyle().isFullyLoaded()){
            //get loaded mapbox style once fully loaded
            //once new styles added
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition()), 14f));
            loadedMapStyle = mapboxMap.getStyle();

            // then reset if sources and layers exist for new/future sources and layer make their way
            if(loadedMapStyle.getSources().size() > 0 && loadedMapStyle.getLayers().size() > 0){
                clearSources();
            }

            destinationPoint = Point.fromLngLat(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
            originPoint = Point.fromLngLat(mylocation.getLongitude(), mylocation.getLatitude());
            //show get navigation fab
            getNavigation.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
            //show get direction and navigate fab
            getDirection.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
            //set current place for panorama
            currentPlace = marker.getTitle().toString();
            //show vr fab
            vr_mode.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
            setSource(marker);
            setLayers();

            //if marker has no snippet, then delete
            if(TextUtils.isEmpty(marker.getSnippet())){
                mapboxMap.removeMarker(marker);
            }

        } else {
            Toast.makeText(getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().recreate();
                }
            }, 2000);
        }

        return false;
    }
    private void clearSources() {
        loadedMapStyle.removeLayer(ROUTE_LAYER_ID);
        loadedMapStyle.removeLayer(ROUTE_SOURCE_ID);
        loadedMapStyle.removeSource(ROUTE_SOURCE_ID);
        loadedMapStyle.removeSource(ICON_SOURCE_ID);
    }

    private void setSource(Marker marker) {

        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource geoJsonSource = new GeoJsonSource(ICON_SOURCE_ID
                , FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(mylocation.getLongitude(), mylocation.getLatitude())),
                Feature.fromGeometry(Point.fromLngLat(marker.getPosition().getLongitude(), marker.getPosition().getLatitude())),
        }));

        loadedMapStyle.addSource(geoJsonSource);
    }

    private void setLayers() {
        LineLayer routeLine = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        routeLine.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#3366ff"))
        );
        loadedMapStyle.addLayer(routeLine);
    }

    public void createSuggestionsForSearchbox(){
        rizalShrine = CarmenFeature.builder()
                .text("Museo ni Rizal")
                .geometry(Point.fromLngLat(121.1668, 14.2137))
                .placeName("F. Mercado St, Calamba, 4027 Laguna")
                .id("tspot-1")
                .properties(new JsonObject())
                .build();

        beachx = CarmenFeature.builder()
                .text("Riverview Resort")
                .geometry(Point.fromLngLat(121.1536111, 14.2086111))
                .placeName("365 Manila S Rd, Calamba, 4027 Laguna")
                .id("tspot-2")
                .properties(new JsonObject())
                .build();
        resto = CarmenFeature.builder()
                .text("El Fili Cafe")
                .geometry(Point.fromLngLat(121.1660849, 14.213472))
                .placeName("215-219 Jose P. Rizal Street Calamba, 4027 Laguna")
                .id("tspot-3")
                .properties(new JsonObject())
                .build();
    }


    private void showDirections() {

        FloatingActionButton fabWalking, fabDriving, fabCycling, fabTraffic;

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.profile_transpo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fabWalking = dialog.findViewById(R.id.fabWalking);
        fabDriving = dialog.findViewById(R.id.fabDriving);
        fabCycling = dialog.findViewById(R.id.fabCycling);
        fabTraffic = dialog.findViewById(R.id.fabTraffic);

        fabWalking.setOnClickListener(v-> {
            profileMode = DirectionsCriteria.PROFILE_WALKING;
            dialog.dismiss();
            getRoute();
        });
        fabCycling.setOnClickListener(v-> {
            profileMode = DirectionsCriteria.PROFILE_CYCLING;
            dialog.dismiss();
            getRoute();
        });
        fabDriving.setOnClickListener(v-> {
            profileMode = DirectionsCriteria.PROFILE_DRIVING;
            dialog.dismiss();
            getRoute();
        });
        fabTraffic.setOnClickListener(v-> {
            profileMode = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC;
            dialog.dismiss();
            getRoute();
        });

        dialog.show();

    }

    private void getRoute() {

        Toast.makeText(getContext(), "Loading directions...Please wait!", Toast.LENGTH_LONG).show();

        directionsClient = MapboxDirections.builder()
                .origin(originPoint)
                .destination(destinationPoint)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .steps(true)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        directionsClient.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                //Toast.makeText(getContext(), "" + response.code(), Toast.LENGTH_SHORT).show();

                if(response.body() != null){
                    currentRoute = response.body().routes().get(0);
                    //Toast.makeText(getContext(), currentRoute.distance().toString(), Toast.LENGTH_LONG).show();
                    showNotification("Closest path has been tracked!");

                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            GeoJsonSource newsource = style.getSourceAs(ROUTE_SOURCE_ID);

                            if(newsource != null) {
                                newsource.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                } else if (response.body().routes().size() == 0){
                    Toast.makeText(getContext(), "No routes fdound", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unknown errror! Soon to fix...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Internet problem, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getClosestPath(){
        if(originPoint == null || destinationPoint == null){
            Toast.makeText(getContext(), "No location points set", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Loading navigation...Please wait!", Toast.LENGTH_LONG).show();

        NavigationRoute.builder(getContext())
                .accessToken(getResources().getString(R.string.mapbox_access_token))
                .origin(originPoint)
                .destination(destinationPoint)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body() == null){
                            Toast.makeText(getContext(), "No routes found! Check your access key.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (response.body().routes().size() < 1){
                            Toast.makeText(getContext(), "No routes found! Check internet connection.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DirectionsRoute routes = response.body().routes().get(0);

                        NavigationLauncherOptions launcher = NavigationLauncherOptions.builder()
                                .directionsRoute(routes)
                                .waynameChipEnabled(true)
                                .shouldSimulateRoute(true)
                                .build();
                        
                        NavigationLauncher.startNavigation(getActivity(), launcher);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {

        this.loadedMapStyle = style;
        locationEnable(loadedMapStyle);

        buildingPlugin = new BuildingPlugin(map, mapboxMap, loadedMapStyle);
        buildingPlugin.setVisibility(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            CarmenFeature searchedPlace = PlaceAutocomplete.getPlace(data);
            Drawable target = ContextCompat.getDrawable(getContext(), R.drawable.target);
            Toast.makeText(getContext(), "Searching for this location...", Toast.LENGTH_SHORT).show();


            if(loadedMapStyle.isFullyLoaded()){
                MarkerOptions searched = new MarkerOptions()
                        .position(new LatLng(((Point) searchedPlace.geometry()).latitude(), ((Point) searchedPlace.geometry()).longitude()))
                        .title(searchedPlace.placeName())
                        .icon(IconFactory.getInstance(getContext()).fromBitmap(BitmapUtils.getBitmapFromDrawable(target)));

                mapboxMap.addMarker(searched);

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .zoom(14f)
                                .target(new LatLng(((Point) searchedPlace.geometry()).latitude(), ((Point) searchedPlace.geometry()).longitude()))
                                .build()), 3000);
            }
        } else {
            //Toast.makeText(getContext(), "Failed! Make sure you are connected to internet or enter a valid place.", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(String message){
        String channel = "channel101";
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        NotificationChannel notificationChannel = new NotificationChannel(channel, "name", NotificationManager.IMPORTANCE_DEFAULT);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, 0);
        Notification notification = new NotificationCompat.Builder(getContext(), channel)
                .setSmallIcon(R.drawable.cw_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.theme))
                .setChannelId(channel)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .build();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1, notification);
    }

    @Override
    public void onStart() {
        super.onStart();
        map.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        map.onStop();
    }

    @Override

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }
}