package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


/**
 * Login screen activity. Request the user to login through google
 * If already login, jump to main screen activity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                // run() method will be executed when 3 seconds have passed

                //Time to start MainActivity
                Intent intent = new Intent(getApplicationContext(), SignInScreen.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
