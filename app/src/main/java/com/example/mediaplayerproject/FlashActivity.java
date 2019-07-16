package com.example.mediaplayerproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        getSupportActionBar().hide();

        //after animation go to home page
        new Timer().schedule(new TimerTask(){
            public void run() {
                FlashActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // do your work here
                        Intent h=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(h);
                    }
                });
            }
        }, 1000);
    }
}
