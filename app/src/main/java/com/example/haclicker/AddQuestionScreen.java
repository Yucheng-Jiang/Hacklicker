package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddQuestionScreen extends AppCompatActivity {
    Button manualAddBtn, quickAddbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question_screen);
        // set UI component
        manualAddBtn = findViewById(R.id.manualAddBtn);
        quickAddbtn = findViewById(R.id.quickAddbtn);

        manualAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManualAddQuestionScreen.class);
                intent.putExtra("quickAdd", false);
                startActivity(intent);
                finish();
            }
        });

        quickAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManualAddQuestionScreen.class);
                intent.putExtra("quickAdd", true);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HostScreen.class);
        startActivity(intent);
        finish();
    }
}