package com.menposun.calambawonders.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.menposun.calambawonders.AccountLogin;
import com.menposun.calambawonders.FavoriteSetters;
import com.menposun.calambawonders.R;
import com.menposun.calambawonders.adapter.FavoritesAdapter;
import com.menposun.calambawonders.adapter.FavoritesItemAdapter;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class AccountFragment extends Fragment {

    private View vue;
    private FrameLayout layout_account;
    private MaterialButton btnEditUser ,editFaves;
    private Dialog dialog;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialTextView emailText, nickname, about_me, account_type, isEmptyFavorites;
    private RecyclerView recycler_view_faves;
    private List<FavoriteSetters> favorites;
    private FavoritesAdapter favoritesItemAdapter;
    private ImageView profile_pic;
    private static final int requestCodeImg = 1;
    ProgressDialog progressDialog;
    boolean hasloaded = false;
    //uri
    private Uri imgUri;
    //firebase
    private FirebaseDatabase db;
    private FirebaseStorage fireStorage;
    private StorageReference storageReference;
    private DatabaseReference dbReference;
    private String DB_STORAGE_PATH = "users/";
    private Uri img;
    private Animation slide_left, slide_right;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       vue =  inflater.inflate(R.layout.fragment_account, container, false);


       layout_account = vue.findViewById(R.id.layout_account);
       emailText = vue.findViewById(R.id.current_email);
       profile_pic = vue.findViewById(R.id.profile_pic);
       recycler_view_faves = vue.findViewById(R.id.recycler_view_faves);
       btnEditUser = vue.findViewById(R.id.edit_user);
       about_me = vue.findViewById(R.id.about_me);
       nickname = vue.findViewById(R.id.nickname);
       account_type = vue.findViewById(R.id.account_type);
       Picasso.get().load(R.drawable.image_unavailable).into(profile_pic);
       isEmptyFavorites = vue.findViewById(R.id.isEmptyFavorites);
       editFaves= vue.findViewById(R.id.editFaves);
       //animations
       slide_left = AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left);
       slide_right= AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
       btnEditUser.startAnimation(slide_right);

       btnEditUser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               updateProfile();
           }
       });

       profile_pic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               changeImage();
           }
       });
       dialog = new Dialog(getContext());

       progressDialog = new ProgressDialog(AccountFragment.this.getContext());
       editFaves.setOnClickListener(v -> redirect(new WishlistFragment()));

       return vue;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        fireStorage = FirebaseStorage.getInstance();
        dbReference = db.getReference("Users");
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        about_me.startAnimation(slide_right);
        account_type.startAnimation(slide_left);
        //when fragment initiated, call this method to load the user profile
        setUpProfile();
        FetchFavorites();
    }

    private void remindUser() {
        AlertDialog.Builder announcement = new AlertDialog.Builder(getContext());
        announcement.setTitle("Announcement");
        announcement.setCancelable(false);
        announcement.setMessage("Hello " + nickname.getText().toString()+", I would like to remind you" +
                " that some of the functionalities of this app are under development. For now, we " +
                "can only tour you from our tourist destinations while we are developing these " +
                "features. Hope you understand. Thank you and have a safe trip!");
        announcement.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
            }
        }).create().show();
    }

    private void updateProfile() {
        String options [] = { "Edit Profile Picture", "Edit Nickname", "Edit About me"};
        //dbReference.child("profile_pic").setValue(img);
        AlertDialog.Builder dialog = new AlertDialog.Builder(AccountFragment.this.getContext());
        dialog.setTitle("What to update?");
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        progressDialog.setMessage("Setting up profile...");
                        optionsProfile();
                        break;
                    case 1:
                        progressDialog.setMessage("Setting up your nickname...");
                        changeNickname();
                        break;
                    case 2:
                        progressDialog.setMessage("Setting up your description...");
                        updateAboutMe();
                        break;

                }
            }
        }).create().show();
    }
    private void optionsProfile(){
        String options [] = { "From Gallery", "From Avatars (Beta)"};
        //dbReference.child("profile_pic").setValue(img);
        AlertDialog.Builder dialog = new AlertDialog.Builder(AccountFragment.this.getContext());
        dialog.setTitle("What to update?");
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        changeImage();
                        break;
                    case 1:
                        showAvatars();
                        break;

                }
            }
        }).create().show();
    }
    public void showAvatars(){
        initAvatars();
        dialog.show();
    }

    private void initAvatars() {

        ArrayList<ImageView> imageViewArrayList = new ArrayList<>();

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_of_avatars);
        ImageView man1, man2, man3, man4, man9, man8, man7, man6,
                man5, girl1, girl2, girl3, girl4, domokun, zombie, girl5, girl6, assassin
                ,girl7, girl8, girl9, girl10, girl11, girl12;

        man1 = dialog.findViewById(R.id.man10);
        man2 = dialog.findViewById(R.id.man2);
        man3 = dialog.findViewById(R.id.man3);
        man4 = dialog.findViewById(R.id.man4);
        man5 = dialog.findViewById(R.id.man5);
        man6 = dialog.findViewById(R.id.man6);
        man7 = dialog.findViewById(R.id.man7);
        man8 = dialog.findViewById(R.id.man8);
        man9 = dialog.findViewById(R.id.man9);
        domokun = dialog.findViewById(R.id.domokun);
        assassin = dialog.findViewById(R.id.assassin);
        zombie = dialog.findViewById(R.id.zombie);
        girl1 = dialog.findViewById(R.id.girl1);
        girl2 = dialog.findViewById(R.id.girl2);
        girl3= dialog.findViewById(R.id.girl3);
        girl4 = dialog.findViewById(R.id.girl4);
        girl5 = dialog.findViewById(R.id.girl5);
        girl6 = dialog.findViewById(R.id.girl6);
        girl7 = dialog.findViewById(R.id.girl7);
        girl8 = dialog.findViewById(R.id.girl8);
        girl9 = dialog.findViewById(R.id.girl9);
        girl10 = dialog.findViewById(R.id.girl10);
        girl11 = dialog.findViewById(R.id.girl11);
        girl12 = dialog.findViewById(R.id.girl12);
        man1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man1);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man2);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man3);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man4);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man5);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl1);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl2);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl3);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl4);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man8);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man9);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man7);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        man6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.man6);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl7);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl8);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl9);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl10);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl11);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl12);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        domokun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.domokun);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        assassin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.assassin);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        zombie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.zombie);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl5);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
        girl6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUri = Uri.parse("android.resource://"
                        + R.class.getPackage().getName() + "/" +R.drawable.girl6);
                uploadToStorage(imgUri);
                dialog.dismiss();
            }});
    }

    private void uploadToStorage(Uri imgUri) {
        progressDialog.show();

        String DEFAULT_FILE_PATJ = DB_STORAGE_PATH + "images_" + user.getUid();
        StorageReference storageReference2 = storageReference.child(DEFAULT_FILE_PATJ);

        storageReference2.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task <Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while(!task.isSuccessful());
                        Uri downloader = task.getResult();

                        if(task.isSuccessful()){
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("profile_pic", downloader.toString());
                            dbReference.child(user.getUid()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            setUpProfile();
                                            createSnackbar("Successfully changed profile picture!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            progressDialog.dismiss();
                                            createSnackbar("Failed to change profile picture!");
                                        }
                                    });
                        } else {
                           createSnackbar("Failed to change profile picture!");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Acount Fragment", e.getMessage());
                    }
                });
    }

    private void updateAboutMe() {
        AlertDialog.Builder setNickname  = new AlertDialog.Builder(AccountFragment.this.getContext());
        setNickname.setTitle("Update Profile");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText editText = new EditText(getActivity());
        layout.addView(editText);
        editText.setHint("Enter your profile description/bio...");
        setNickname.setView(layout);

        setNickname.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String bio = editText.getText().toString().trim();

                if(TextUtils.isEmpty(bio)){
                    editText.setError("This is required!");
                } else if(bio.length() > 25){
                    editText.setError("This is required!");
                    createSnackbar("Your bio is too long! Max of 25 characters.");
                } else {
                    progressDialog.show();
                    HashMap<String, Object> setter = new HashMap<>();
                    setter.put("bio", bio);

                    //dbReference == "users/" (Realtime database)
                    dbReference.child(user.getUid()).updateChildren(setter)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    setUpProfile();
                                    Toast.makeText(AccountFragment.this.getContext(),
                                            "Profile bio success edit!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AccountFragment.this.getContext(),
                                            "Edit failed successfully!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                }
            }
        }).setNegativeButton("No, sorry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void changeNickname() {
        AlertDialog.Builder setNickname = new AlertDialog.Builder(AccountFragment.this.getContext());
        setNickname.setTitle("Update Nickname");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText editText = new EditText(getActivity());
        layout.addView(editText);
        editText.setHint("Enter new nickname...");
        setNickname.setView(layout);

        setNickname.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String new_nickname = editText.getText().toString().trim();

                if (TextUtils.isEmpty(new_nickname)) {
                    createSnackbar("Empty nickname! Please enter.");
                    editText.setError("This is empty!");
                } else if (new_nickname.length() > 12) {
                    createSnackbar("Too long nickname! Max of 12 letters.");
                    editText.setError("This is empty!");
                } else {
                    progressDialog.show();

                    //new_nickname = new_nickname.substring(0, 1).toUpperCase() + new_nickname.substring(1).toLowerCase();

                    HashMap<String, Object> setter = new HashMap<>();
                    setter.put("nickname", new_nickname);

                    //dbReference == "users/" (Realtime database)
                    dbReference.child(user.getUid()).updateChildren(setter)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    setUpProfile();
                                    createSnackbar("Nickname successfully changed!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    progressDialog.dismiss();
                                    createSnackbar("Failed to edit nickname! Make sure you have internet conneciton.");
                                }
                            });
                }
            }
        }).setNegativeButton("No, sorry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void setUpProfile() {
        progressDialog.setMessage("Setting up your profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Query load = dbReference.orderByChild("email").equalTo(user.getEmail());
        load.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    emailText.setText(ds.child("email").getValue().toString());
                    nickname.setText(ds.child("nickname").getValue().toString());
                    about_me.setText(ds.child("bio").getValue().toString());
                    account_type.setText(ds.child("type").getValue().toString());

                    emailText.startAnimation(slide_left);

                    String UUID = ds.child("UUID").getValue().toString();

                    String image = ds.child("profile_pic").getValue().toString();

                    try {
                        Glide.with(getContext()).load(image).fitCenter().into(profile_pic);
                    } catch (Exception e){
                        Log.v("ERROR ACCOUNT", e.getMessage());
                    }
                }
                progressDialog.cancel();
                progressDialog.dismiss();
                if(!hasloaded){
                    remindUser();
                }
                hasloaded = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void changeImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCodeImg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == requestCodeImg && data != null && data.getData() != null) {
            img = data.getData();
            progressDialog.show();
            Glide.with(getContext()).load(img).fitCenter().into(profile_pic);
            uploadProfile();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void uploadProfile() {
        progressDialog.show();
        //users/images_1234567
        String filePath = DB_STORAGE_PATH + "images_" + user.getUid();
        StorageReference storageReference2 = storageReference.child(filePath);
        storageReference2.putFile(img)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task <Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while(!task.isSuccessful());
                        Uri downloader = task.getResult();

                        if(task.isSuccessful()){
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("profile_pic", downloader.toString());
                            dbReference.child(user.getUid()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            setUpProfile();
                                            createSnackbar("Profile picture successfully changed!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            progressDialog.dismiss();
                                            createSnackbar("Failed to upload profile! Make sure you have internet conneciton.");
                                        }
                                    });
                        } else {
                            createSnackbar("Failed to upload profile! Make sure you have internet conneciton.");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        createSnackbar("Failed to upload profile! Make sure you have internet conneciton.");
                    }
                });
    }

    public void FetchFavorites(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler_view_faves.setLayoutManager(layoutManager);

        favorites = new ArrayList<>();
        favorites.clear();
        Query loadFaves =  FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("UUID").equalTo(user.getUid());
        loadFaves.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot datas : snapshot.getChildren()){
                    if(datas.child("favorites").exists()){
                        for(DataSnapshot dataFromFaves : datas.child("favorites").getChildren()){
                            favorites.add(new FavoriteSetters(dataFromFaves.child("place").getValue().toString(),
                                    dataFromFaves.child("type").getValue().toString()));


                            //set gone kc may data na
                            isEmptyFavorites.setVisibility(View.GONE);
                        }
                    } else {
                        //set visible kung walang data pa
                        isEmptyFavorites.setVisibility(View.VISIBLE);
                    }
                }
                favoritesItemAdapter = new FavoritesAdapter(getContext(), favorites);
                favoritesItemAdapter.notifyDataSetChanged();
                recycler_view_faves.setAdapter(favoritesItemAdapter);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                isEmptyFavorites.setVisibility(View.VISIBLE);
            }
        });
    }
    private void createSnackbar(String message) {
        Snackbar.make(layout_account, message, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setTextColor(Color.BLACK)
                .show();
    }
    private void redirect(Fragment frag){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, frag).commit();
    }
    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle("Calamba Wonders");
    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle("Profile");
    }
}