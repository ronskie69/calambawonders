package com.menposun.calambawonders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.menposun.calambawonders.fragments.AccountFragment;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import maes.tech.intentanim.CustomIntent;

public class SignupActivity extends AppCompatActivity {

    //https://console.firebase.google.com/project/calambawonders/database

    private MotionLayout motion_signup;
    private MaterialButton mbCancel, mbSignUp;
    TextInputLayout layout_email_address, layout_pass;
    private EditText email_signup, password_signup;
    TextView textView2;


    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    String account_type;
    String type ="";
    Uri imgUri;

    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference dbReference;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        motion_signup = findViewById(R.id.motion_signup);
        layout_pass = findViewById(R.id.layout_pass);
        layout_email_address = findViewById(R.id.layout_email_address);
        email_signup = findViewById(R.id.email_signup);
        password_signup = findViewById(R.id.password_signup);
        mbCancel = findViewById(R.id.mbCancel);
        mbSignUp = findViewById(R.id.mbSignUp);
        textView2 = findViewById(R.id.textView2);

        if(!ifTheresInternet()){
            showNoInternetDialog();
        }



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering... please wait");

        //firebase instace
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseDatabase.getInstance();
        dbReference = db.getReference("Users");

        email_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    layout_email_address.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    layout_pass.setError("Don't leave fiels empty!");
                } else {
                    layout_pass.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mbSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_add = email_signup.getText().toString();
                String password = password_signup.getText().toString();

                 if(TextUtils.isEmpty(email_add) || TextUtils.isEmpty(password)) {
                   layout_email_address.setError("This is required!");
                   layout_pass.setError("This is required!");
                } else if(TextUtils.isEmpty(password)) {
                     layout_pass.setError("This is required!");
                } else if(password.length() < 6) {
                     layout_pass.setError("Password too short! (Minimum of 6 characters)");
                     password_signup.getText().clear();
                }  else if(!(Patterns.EMAIL_ADDRESS.matcher(email_add).matches())) {
                     createSnackbar("Empty email address and password!");
                     layout_email_address.setError("Please enter valid address! (ex. karl@gmail.com)");
                }  else {
                    if(ifTheresInternet()){
                        RegisterAccount(email_add, password);
                        mbSignUp.setText("Signing up...");
                        mbSignUp.setEnabled(false);
                    } else {
                        showNoInternetDialog();
                    }
                }
            }
        });

        mbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackToMainPage(null);
            }
        });

        Bundle bundle = getIntent().getExtras();


        if(bundle != null){
            account_type = bundle.getString("type");
            textView2.setText("Quick Signup as " + account_type);
        }
    }

    private void RegisterAccount(String email_add, String password) {

        progressDialog.show();
        progressDialog.setCancelable(false);
        firebaseAuth.createUserWithEmailAndPassword(email_add, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user = firebaseAuth.getCurrentUser();

                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    createSnackbarWithActionGmail("Please check your email for verification");
                                    Signup(password);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    createSnackbar("Internet connection failed!");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            recreate();
                                        }
                                    }, 1000);
                                    //CustomIntent.customType(getApplicationContext(), "fadein-to-fadeout");
                                }
                            });
                        } else {
                            layout_email_address.setError("Email address already signed up!");
                            progressDialog.cancel();
                            progressDialog.dismiss();
                            password_signup.getText().clear();
                        }
                    }
                });
    }

    private void Signup(String password){

        user = firebaseAuth.getCurrentUser();
        //INSERT IMAGE FIRST TO DATABASE
        imgUri = Uri.parse("android.resource://"
                + R.class.getPackage().getName() + "/" +R.drawable.man1);

        String filePath = "users/images_" + user.getUid();

        StorageReference storageReference2 = storageReference.child(filePath);

        storageReference2.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //AFTER INSERT, DOWNLOAD THE URI STRING

                        Task <Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while(!task.isSuccessful());
                        Uri downloader = task.getResult();

                        if(task.isSuccessful()){

                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap <Object, String> hashMap = new HashMap<>();

                            if(account_type !=null || !account_type.isEmpty()){
                                hashMap.put("type", account_type);
                            }
                            hashMap.put("email", email);
                            hashMap.put("nickname", "Unknown");
                            hashMap.put("UUID", uid);
                            hashMap.put("pass", password);
                            hashMap.put("bio", "No profile description added yet.");
                            hashMap.put("profile_pic", downloader.toString());

                            //THEN UPLOAD TO FB DATABASE INCLUDING THE IMAGE URI (LINK PATH)
                            // AS USER PROFILE

                            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            createSnackbar("Registered successfully!");
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    BackToMainPage(email);
                                                }
                                            }, 2000);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {

                                        }
                                    });
                        } else {
                            createSnackbar("Failed to register!");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        createSnackbar("Failed to register! Make sure you have a stable internet connection.");
                    }
                });
    }
    private void createSnackbar(String mesage) {
        Snackbar.make(motion_signup, mesage, Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setTextColor(Color.WHITE)
                .show();
    }

    private void createSnackbarWithActionGmail(String mesage) {
        Snackbar.make(motion_signup, mesage, Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setTextColor(Color.WHITE)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivity");
                        startActivity(intent);
                        CustomIntent.customType(getApplicationContext(), "fadein-to-fadeout");
                    }
                })
                .show();
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
        android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this);
        dialog.setTitle("Internet Required");
        dialog.setCancelable(false);
        dialog.setMessage("Apologies, but unfortunately you don't have a stable internet access. Please enable WiFi or Data Connection to continue.");
        dialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        }).show();
    }

    public void BackToMainPage(String email){
        Intent intent = new Intent(SignupActivity.this.getApplicationContext(),
                AccountLogin.class);
        intent.putExtra("email", email);
        startActivity(intent);
        CustomIntent.customType(SignupActivity.this,
                "fadein-to-fadeout");
    }

    @Override
    public void onBackPressed() {
        BackToMainPage(null);
    }
}