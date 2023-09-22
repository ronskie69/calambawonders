package com.menposun.calambawonders.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menposun.calambawonders.R;

import org.jetbrains.annotations.NotNull;


public class History extends Fragment {

    private View vue;
    private String text_fact;
    private String dbRef;
    private TextView fact_text;
    private String refName;

    private History(){}  // Required empty public constructor

    public History(String dbRef, String fact, String refName) {
        this.dbRef = dbRef;
        this.text_fact = fact;
        this.refName = refName;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vue = inflater.inflate(R.layout.fragment_history, container, false);
        fact_text = vue.findViewById(R.id.fact_text);
        FirebaseDatabase.getInstance().getReference(this.dbRef)
                .orderByChild("name").equalTo(this.refName)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            fact_text.setText(Html.fromHtml(data.child("details").getValue().toString(), Html.FROM_HTML_MODE_LEGACY));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("ERROR", error.getMessage());
                    }
                });
        return vue;
    }
}