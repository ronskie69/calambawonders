package com.menposun.calambawonders.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.ReviewSetter;
import com.menposun.calambawonders.adapter.DraggableFloatingActionButton;
import com.menposun.calambawonders.adapter.ReviewAdapter;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Reviews extends Fragment {

    //views
    private DraggableFloatingActionButton fabAddReview, fabDeleteRevew;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private TextView no_reviews;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refresher;
    private RatingBar ratingBar;
    private EditText review_msg;
    private MaterialButton send_review, closeReviewBox;
    private ConstraintLayout review_box;
    private FrameLayout review_layout;
    //firebase
    private FirebaseDatabase database;
    private DatabaseReference tourist_spot_reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String ratingValue;
    private String reviewMsg;
    boolean isLoaded = false;
    boolean reversed = false;
    private HashMap<Object, String> newvalues;
    //strings
    private String DB_REFERENCE;
    private String TOURIST_PLACE;
    //anims
    float translateY = -200f;
    private OvershootInterpolator interpolator = new OvershootInterpolator();

    //current user
    String user_nickname, user_profile_pic, user_type, uuid;
    float totalRatings = 0.0f;

    //reviews
    String nickname, type, profile_pic, review, uid, date, rating;

    //constructors/setters
    private List<ReviewSetter> tourisr_reviews;
    private ReviewAdapter reviewAdapter;
    private ReviewAdapter.ReviewerOnClick reviewerOnClick;


    private Reviews(){}// Required empty public constructor
    public Reviews(String dbReference, String dbPath) {
        this.DB_REFERENCE = dbReference;
        this.TOURIST_PLACE = dbPath;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        tourist_spot_reference = database.getReference(DB_REFERENCE);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        dialog = new ProgressDialog(getActivity());
        if(user.isAnonymous()){
            fabAddReview.setVisibility(View.GONE);
            fabDeleteRevew.setVisibility(View.GONE);
        }

        //load profile
        getReviews();

    }

    public void animateOptionsClose(){
        //enable fullscreen
        fabAddReview.setImageResource(R.drawable.add_comment);
        review_box.setVisibility(View.GONE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        review_box.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
    }
    public void animateOptionsOpen(){
        //temporarily disable full screen
        fabAddReview.setImageResource(R.drawable.clear);
        review_box.setVisibility(View.VISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        review_box.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reviews, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        fabAddReview = root.findViewById(R.id.add_review);
        fabDeleteRevew = root.findViewById(R.id.delete_review);
        no_reviews = root.findViewById(R.id.no_reviews);
        refresher = root.findViewById(R.id.refresher);
        ratingBar = root.findViewById(R.id.rating);
        review_msg = root.findViewById(R.id.review_msg);
        send_review = root.findViewById(R.id.send_review);
        review_box = root.findViewById(R.id.review_box);
        review_layout = root.findViewById(R.id.review_layout);
        closeReviewBox = root.findViewById(R.id.closeReviewBox);

        closeReviewBox.setOnClickListener(v->animateOptionsClose());



        fabAddReview.setCustomClickListener(new DraggableFloatingActionButton.CustomClickListener() {
            @Override
            public void onClick(View view) {
                if(review_box.getVisibility() == View.GONE){
                    animateOptionsOpen();
                } else {
                    animateOptionsClose();
                }
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            recyclerView.smoothScrollToPosition(reviewAdapter.getItemCount()-1);
                        } catch (Exception e){
                            refresher.scrollTo(0, refresher.getBottom());
                        }
                    }
                }, 500);
            }
        });

        fabDeleteRevew.setCustomClickListener(new DraggableFloatingActionButton.CustomClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogRemove = new AlertDialog.Builder(getContext());
                dialogRemove.setTitle("Remove Review");
                dialogRemove.setMessage("Are you going to remove your review? This cannot be undone.");
                //set uncancelable para di makansel ng user
                dialogRemove.setCancelable(false);
                dialogRemove.setPositiveButton("Yes, Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReview();
                        dialog.cancel();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(ratingBar.getRating() == 1.0){
                    ratingValue ="1.0";
                } else if(ratingBar.getRating() == 1.5){
                    ratingValue ="1.5";
                } else if(ratingBar.getRating() == 2.0){
                    ratingValue ="2.0";
                } else if(ratingBar.getRating() == 2.5){
                    ratingValue ="2.5";
                } else if(ratingBar.getRating() == 3.0){
                    ratingValue ="3.0";
                } else if(ratingBar.getRating() == 3.5){
                    ratingValue ="3.5";
                } else if(ratingBar.getRating() == 4.0){
                    ratingValue ="4.0";
                } else if(ratingBar.getRating() == 4.5){
                    ratingValue ="4.5";
                } else if(ratingBar.getRating() == 5.0){
                    ratingValue ="5.0";
                }
            }
        });

        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getReviews();

                refresher.setRefreshing(false);
            }
        });

        send_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(review_msg.getText().toString().length() > 300){
                    createSnackbar("Your review must not exceed to 400 characters!");
                    return;
                }
                sendReview(review_msg.getText().toString(), ratingValue);
            }
        });

        tourisr_reviews = new ArrayList<>();

        reviewerOnClick = new ReviewAdapter.ReviewerOnClick() {
            @Override
            public void onReviewerLongClickListener(View vue, int position) {
                String reviewer_name = tourisr_reviews.get(position).getR_nickname();
                ShowDialog(reviewer_name);
            }
        };

        return root;
    }
    private void ShowDialog(String reviewer_name) {
        String options [] = { "View Profile", "Report"};
        //dbReference.child("profile_pic").setValue(img);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        ViewProfile(reviewer_name);
                        break;
                    case 1:
                        createSnackbar("You have reported "+reviewer_name+"! Please wait for an admin to respond. This will take a few minutes");
                        break;

                }
            }
        }).create().show();
    }

    private void ViewProfile(String reviewer_name) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetCustom);
        View sheet = getLayoutInflater().inflate(R.layout.bottomsheet_design, null);

        bottomSheetDialog.setContentView(sheet);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout visited_list_holder = bottomSheetDialog.findViewById(R.id.visited_list_holder);
        CircleImageView view_profile_pic = bottomSheetDialog.findViewById(R.id.view_profile_pic);
        TextView profile_name = bottomSheetDialog.findViewById(R.id.profile_name);
        TextView visited_places_text = bottomSheetDialog.findViewById(R.id.visited_places_text);
        ListView visited_list = bottomSheetDialog.findViewById(R.id.visited_list);
        TextView about_profile = bottomSheetDialog.findViewById(R.id.about_profile);
        Button view_visited = bottomSheetDialog.findViewById(R.id.view_visited);
        MaterialButton closeBottomSheet = bottomSheetDialog.findViewById(R.id.closeBottomSheet);

        final ArrayList <String> visits = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("nickname")
                .equalTo(reviewer_name)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            profile_name.setText(data.child("nickname").getValue().toString());
                            about_profile.setText(data.child("bio").getValue().toString());

                            String image = data.child("profile_pic").getValue().toString();

                            if(data.child("visited").exists()){
                                for(DataSnapshot datax : data.child("visited").getChildren()){
                                    String place = datax.child("place").getValue().toString();
                                    String type = datax.child("type").getValue().toString();

                                    visits.add(place + " (" + type + ")");
                                }

                                ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.text_layout, visits);
                                arrayAdapter.notifyDataSetChanged();

                                visited_list.setAdapter(arrayAdapter);
                            } else {
                                visits.add("No visited places yet.");
                            }
                            try{
                                Picasso.get()
                                        .load(image)
                                        .fit()
                                        .centerCrop()
                                        .into(view_profile_pic);
                            } catch (NullPointerException e){
                                Picasso.get()
                                        .load(R.drawable.man1)
                                        .fit()
                                        .centerCrop()
                                        .into(view_profile_pic);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        view_visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visited_list_holder.getVisibility() == View.GONE){
                    visited_list_holder.setVisibility(View.VISIBLE);
                    view_visited.setBackgroundResource(R.drawable.up_icon);
                } else {
                    visited_list_holder.setVisibility(View.GONE);
                    view_visited.setBackgroundResource(R.drawable.down_icon);
                }
            }
        });
        visited_places_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visited_list_holder.getVisibility() == View.GONE){
                    visited_list_holder.setVisibility(View.VISIBLE);
                    view_visited.setBackgroundResource(R.drawable.up_icon);
                } else {
                    visited_list_holder.setVisibility(View.GONE);
                    view_visited.setBackgroundResource(R.drawable.down_icon);
                }
            }
        });
        closeBottomSheet.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }



    public void sendReview(String value, String ratingValue){
        if(TextUtils.isEmpty(ratingValue) || TextUtils.equals(ratingValue, "")){
            ratingValue = "1.0";
        }
        dialog.setMessage("Adding you review...");
        dialog.setCancelable(false);
        dialog.show();

        newvalues = new HashMap<>();

        DatabaseReference dbReference = database.getReference("Users");
        Query load = dbReference.orderByChild("email").equalTo(user.getEmail());
        String rating = ratingValue;
        load.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    Calendar calendar = Calendar.getInstance();
                    Date today = calendar.getTime();

                    //apply user's data to hasmap
                    newvalues.put("review", value);
                    newvalues.put("rating", rating);
                    newvalues.put("nickname", ds.child("nickname").getValue().toString());
                    newvalues.put("type", ds.child("type").getValue().toString());
                    newvalues.put("profile_pic", ds.child("profile_pic").getValue().toString());
                    newvalues.put("UUID", user.getUid());
                    newvalues.put("date", createTimeFormat(today));

                    //send info as well as the review to database
                    tourist_spot_reference.child(TOURIST_PLACE)
                            .child("reviews")
                            .child(user.getUid())
                            .setValue(newvalues).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            animateOptionsClose();
                            createSnackbar("Your review has been added!");
                            //scroll to the message sent
                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            layoutManager.scrollToPositionWithOffset(0, 0);
                            dialog.cancel();
                            dialog.dismiss();


                            animateOptionsClose();
                            review_msg.getText().clear();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to add your review!", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    getReviews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showThanks(){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_total_reviews);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView totalRating = dialog.findViewById(R.id.totalRatings);
        RatingBar total_ratingbar = dialog.findViewById(R.id.total_ratingbar);
        total_ratingbar.setRating(Float.valueOf(String.format("%.1f", totalRatings)));
        totalRating.setText("Total Ratings : "+ String.format("%.1f", totalRatings));
        dialog.show();
    }


    private String createTimeFormat(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE,  MMM dd, yyyy 'at' h:mm a");
        String newdate = sdf.format(today);

        return newdate;
    }

    public void deleteReview(){
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Removing message...");
        progressDialog.show();
        String UUID = user.getUid();
        tourist_spot_reference.child(TOURIST_PLACE).child("reviews").orderByChild("UUID").equalTo(UUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot data : snapshot.getChildren()){
                                data.getRef().removeValue();

                            }
                            progressDialog.cancel();
                            progressDialog.dismiss();
                            getReviews();
                        } else {
                            progressDialog.cancel();
                            progressDialog.dismiss();
                            createSnackbar("You have no review in here!");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getContext(), error.toString(),
                                Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        progressDialog.dismiss();
                    }
                });
    }


    public void getReviews(){
        tourisr_reviews.clear();
        tourist_spot_reference.child(TOURIST_PLACE).child("reviews")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        float ratings = 0f;
                        for (DataSnapshot data : snapshot.getChildren()) {

                            no_reviews.setVisibility(View.INVISIBLE);

                            tourisr_reviews.add(new ReviewSetter(
                                    data.child("nickname").getValue().toString(),
                                    data.child("rating").getValue().toString(),
                                    data.child("review").getValue().toString(),
                                    data.child("profile_pic").getValue().toString(),
                                    data.child("UUID").getValue().toString(),
                                    data.child("date").getValue().toString()));

                            ratings = ratings + Float.parseFloat(data.child("rating").getValue().toString());
                        }

                        Collections.reverse(tourisr_reviews);
                        reviewAdapter = new ReviewAdapter(getContext(), tourisr_reviews, reviewerOnClick);
                        //reviewAdapter.setHasStableIds(true);
                        recyclerView.setAdapter(reviewAdapter);

                        reversed = true;

                        setRating(ratings, tourisr_reviews.size());
                        if (!isLoaded && tourisr_reviews.size() != 0) {
                            showThanks();
                        }
                        isLoaded = true;
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        no_reviews.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void createSnackbar(String message) {
        Snackbar.make(review_layout, message, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setTextColor(Color.BLACK)
                .show();
    }

    private float getReviewSum(float rating) {
        float ratingSum = rating;
        float sums[] = new float[0];
        float total = 0;

        for(int i = 0; i < sums.length; i++){
            sums[i] = ratingSum;
            total = total + sums[i];
        }
        return total;
    }

    public void setRating(float ratings, int total_reviews){
        float total_rating = 0.0f;
        final int maxStars = 5;
        float sum_of_reviewers_and_maxStars = total_reviews * maxStars;

        total_rating = (ratings * maxStars) / sum_of_reviewers_and_maxStars;

        totalRatings = total_rating;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        database.goOnline();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(review_box.getVisibility() == View.VISIBLE){
            animateOptionsClose();
        }
        database.goOffline();
    }
}