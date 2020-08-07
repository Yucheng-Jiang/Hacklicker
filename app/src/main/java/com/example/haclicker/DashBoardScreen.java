package com.example.haclicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class DashBoardScreen extends AppCompatActivity {

    ImageButton consoleButton;
    Button createRoom, joinRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_screen);

        consoleButton = findViewById(R.id.consoleButton);
        createRoom = findViewById(R.id.createRoom);
        joinRoom = findViewById(R.id.joinRoom);

        consoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConsoleScreen.class);
                startActivity(intent);
            }
        });

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: create room
            }
        });
    }
}