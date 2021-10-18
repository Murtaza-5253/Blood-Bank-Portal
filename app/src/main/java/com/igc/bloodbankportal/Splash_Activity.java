package com.igc.bloodbankportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash_Activity extends AppCompatActivity {
    int SPLASH_SCREEN = 3000;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sp = getSharedPreferences("BloodBank", MODE_PRIVATE);
                if(sp.getString("EMAIL","Guest").equalsIgnoreCase("Guest"))
                {
                    startActivity(new Intent(Splash_Activity.this,Login_Activity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(Splash_Activity.this,DashBoard_Activity.class));
                    finish();
                }
            }
        },SPLASH_SCREEN);
    }
}