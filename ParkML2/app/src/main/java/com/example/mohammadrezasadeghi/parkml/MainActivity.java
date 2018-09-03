package com.example.mohammadrezasadeghi.parkml;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3004;
    Button button2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                Intent homeintent = new Intent( MainActivity.this, HomeActivity.class );

                startActivity( homeintent );
                finish();
            }
        }, SPLASH_TIME_OUT );

    }


    }

