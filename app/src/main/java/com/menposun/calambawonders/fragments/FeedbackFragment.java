package com.menposun.calambawonders.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.menposun.calambawonders.R;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class FeedbackFragment extends Fragment {

    private View vue;
    private EditText compose_feedback;
    private MaterialButton btnFeedback;
    private FirebaseAuth auth;
    private RelativeLayout feedback_layout;
    private FirebaseUser currentUser;
    private ProgressDialog dialog;

    public FeedbackFragment() {
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
        vue= inflater.inflate(R.layout.fragment_feedback, container, false);

        compose_feedback = vue.findViewById(R.id.compose_feedback);
        btnFeedback = vue.findViewById(R.id.btnFeedback);
        feedback_layout = vue.findViewById(R.id.feedback_layout);

        //initialize dialog
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Sending your feedback...");
        //firebase current user
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        btnFeedback.setOnClickListener(v -> {
            if(TextUtils.isEmpty(compose_feedback.getText().toString())){
                createSnackbar("Your feedback is empty!");
            } else {
                //show dialog and send email feedback
                dialog.show();
                sendEmailFeedback(compose_feedback.getText().toString());
            }
        });
        

        return  vue;
    }

    public void sendEmailFeedback(String msg) {

        final String user = "sunogan77@gmail.com";
        final String pass = getString(R.string.hello_first_fragment);

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {

            final MimeMessage message = new MimeMessage(session);
            final String addressList = "sunogan77@gmail.com, mendozaprincesslorraine@gmail.com, populiangelicarose@gmail.com";
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(addressList));
            message.setSubject("Feedback for Calamba Wonders");
            String mesg = "Hello Developers! User '" + currentUser.getEmail() + " has sent a feedback. ''" + msg +"''";
            message.setText(mesg);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);

                        dialog.cancel();
                        dialog.dismiss();

                        compose_feedback.getText().clear();
                        createSnackbar("Thanks for your feedback!");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (MessagingException e){
            throw new RuntimeException(e);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    private void createSnackbar(String message) {
        Snackbar.make(feedback_layout, message, Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setBackgroundTint(Color.parseColor("#EDAC1A"))
                .setTextColor(Color.BLACK)
                .show();
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
        activity.getSupportActionBar().setTitle("Feedback");
    }
}