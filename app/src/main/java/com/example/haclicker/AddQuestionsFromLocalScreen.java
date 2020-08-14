package com.example.haclicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;

import java.util.Arrays;
import java.util.List;

public class AddQuestionsFromLocalScreen extends AppCompatActivity {

    Button submit_button;
    EditText path_input;
    private String path;
    private List<String> questionsToAdd;

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file_path);
        path_input = findViewById(R.id.path_input);
        submit_button = (Button) findViewById(R.id.submit);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path = path_input.getText().toString();
                toList(path);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void toList(String path) {
        String[] parsedChoices;
        List<String> choicestoAdd;
        questionsToAdd = ImportQuestonsFromLocal.readFileIntoString(path);
        int counter = 1;
        for (int i = 0; i < questionsToAdd.size(); i += 2) {
            String questionDescription = questionsToAdd.get(i);
            parsedChoices = questionsToAdd.get(i + 1).split(",");
            choicestoAdd = Arrays.asList(parsedChoices);
            Teacher.addQuestionToQueue(new Question(questionDescription, counter, choicestoAdd, false));
            counter++;
        }
    }
}