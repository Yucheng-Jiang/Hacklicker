package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainScreen extends AppCompatActivity {
    //The create room button;
    private Button createRoomButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createRoomButton = findViewById(R.id.CreateRoom);
        setContentView(R.layout.activity_main_screen);
    }
}