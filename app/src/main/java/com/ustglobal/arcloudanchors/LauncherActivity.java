package com.ustglobal.arcloudanchors;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    public static final String ELECTRONICS = "electronics";
    public static final String TOYS = "toys";
    public static final String TV_APPLIANCES = "tv_appliances";
    public static final String CLOTHING = "clothing";
    public static final String FROM = "from";
    public static final String MODE = "mode";
    public String userMode = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laucher_activiy);
        //For background animation
        ConstraintLayout layout = findViewById(R.id.main_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        ImageButton settingsBtn = findViewById(R.id.settings_btn);
        settingsBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getApplicationContext(), v);
            popup.getMenuInflater().inflate(R.menu.menu_main ,popup.getMenu());
            popup.setOnMenuItemClickListener(LauncherActivity.this::onMenuItemClick);
            popup.show();
        });

        ImageButton electBtn = findViewById(R.id.electronic_btn);
        ImageButton toysBtn = findViewById(R.id.toys_btn);
        ImageButton tvBtn = findViewById(R.id.tv_btn);
        ImageButton clothingBtn = findViewById(R.id.clothing_btn);
        electBtn.setOnClickListener(this);
        toysBtn.setOnClickListener(this);
        tvBtn.setOnClickListener(this);
        clothingBtn.setOnClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                userMode = "user";
                return true;
            case R.id.item2:
                userMode = "admin";
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.electronic_btn:
                goToCameraActivity(ELECTRONICS);
                break;
            case R.id.toys_btn:
                goToCameraActivity(TOYS);
                break;
            case R.id.tv_btn:
                goToCameraActivity(TV_APPLIANCES);
                break;
            case R.id.clothing_btn:
                goToCameraActivity(CLOTHING);
                break;

        }
    }

    private void goToCameraActivity(String Section) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra(FROM, Section);
        i.putExtra(MODE,userMode);
        startActivity(i);
    }
}
