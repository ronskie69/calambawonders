package com.menposun.calambawonders.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.menposun.calambawonders.PlanSetters;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.adapter.PlansAdapter;


import java.util.ArrayList;
import java.util.List;

public class PlanningToVisit extends Fragment {

    //vuews
    private RecyclerView recyclerView;
    private TextView isEmptyFavorites;
    private MaterialButton btnAddplace;
    private Animation anim, anim2;


    private List<PlanSetters> plans;
    private PlansAdapter plansAdapter;
    private FirebaseUser user;

    private View vue;
    private SwipeRefreshLayout refresh_list;
    public PlanningToVisit() {
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
        vue = inflater.inflate(R.layout.fragment_wishlists, container, false);
        isEmptyFavorites = vue.findViewById(R.id.no_data);
        recyclerView = vue.findViewById(R.id.recycler_view_wishlist);
        btnAddplace = vue.findViewById(R.id.btnAddplace);
        refresh_list = vue.findViewById(R.id.refresh_list);

        btnAddplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(new MapFragment());
            }
        });
        refresh_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchPlans();
                refresh_list.setRefreshing(false);
            }
        });

        return  vue;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        anim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
        isEmptyFavorites.startAnimation(anim2);

        //get favorites list
        FetchPlans();
    }

    public void FetchPlans(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        plans = new ArrayList<>();
        Query loadFaves =  FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("UUID").equalTo(user.getUid());
        loadFaves.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot datas : snapshot.getChildren()){
                    if(datas.child("plans").exists()){
                        for(DataSnapshot dataFromPlans : datas.child("plans").getChildren()){
                            plans.add(new PlanSetters
                                    (dataFromPlans.child("place").getValue().toString(),
                                    dataFromPlans.child("type").getValue().toString(),
                                            dataFromPlans.child("date").getValue().toString()));

                            //set gone kc may data na
                            isEmptyFavorites.setVisibility(View.GONE);
                            btnAddplace.setVisibility(View.GONE);
                        }
                    } else {
                        //set visible kung walang data pa
                        isEmptyFavorites.setVisibility(View.VISIBLE);
                        btnAddplace.setVisibility(View.VISIBLE);
                    }
                }
                plansAdapter = new PlansAdapter(getContext(), plans);
                plansAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(plansAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //set visible kung walang data pa
                //same here if may error sa database kase wala namng mafefetch
                isEmptyFavorites.setVisibility(View.VISIBLE);
                btnAddplace.setVisibility(View.VISIBLE);
            }
        });
        Toast.makeText(getContext(), "Loaded", Toast.LENGTH_SHORT).show();
    }
    private void redirect(Fragment frag){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, frag).commit();
    }
}