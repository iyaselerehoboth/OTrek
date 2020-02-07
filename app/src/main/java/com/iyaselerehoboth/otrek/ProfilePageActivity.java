package com.iyaselerehoboth.otrek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindInt;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar_profile);
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

        }
    }



}
