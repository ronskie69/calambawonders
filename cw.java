MAINACTIVITY.JAVA

public class MainActivity extends AppCompatActivity {

    FirebaseUser user;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Auto Login...");

        if(isConnectedToInternet()){
            checkPermision();
        } else {
            showNoInternetDialog();
        }
    }

    public boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public void checkPermision(){
        Dexter.withContext(this.getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        if(isConnectedToInternet()){
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user != null){
                                progressDialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.this.progressDialog.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }, 3000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), AccountLogin.class);
                                        startActivity(intent);
                                    }
                                }, 3000);
                            }
                        }
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Please allow it to continue. To get request again, clear the app's data.", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.finish();
                                System.exit(0);
                            }
                        }, 3000);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    private void showNoInternetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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


ACCOUNTLOGIN.JAVA

public class AccountLogin extends AppCompatActivity {

    MaterialButton btnLogin, btnBack;
    EditText emailAdd, pass;
    TextInputLayout layout_email_address, layout_pass;
    TextView signup_text;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Animation slide_left, slide_right;

    String SAVE_EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.account_login);

        if(!isConnectedToInternet()){
            showNoInternetDialog();
        }

        //dito 

        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        signup_text = findViewById(R.id.signup_text);
        emailAdd = findViewById(R.id.email_address);
        pass = findViewById(R.id.password);
        layout_email_address = findViewById(R.id.layout_email_address);
        layout_pass = findViewById(R.id.layout_pass);

        btnBack.setVisibility(View.GONE);

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
        signup_text.startAnimation(slide_left);


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountLogin.this, Checkpoint.class));
                CustomIntent.customType(AccountLogin.this,
                        "fadein-to-fadeout");
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

                if(!isConnectedToInternet()){
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
                        Toast.makeText(AccountLogin.this, "Login success!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(AccountLogin.this, HomeActivity.class);
                                startActivity(intent);
                                CustomIntent.customType(AccountLogin.this,
                                        "fadein-to-fadeout");
                            }
                        }, 3000);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountLogin.this, "Failed to login", Toast.LENGTH_LONG).show();
                        pass.getText().clear();
                    }
                });
    }

    public void SignUp(View view) {
        Intent intent = new Intent(AccountLogin.this, Checkpoint.class);
        startActivity(intent);
        CustomIntent.customType(AccountLogin.this,
                "fadein-to-fadeout");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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