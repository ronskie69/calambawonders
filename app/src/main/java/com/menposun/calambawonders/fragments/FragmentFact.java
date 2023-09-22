package com.menposun.calambawonders.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.adapter.FragmentAdapter;
import com.menposun.calambawonders.toursit_spots.TouristAttraction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FragmentFact extends Fragment implements TouristAttraction {

    //VIEWS
    TextView favoritesCount, distanceText;
    private  View vue;
    private TabLayout tabLayout;
    ViewPager2 pager;
    ImageSlider image_slider;
    FloatingActionButton backToMaps_btn, addToPlans, fabAddToFavorites, AddTovisited;
    LinearLayout options;

    //VARIABLES
    boolean clickToggle = false;
    boolean alreadyOnFaveList = false;
    boolean alreadyPlanned = false;
    boolean alreadyVisited = false;

    //BUNDLE HOLDERS
    String TOURSIT_PLACE;
    String TOURSIT_TYPE;
    String dbreference;

    //firebase
    private String DB_REFERENCE_USER = "Users";
    private String DB_REFERENCE_TOURIST_SPOT = "Touristspot";
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseUser user;
    private FirebaseAuth auth;
    int faveCounts;

    //date
    String dateNow;
    String datePlan;
    private DatePickerDialog datepicker;

    //map objects, values
    HashMap<Object, String> newvalues;

    public FragmentFact(String DB_REFERENCE_TOURIST_SPOT) {
        this.DB_REFERENCE_TOURIST_SPOT = DB_REFERENCE_TOURIST_SPOT;
    }
    public FragmentFact() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vue = inflater.inflate(R.layout.fragment_fact, container, false);
        fabAddToFavorites = vue.findViewById(R.id.fabAddToFavorites);
        favoritesCount = vue.findViewById(R.id.favoritesCount);
        backToMaps_btn = vue.findViewById(R.id.backToMaps_btn);
        addToPlans = vue.findViewById(R.id.addToPlans);
        AddTovisited = vue.findViewById(R.id.AddTovisited);
        distanceText = vue.findViewById(R.id.distanceText);
        pager = vue.findViewById(R.id.pagerx);
        tabLayout = vue.findViewById(R.id.tab_layoutx);
        image_slider = vue.findViewById(R.id.image_slider);
        options = vue.findViewById(R.id.options);


        //hide navigation status
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        backToMaps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(new MapFragment());
            }
        });

        AddTovisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alreadyVisited){
                    removeVisited(DB_REFERENCE_USER, TOURSIT_PLACE);
                } else {
                    showAddVisitedDialog();
                }
            }
        });

        addToPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alreadyPlanned){
                    removeToPlan(DB_REFERENCE_USER, TOURSIT_PLACE);
                } else {
                    ShowDatePicker();
                }
            }
        });
        fabAddToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alreadyOnFaveList){
                    removeToFavorites(DB_REFERENCE_TOURIST_SPOT, TOURSIT_PLACE);
                } else {
                    AddToFavorites();
                }
            }
        });

        //bundle from maps
        Bundle bundles = getArguments();
        checkBundle(bundles);
        //checks
        checkIfAddedToFavorites();
        checkIfPlanned();
        checkIfVisited();


        tabLayout.addTab(tabLayout.newTab().setText("Facts"));
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        pager.setUserInputEnabled(false);


        return  vue;
    }

    @Override
    public void checkBundle(Bundle bundle) {
        if(bundle != null) {

            this.TOURSIT_PLACE = bundle.getString("title");
            this.TOURSIT_TYPE = bundle.getString("type");
            this.dbreference = bundle.getString("place");
            float distance = bundle.getFloat("distance");


            loadFavoritesCount(TOURSIT_PLACE);
            setDistance(distance);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();


            //IMAGE SLIDER

            setImageSlider(this.dbreference);
            FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle(), this.DB_REFERENCE_TOURIST_SPOT, dbreference, this.TOURSIT_PLACE);
            pager.setAdapter(fragmentAdapter);
        }

        datePicker();
    }
    private void setDistance(float distance) {
        String km = String.format("%.2f", distance);
        distanceText.setText(km + " kilometers from your location.");
    }
    @Override
    public void setImageSlider(String childPath) {
        List<SlideModel> images = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(this.DB_REFERENCE_TOURIST_SPOT).child(childPath).child("images")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            if(snapshot.exists()){
                                for(DataSnapshot data: snapshot.getChildren()){
                                    images.add(new SlideModel(""+data.child("url").getValue().toString(),
                                            data.child("title").getValue().toString(), ScaleTypes.FIT));
                                }
                                image_slider.setImageList(images, ScaleTypes.FIT);
                            } else {
                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    @Override
    public void loadFavesCount(TextView textView) {

    }

    public void loadFavoritesCount(String place){
        Query loadData = FirebaseDatabase.getInstance().getReference(this.DB_REFERENCE_TOURIST_SPOT)
                .orderByChild("name").equalTo(place);
        loadData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    favoritesCount.setText(data.child("favorites").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("ERROR", error.getMessage());
            }
        });
    }
    @Override
    public void addFavorites(String dbRef, String dbPath) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Adding to your favorites");
        progressDialog.setCancelable(false);
        progressDialog.show();

        newvalues = new HashMap<>();

        newvalues.put("place", TOURSIT_PLACE);
        newvalues.put("type", TOURSIT_TYPE);

        database.getReference(DB_REFERENCE_USER)
                .child(user.getUid())
                .child("favorites")
                .child(TOURSIT_PLACE).setValue(newvalues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(getContext(), ""+ TOURSIT_PLACE + " is added to your favorites!", Toast.LENGTH_SHORT).show();
                //same here, once added, check if nag exist ang favorites record, if meron, return true hehehe
                checkIfAddedToFavorites();
                updateCount("add");
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Toast.makeText(getContext(), "Failed to add to your favorites!", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        });
    }
    @Override
    public void removeToFavorites(String dbRef, String dbPath) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Deleting to your favorites");
        progressDialog.setCancelable(false);
        progressDialog.show();
        database.getReference(dbRef)
                .child(user.getUid())
                .child("favorites").orderByChild("place").equalTo(dbPath)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            data.getRef().removeValue();
                        }
                        checkIfAddedToFavorites();
                        updateCount("minus");
                        //once removed, check if nag eexist pa ang favorites record, if wala, return false heheh
                        progressDialog.cancel();
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }
    private void datePicker(){

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                datePlan = createDate(year, month, dayOfMonth);
                addToPlan(DB_REFERENCE_USER, TOURSIT_PLACE, datePlan);
            }
        };
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = R.style.DatePicker_Dialog;

        datepicker = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
        datepicker.getDatePicker().setMinDate(System.currentTimeMillis());
    }
    private String createDate(int year, int month, int day) {
        return monthToString(month) + " " + day + ", " + year;
    }
    private String monthToString(int month){
        if(month == 1) return "January";
        if (month == 2) return "February";
        if (month == 3) return "March";
        if (month == 4) return "April";
        if (month == 5) return "May";
        if (month == 6) return "June";
        if (month == 7) return "July";
        if (month == 8) return "August";
        if (month == 9) return "September";
        if (month == 10) return "October";
        if (month == 11) return "November";
        if (month == 12) return "December";
        return "January";
    }

    @Override
    public void addToPlan(String dbRef, String dbPath, String date) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Adding to your plans to visit...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        newvalues = new HashMap<>();

        newvalues.put("place", TOURSIT_PLACE);
        newvalues.put("type", TOURSIT_TYPE);
        newvalues.put("date", date);

        database.getReference(dbRef)
                .child(user.getUid())
                .child("plans")
                .child(dbPath).setValue(newvalues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), ""+ TOURSIT_PLACE + " is added to your plans! " +
                        "You may view it from you favorites", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                progressDialog.dismiss();
                removeVisited(DB_REFERENCE_USER, TOURSIT_PLACE);
                checkIfPlanned();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to add to your plans!", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void removeToPlan(String dbRef, String dbPath) {
        database.getReference(dbRef)
                .child(user.getUid())
                .child("plans").orderByChild("place").equalTo(dbPath)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            data.getRef().removeValue();
                            //once removed, check if nag eexist pa ang favorites record, if wala, return false hehehe
                        }
                        checkIfPlanned();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    @Override
    public void addVisited(String dbRef, String dbPath) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Adding to your visited places");
        progressDialog.setCancelable(false);
        progressDialog.show();

        newvalues = new HashMap<>();
        newvalues.put("place", TOURSIT_PLACE);
        newvalues.put("type", TOURSIT_TYPE);

        database.getReference(dbRef)
                .child(user.getUid())
                .child("visited")
                .child(dbPath).setValue(newvalues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), ""+ TOURSIT_PLACE + " is added to your visited places!", Toast.LENGTH_SHORT).show();

                removeToPlan(DB_REFERENCE_USER, TOURSIT_PLACE);
                checkIfVisited();
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to add to your visited places!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void removeVisited(String dbRef, String dbPath) {
        FirebaseDatabase.getInstance().getReference(dbRef)
                .child(user.getUid())
                .child("visited").orderByChild("place").equalTo(dbPath)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            data.getRef().removeValue();
                        }
                        checkIfVisited();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    @Override
    public void checkIfAddedToFavorites() {
        database.getReference(DB_REFERENCE_USER)
                .child(user.getUid())
                .child("favorites").orderByChild("place").equalTo(TOURSIT_PLACE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            alreadyOnFaveList = true;
                            fabAddToFavorites.setImageResource(R.drawable.faves);
                        } else {
                            alreadyOnFaveList = false;
                            fabAddToFavorites.setImageResource(R.drawable.fave_unchecked);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        alreadyOnFaveList = false;
                        fabAddToFavorites.setImageResource(R.drawable.fave_unchecked);
                        Toast.makeText(getContext(), "No Favorites", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void checkIfPlanned() {
        database.getReference(DB_REFERENCE_USER)
                .child(user.getUid())
                .child("plans").orderByChild("place").equalTo(TOURSIT_PLACE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            alreadyPlanned = true;
                            addToPlans.setImageResource(R.drawable.plan_checked);
                        } else {
                            alreadyPlanned = false;
                            addToPlans.setImageResource(R.drawable.plan_unchecked);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        alreadyPlanned = false;
                        addToPlans.setImageResource(R.drawable.plan_unchecked);
                    }
                });
    }

    @Override
    public void checkIfVisited() {
        database.getReference(DB_REFERENCE_USER)
                .child(user.getUid())
                .child("visited").orderByChild("place").equalTo(TOURSIT_PLACE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            alreadyVisited = true;
                            AddTovisited.setImageResource(R.drawable.arrived_checked);
                        } else {
                            alreadyVisited = false;
                            AddTovisited.setImageResource(R.drawable.arrived_unchecked);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        alreadyVisited = false;
                        AddTovisited.setImageResource(R.drawable.arrived_unchecked);
                    }
                });
    }

    @Override
    public void updateCount(String operator) {
        if(operator.equals("add")){
            faveCounts = Integer.parseInt(favoritesCount.getText().toString()) + 1;
        }
        if(operator.equals("minus")){
            faveCounts = Integer.parseInt(favoritesCount.getText().toString()) - 1;
        }


        HashMap <String, Object> data = new HashMap<>();
        data.put("favorites", Integer.toString(faveCounts));

        FirebaseDatabase.getInstance().getReference(this.DB_REFERENCE_TOURIST_SPOT)
                .child(dbreference).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loadFavesCount(favoritesCount);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed updating favorites count!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void redirect(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment).commit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkIfAddedToFavorites();
        checkIfPlanned();
        checkIfVisited();
        loadFavesCount(favoritesCount);
    }

    private void AddToFavorites(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Add to Favorites");
        dialog.setMessage("Do you want to add " + TOURSIT_PLACE+" to your favorites?");
        dialog.setCancelable(false);
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addFavorites(DB_REFERENCE_USER, TOURSIT_PLACE);
                dialog.cancel();
                dialog.dismiss();
            }
        }).show();
    }

    private void ShowDatePicker(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Add to Plan to Visit");
        dialog.setMessage("Are you going to set a plan to visit " + TOURSIT_PLACE +"?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                datepicker.show();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

    }
    private void showAddVisitedDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(TOURSIT_PLACE);
        dialog.setMessage("Have you been there?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addVisited(DB_REFERENCE_USER, TOURSIT_PLACE);
            }
        });
        dialog.setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(user.isAnonymous()){
            options.setVisibility(View.GONE);
        }
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
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().show();
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}