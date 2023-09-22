package com.menposun.calambawonders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class Checkpoint extends AppCompatActivity {

    MaterialRadioButton toursit_type, resident_type;
    MaterialButton confirm_selection;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkpoint);

        layout = findViewById(R.id.constraintLayout);
        toursit_type = findViewById(R.id.type_tourist);
        resident_type = findViewById(R.id.type_resident);
        confirm_selection = findViewById(R.id.confirm_selection);

        toursit_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toursit_type.isChecked()){
                    resident_type.setChecked(false);
                }
            }
        });

        resident_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resident_type.isChecked()){
                    toursit_type.setChecked(false);
                }
            }
        });

        confirm_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toursit_type.isChecked()){
                    requestPermissions("Tourist");
                }
                if (resident_type.isChecked()){
                    requestPermissions("Resident");
                }
            }
        });

        if (savedInstanceState == null) {
            layout.setVisibility(View.INVISIBLE);

            final ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();

            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                });
            }

        }

    }

    private void circularRevealActivity() {
        int cx = layout.getRight() + getDips(44);
        int cy = layout.getBottom() + getDips(44);

        float finalRadius = Math.max(layout.getWidth(), layout.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                layout,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(1000);
        layout.setVisibility(View.VISIBLE);
        circularReveal.start();

    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = layout.getWidth() - getDips(88);
            int cy = layout.getBottom() - getDips(88);

            float finalRadius = Math.max(layout.getWidth(), layout.getHeight());
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(layout, cx, cy, finalRadius, 0);

            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    layout.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(Checkpoint.this, AccountLogin.class);
                    startActivity(intent);
                    CustomIntent.customType(Checkpoint.this,
                            "fadein-to-fadeout");
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            circularReveal.setDuration(800);
            circularReveal.start();
        }
        else {
            super.onBackPressed();
            CustomIntent.customType(Checkpoint.this,
                    "fadein-to-fadeout");
            finish();
        }
    }
    public void requestPermissions(String type){
        Dexter.withContext(getApplicationContext())
                .withPermissions(
                        Manifest.permission.READ_SMS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_NUMBERS
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Intent intent = new Intent(Checkpoint.this, SignupActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
                CustomIntent.customType(Checkpoint.this,
                        "fadein-to-fadeout");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError dexterError) {
                settingsDialog();
            }
        }).onSameThread().check();
    }
    public void settingsDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Checkpoint.this);
        dialog.setMessage("This app need these permissions to be granted for the better user" +
                "experience. You are required to enable these permissions. Do you agree with that?");
        dialog.setTitle("Permissions Denied");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                CustomIntent.customType(Checkpoint.this,
                        "fadein-to-fadeout");
            }
        }).show();
    }
}