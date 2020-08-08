package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;
import com.example.haclicker.HostScreen;
import com.example.haclicker.R;

import java.util.ArrayList;
import java.util.List;

public class ManualAddQuestionScreen extends AppCompatActivity {

    EditText questionDescibe;
    Button addOption, createQuestion;
    List<String> options = new ArrayList<>();
    List<EditText> editText = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_add_question_screen);

        questionDescibe = findViewById(R.id.questionDescribe);
        addOption = findViewById(R.id.addOption);
        createQuestion = findViewById(R.id.createQuestion);

        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout optionList = findViewById(R.id.optionList);
                optionList.removeAllViews();


                for (final EditText et : editText) {
                    View optionChunk = getLayoutInflater().inflate(R.layout.chunk_option,
                            optionList, false);
                    EditText optionTxt = optionChunk.findViewById(R.id.optionTxt);
                    optionTxt.setText(et.getText().toString());
                    optionList.addView(optionChunk);
                }

                View optionChunk = getLayoutInflater().inflate(R.layout.chunk_option,
                        optionList, false);
                EditText questionTxt = optionChunk.findViewById(R.id.optionTxt);
                questionTxt.setHint("add option description here");
                optionList.addView(optionChunk);
                editText.add(questionTxt);
            }
        });

        createQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (EditText et : editText) {
                    options.add(et.getText().toString());
                }
                int totalQuestion = Teacher.getClassroom().getQuestions().size();
                Teacher.addQuestionToQueue(new Question(questionDescibe.getText().toString(), totalQuestion, options));
                Intent intent = new Intent(getApplicationContext(), HostScreen.class);
                startActivity(intent);
            }
        });
    }
}