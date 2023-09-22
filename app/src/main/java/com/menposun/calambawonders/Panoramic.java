package com.menposun.calambawonders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;

import maes.tech.intentanim.CustomIntent;

public class Panoramic extends AppCompatActivity {

    private VrPanoramaView panoramicView;
    private String currentPlace = null;
    private FloatingActionButton fabBack;
    private Bitmap bitmap;
    private FirebaseStorage firebaseStorage;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panoramic);
        fabBack = findViewById(R.id.fabBack);
        panoramicView = (VrPanoramaView) findViewById(R.id.panoramicView);
        layout = findViewById(R.id.layout);

        fabBack.setOnClickListener(v-> this.onBackPressed());

        panoramicView.setInfoButtonEnabled(false);
        panoramicView.setStereoModeButtonEnabled(false);

        //firebase
        firebaseStorage = FirebaseStorage.getInstance();
        Bundle bundle = getIntent().getExtras();
        currentPlace = bundle.getString("title");

        loadPhotoSphere(currentPlace);

    }


    private void loadPhotoSphere(String place){
        Snackbar.make(layout, "Loading panorama, please wait...", Snackbar.LENGTH_SHORT)
                .setTextColor(Color.BLACK)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show();
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        options.inputType = VrPanoramaView.Options.TYPE_MONO;

        StorageReference reference = firebaseStorage.getReference("360")
                .child(place +".jpg");

        try {
            final File localFile = File.createTempFile("Images", "jpg");
            reference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            panoramicView.loadImageFromBitmap(bitmap, options);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    createSnackbar("I'm sorry, panorama in this area doesn't exist.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }
            });

        } catch (Exception e){
            Log.e("Panoramic", e.getMessage());
            createSnackbar("Can't laod panorama! Your internet is slow.");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
        }

    }

    private void createSnackbar(String mesage) {
        final Snackbar snackbar = Snackbar.make(layout, mesage, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setActionTextColor(Color.BLACK)
                .setTextColor(Color.BLACK);

        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                Panoramic.this.onBackPressed();
            }
        }).show();
    }

    private void testPanorama(String place){
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        options.inputType = VrPanoramaView.Options.TYPE_MONO;
        InputStream inputStreams = null;
        AssetManager asset = getAssets();

        try {
            inputStreams = asset.open(place +".jpg");
            panoramicView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStreams), options);
            Toast.makeText(Panoramic.this, "loaded", Toast.LENGTH_SHORT).show();
            inputStreams.close();
        } catch (Exception e) {
            Log.e("LandingFragment", e.getMessage());
            Toast.makeText(Panoramic.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        CustomIntent.customType(Panoramic.this,
                "fadein-to-fadeout");
    }

    @Override
    public void onResume() {
        super.onResume();
        panoramicView.resumeRendering();
    }

    @Override
    public void onPause() {
        super.onPause();
        panoramicView.pauseRendering();
    }

    @Override
    public void onDestroy() {
        panoramicView.shutdown();
        super.onDestroy();
    }
}