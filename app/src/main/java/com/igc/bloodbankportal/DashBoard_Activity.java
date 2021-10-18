package com.igc.bloodbankportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashBoard_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
    }
    public void findDonor(View view) {
        startActivity(new Intent(DashBoard_Activity.this,Find_Donor_Activity.class));
        finish();
    }
    public void bloodBank(View view) {
        startActivity(new Intent(DashBoard_Activity.this,Blood_Bank_Activity.class));
        finish();
    }
    public void status(View view) {
        startActivity(new Intent(DashBoard_Activity.this,Status_Activity.class));
        finish();
    }

    public void request(View view) {
        startActivity(new Intent(DashBoard_Activity.this,Request_Activity.class));
        finish();
    }

    public void profile(View view) {
        startActivity(new Intent(DashBoard_Activity.this,Profile_Activity.class));
        finish();
    }
}