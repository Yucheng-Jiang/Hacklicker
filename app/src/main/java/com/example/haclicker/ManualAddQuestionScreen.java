package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.haclicker.DataStructure.ClassRoom;
import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;

import java.util.ArrayList;
import java.util.List;

/**
 * Host user add question manually
 */
public class ManualAddQuestionScreen extends AppCompatActivity {

    EditText questionDescribe;
    Button addOption, createQuestion;
    List<String> options = new ArrayList<>();
    List<EditText> editText = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_add_question_screen);
        // set UI component
        questionDescribe = findViewById(R.id.questionDescribe);
        addOption = findViewById(R.id.addOption);
        createQuestion = findViewById(R.id.createQuestion);
        // add option button clicked
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove all existing options
                LinearLayout optionList = findViewById(R.id.optionList);
                optionList.removeAllViews();
                // re-populate all editText views
                for (final EditText et : editText) {
                    View optionChunk = getLayoutInflater().inflate(R.layout.chunk_option,
                            optionList, false);
                    EditText optionTxt = optionChunk.findViewById(R.id.optionTxt);
                    optionTxt.setText(et.getText().toString());
                    optionList.addView(optionChunk);
                }
                // add a new option editText
                View optionChunk = getLayoutInflater().inflate(R.layout.chunk_option,
                        optionList, false);
                EditText questionTxt = optionChunk.findViewById(R.id.optionTxt);
                questionTxt.setHint("add option description here");
                optionList.addView(optionChunk);
                editText.add(questionTxt);
            }
        });

        // confirm question button onClickListener
        createQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (EditText et : editText) {
                    options.add(et.getText().toString());
                }
                // used to set question ID
                int totalQuestion = Teacher.getClassroom().getQuestions().size();
                Teacher.addQuestionToQueue(new Question(questionDescribe.getText().toString(),
                        totalQuestion, options, false));
                Intent intent = new Intent(getApplicationContext(), HostScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AddQuestionScreen.class);
        startActivity(intent);
        finish();
    }
}