package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddQuestionScreen extends AppCompatActivity {
    Button manualAddBtn, jsonAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question_screen);
        // set UI component
        manualAddBtn = findViewById(R.id.manualAddBtn);
        jsonAddBtn = findViewById(R.id.jsonAddBtn);

        manualAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManualAddQuestionScreen.class);
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