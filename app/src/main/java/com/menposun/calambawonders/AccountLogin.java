package com.menposun.calambawonders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import maes.tech.intentanim.CustomIntent;
import okhttp3.internal.Version;;

public class AccountLogin extends AppCompatActivity {

    MotionLayout motionLayout;
    MaterialButton btnLogin, btnBack;
    TextView signup_text;
    EditText emailAdd, pass;
    TextInputLayout layout_email_address, layout_pass;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Animation slide_left;

    String SAVE_EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_login);

        if(!isConnectedToInternet() || !ifTheresInternet()){
            showNoInternetDialog();
        }


        motionLayout = findViewById(R.id.motionLayout);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        signup_text = findViewById(R.id.signup_text);
        emailAdd = findViewById(R.id.email_address);
        pass = findViewById(R.id.password);
        layout_email_address = findViewById(R.id.layout_email_address);
        layout_pass = findViewById(R.id.layout_pass);

        //btnBack.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            SAVE_EMAIL = bundle.getString("email");
            emailAdd.setText(bundle.getString("email"));
            btnBack.setVisibility(View.VISIBLE);
        }
        if(savedInstanceState != null){
            String email = savedInstanceState.getString("email");
            emailAdd.setText(email);
        }

        //animations
        slide_left = AnimationUtils.loadAnimation(this, R.anim.right_to_left);


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setVisibility(View.GONE);
                btnBack.setText("Logging in as guest...");
                btnBack.setBackgroundColor(getColor(R.color.theme));
                btnBack.setEnabled(false);
                if(ifTheresInternet()){
                    firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(AccountLogin.this, HomeActivity.class);
                                startActivity(intent);
                                CustomIntent.customType(AccountLogin.this,
                                        "fadein-to-fadeout");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                        }
                    });
                } else {
                    createSnackbar("No internet connection!");
                }
            }
        });


        emailAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    layout_email_address.setError("Please don't leave fields empty!");
                } else {
                    layout_email_address.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    layout_pass.setError("Please don't leave fields empty!");
                } else {
                    layout_pass.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailAdd.getText().toString();
                String password = pass.getText().toString();

                if(!isConnectedToInternet() || !ifTheresInternet()){
                    showNoInternetDialog();
                } else {
                    if(TextUtils.isEmpty(email)){
                        layout_email_address.setError("Your email address is empty! It is required.");
                    } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
                        layout_email_address.setError("Please add valid email adress. (ex. karl@gmail.com)");
                    } else if (TextUtils.isEmpty(password)){
                        layout_pass.setError("Your password is empty! It is required.");
                    }  else {
                        LoginAccount(email, password);
                        btnLogin.setText("Logging in...");
                        signup_text.animate().alpha(0f).setDuration(500).start();
                        btnLogin.setEnabled(false);
                        btnBack.setEnabled(false);
                    }
                }
            }
        });
    }

    private void LoginAccount(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        SAVE_EMAIL = email;
                        btnLogin.setText("Please wait...");
                        createSnackbar("Login success!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(AccountLogin.this, HomeActivity.class);
                                startActivity(intent);
                                CustomIntent.customType(AccountLogin.this,
                                        "fadein-to-fadeout");
//                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
//                                    CustomIntent.customType(AccountLogin.this,
//                                            "fadein-to-fadeout");
//                                }
                            }
                        }, 1500);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        createSnackbar("Failed to login");
                        layout_pass.setError("Wrong email address or password!");
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login again");
                        signup_text.animate().alpha(1f).setDuration(500).start();
                    }
                });
    }

    private void createSnackbar(String message) {
        Snackbar.make(motionLayout, message, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setTextColor(Color.WHITE)
                .show();
    }

    public void SignUp(View view) {
        //createSnackbar("Registration is temporary unavailable. We apologize for the inconvenience.");
        Intent intent = new Intent(AccountLogin.this, Checkpoint.class);
        startActivity(intent);
        CustomIntent.customType(AccountLogin.this,
                "fadein-to-fadeout");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", SAVE_EMAIL);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            emailAdd.setText(savedInstanceState.getString("email"));
        }
    }
    public boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public boolean ifTheresInternet(){
        String command = "ping -c 1 google.com";
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
    private void showNoInternetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AccountLogin.this);
        dialog.setTitle("Internet Required");
        dialog.setCancelable(false);
        dialog.setMessage("Apologies, but unfortunately you don't have an internet access. Please enable WiFi or Data Connection to continue.");
        dialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        }).show();
    }
}