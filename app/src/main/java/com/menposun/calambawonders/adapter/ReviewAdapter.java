package com.menposun.calambawonders.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.ReviewSetter;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private Context context;
    public List<ReviewSetter> reviews;
    private String review_nickname, profile_img, review_msg, review_rating, review_date;
    private List <String> favoritesList;
    public ReviewAdapter.ReviewerOnClick reviewerClick;

    public ReviewAdapter(Context context, List<ReviewSetter> reviews, ReviewerOnClick reviewerClick){
        this.reviewerClick = reviewerClick;
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(context).inflate(R.layout.review_layout, parent, false);

        return new ReviewHolder(layout);
    }


    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        //setting the data from review settter

        review_nickname = reviews.get(position).getR_nickname();
        review_rating = reviews.get(position).getR_rating();
        review_msg = reviews.get(position).getR_msg();
        profile_img = reviews.get(position).getR_profile_pic();
        String forFutureID = reviews.get(position).getR_id();
        review_date = reviews.get(position).getR_date();

        if(review_date.equals(getDateToday())){
            holder.reviewer_date.setText("Just now");
            //Tue Jul 13 08:30:36 GMT+08:00 2021
        } else {
            holder.reviewer_date.setText(review_date);
        }

        holder.reviewer_nickname.setText(review_nickname);
        holder.reviewer_msg.setText(review_msg);
        holder.ratingBar.setRating(Float.parseFloat(review_rating));

        //para di cya maclick ng tao
        holder.ratingBar.setIsIndicator(true);

        try{
            Glide.with(context).load(profile_img).fitCenter().into(holder.reviewer_img);
        } catch (NullPointerException e){
            Glide.with(context).load(R.drawable.man1).fitCenter().into(holder.reviewer_img);
            e.printStackTrace();
        }
    }
    public String getDateToday(){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String dateNow = formatDate(today);

        return dateNow;
    }
    private String formatDate(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE,  MMM dd, yyyy 'at' h:mm a");
        String newdate = sdf.format(today);

        return newdate;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public interface ReviewerOnClick {
        void onReviewerLongClickListener(View vue, int position);
    }

    class ReviewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        CircleImageView reviewer_img;
        RatingBar ratingBar;
        TextView reviewer_nickname, reviewer_msg, user_type, reviewer_date;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewer_img = itemView.findViewById(R.id.reviewer_img);
            reviewer_msg = itemView.findViewById(R.id.reviewer_msg);
            reviewer_nickname = itemView.findViewById(R.id.reviewer_nickname);
            reviewer_date = itemView.findViewById(R.id.date);

            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {
            reviewerClick.onReviewerLongClickListener(v , getAdapterPosition());
            return false;
        }
    }
}