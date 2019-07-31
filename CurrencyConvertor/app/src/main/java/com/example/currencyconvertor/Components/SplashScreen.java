package com.example.currencyconvertor.Components;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.example.currencyconvertor.R;

public class SplashScreen extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        handler.postDelayed(new Runnable() {
            public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
            }
        }, 2000);

    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

