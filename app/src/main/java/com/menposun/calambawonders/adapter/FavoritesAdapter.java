package com.menposun.calambawonders.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.menposun.calambawonders.FavoriteSetters;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.ReviewSetter;
import com.menposun.calambawonders.fragments.AccountFragment;
import com.menposun.calambawonders.fragments.WishlistFragment;
import com.squareup.picasso.Picasso;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavesItemHolder> {

    Context context;
    List<FavoriteSetters> faves;
    String tourist_place, tourist_type;
    FirebaseUser user;

    public FavoritesAdapter(Context context, List<FavoriteSetters> faves){
        this.context = context;
        this.faves = faves;
    }

    @NonNull
    @Override
    public FavesItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //inflate tge layout of the reviews

        View layout = LayoutInflater.from(context).inflate(R.layout.faves_layout, parent, false);
        user = FirebaseAuth.getInstance().getCurrentUser();


        return new FavesItemHolder(layout);
    }

    @Override
    public void onBindViewHolder(FavesItemHolder holder, int position) {
        //setting the data from review settter

        tourist_place = faves.get(position).getPlace_title();
        tourist_type = faves.get(position).getPlace_type();

        holder.tourist_place.setText(tourist_place);
        holder.toursit_type.setText(tourist_type);

    }

    @Override
    public int getItemCount() {
        return faves.size();
    }

    class FavesItemHolder extends RecyclerView.ViewHolder {

        TextView toursit_type, tourist_place;

        public FavesItemHolder(View itemView) {
            super(itemView);
            tourist_place = itemView.findViewById(R.id.tourist_place);
            toursit_type = itemView.findViewById(R.id.toursit_type);
        }
    }
}