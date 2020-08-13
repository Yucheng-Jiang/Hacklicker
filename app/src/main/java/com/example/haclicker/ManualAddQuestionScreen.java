package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
    private List<String> options;
    private List<EditText> editText;
    private Intent intent;
    private boolean isQuickAdd;
    private final int DEFAUL_OPTION_COUNT = 4;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_add_question_screen);
        // initialize field
        options = new ArrayList<>();
        editText = new ArrayList<>();
        intent = getIntent();
        isQuickAdd = intent.getBooleanExtra("quickAdd", false);
        // set UI component
        questionDescribe = findViewById(R.id.questionDescribe);
        addOption = findViewById(R.id.addOption);
        createQuestion = findViewById(R.id.createQuestion);

        if (isQuickAdd) {
            questionDescribe.setText("Question " + (Teacher.getClassroom().getQuestions().size() + 1));
            questionDescribe.setGravity(Gravity.CENTER);
            // add a new option editText
           for (int i = 0; i < DEFAUL_OPTION_COUNT; i++) {
               char index = (char) ((int) 'A' + i);
               addOption("Option " + Character.toString(index));
           }
        }

        // add option button clicked
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOption(null);
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
    private void addOption(String optionDescription) {
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
        EditText optionTxt = optionChunk.findViewById(R.id.optionTxt);
        if (optionDescription != null) {
            optionTxt.setText(optionDescription);
            optionTxt.setGravity(Gravity.CENTER);
        } else {
            optionTxt.setHint("add option description here");
        }
        optionList.addView(optionChunk);
        editText.add(optionTxt);
    }
}