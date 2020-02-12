package com.iyaselerehoboth.otrek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.iyaselerehoboth.otrek.Database.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfilePageActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_profile)
    MaterialToolbar toolbar_profile;

    @BindView(R.id.img_btn_make_call)
    AppCompatImageButton img_btn_make_call;

    @BindView(R.id.img_btn_whatsapp)
    AppCompatImageButton img_btn_whatsapp;

    @BindView(R.id.img_btn_location)
    AppCompatImageButton img_btn_location;

    @BindView(R.id.img_btn_music)
    AppCompatImageButton img_btn_music;

    @BindView(R.id.img_btn_selfie)
    AppCompatImageButton img_btn_selfie;

    @BindView(R.id.circular_imgView)
    CircularImageView civ_profile_pic;

    @BindView(R.id.mtv_username)
    MaterialTextView mtv_username;

    @BindView(R.id.fab_profile_page)
    FloatingActionButton fab_profile_page;

    SessionManager session;
    HashMap<String, String> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        ButterKnife.bind(this);
        session = new SessionManager(ProfilePageActivity.this);

        setSupportActionBar(toolbar_profile);
        initProfileViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit_profile:
                //Do Something
                return true;
            case R.id.menu_sign_out:
                signOutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @OnClick({R.id.img_btn_selfie, R.id.img_btn_music, R.id.img_btn_location, R.id.img_btn_whatsapp, R.id.img_btn_make_call})
    public void actionButton(AppCompatImageButton imgBtn){
        switch (imgBtn.getId()){
            case R.id.img_btn_make_call:
                startActivity(new Intent(Intent.ACTION_CALL_BUTTON));
                break;
            case R.id.img_btn_music:
                startActivity(new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER));
                break;
            case R.id.img_btn_whatsapp:
                startWhatsAppChat();
                break;

        }
    }

    @OnClick(R.id.fab_profile_page)
    public void fabOnClick(){
        initTrekking();
    }

    public void initProfileViews(){
        user = session.getUserDetails();

        Glide.with(ProfilePageActivity.this)
                .load(user.get(SessionManager.KEY_USER_PICTURE_LINK))
                .placeholder(R.drawable.ic_logo)
                .circleCrop()
                .centerInside()
                .into(civ_profile_pic);

        mtv_username.setText("Welcome, " + user.get(SessionManager.KEY_USER_FULL_NAME));
    }

    public void signOutUser(){
        FirebaseAuth.getInstance().signOut();
        session.clearUserDetails();
        finish();
    }

    public void startWhatsAppChat(){
        try{
            startActivity(new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .setPackage("com.whatsapp"));
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, "Kindly install WhatsApp", Toast.LENGTH_LONG).show();
        }

    }

    public void initTrekking(){
        //Do many things.
        startActivity(new Intent(ProfilePageActivity.this, MapsActivity.class));
    }

}
