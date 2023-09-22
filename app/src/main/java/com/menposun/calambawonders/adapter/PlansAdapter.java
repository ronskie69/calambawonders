package com.menposun.calambawonders.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menposun.calambawonders.PlanSetters;
import com.menposun.calambawonders.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlanItemHolder> {

    Context context;
    List<PlanSetters> plans;
    String tourist_place, tourist_type, date_planned;
    FirebaseUser user;

    public PlansAdapter(Context context, List<PlanSetters> plans){
        this.context = context;
        this.plans = plans;
    }

    @NonNull
    @Override
    public PlanItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //inflate tge layout of the reviews

        View layout = LayoutInflater.from(context).inflate(R.layout.plans_layout, parent, false);
        user = FirebaseAuth.getInstance().getCurrentUser();


        return new PlanItemHolder(layout);
    }

    @Override
    public void onBindViewHolder(PlanItemHolder holder, int position) {
        //setting the data from review settter

        tourist_place = plans.get(position).getPlace_title();
        tourist_type = plans.get(position).getPlace_type();
        date_planned = plans.get(position).getDate_plan();

        holder.tourist_place.setText(tourist_place);
        holder.toursit_type.setText(tourist_type);
        holder.date_planned.setText("Plans to visit on " + date_planned);

    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    class PlanItemHolder extends RecyclerView.ViewHolder {

        TextView toursit_type, tourist_place, date_planned;
        FloatingActionButton fabEditItemFavorite;
        String dbReference;

        public PlanItemHolder(View itemView) {
            super(itemView);
            tourist_place = itemView.findViewById(R.id.tourist_place);
            toursit_type = itemView.findViewById(R.id.toursit_type);
            date_planned = itemView.findViewById(R.id.date_planned);
        }
    }
}