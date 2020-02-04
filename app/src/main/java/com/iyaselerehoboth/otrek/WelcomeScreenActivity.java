package com.iyaselerehoboth.otrek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeScreenActivity extends AppCompatActivity {
    @BindView(R.id.btn_facebook_login)
    MaterialButton btn_facebook_login;

    @BindView(R.id.btn_twitter_login)
    MaterialButton btn_twitter_login;

    @BindView(R.id.btn_email_login)
    MaterialButton btn_email_login;

    @BindView(R.id.btn_google_login)
    MaterialButton btn_google_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        ButterKnife.bind(this);

        // In Activity's onCreate() for instance
        // To make the notification bar transparent.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    @OnClick(R.id.btn_email_login)
    public void emailLogin(){
        startActivity(new Intent(WelcomeScreenActivity.this, ProfilePageActivity.class));
    }


}
