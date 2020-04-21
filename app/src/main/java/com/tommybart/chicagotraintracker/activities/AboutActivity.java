package com.tommybart.chicagotraintracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.tommybart.chicagotraintracker.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionNumberText = findViewById(R.id.about_version);
        try {
            PackageInfo packageInfo =
                    getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumberText.setText(String.format("Version %s", packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
