package com.johnwilliams.qq.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.OnClearFromRecentService;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
