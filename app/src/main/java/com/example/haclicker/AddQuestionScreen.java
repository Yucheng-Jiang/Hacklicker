package com.example.haclicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;

public class AddQuestionScreen extends AppCompatActivity {
    Button manualAddBtn, quickAddbtn;
    private final int DEFAULT_OPTION_NUM = 5;
    private String path;

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
                startActivity(intent);
                finish();
            }
        });

        quickAddbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                path = new ImportFile("").getFilePath();
                List<String> options = ImportQuestonsFromLocal.readFileIntoString(path);
                int questionID = Teacher.getClassroom().getQuestions().size();
                for (int i = 0; i < DEFAULT_OPTION_NUM; i++) {
                    char index = (char) ((int) 'A' + i);
                    options.add("Option " + index);
                }
                Teacher.addQuestionToQueue(new Question("Question " + (questionID + 1),
                        questionID, options, false));
                Intent intent = new Intent(getApplicationContext(), HostScreen.class);
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