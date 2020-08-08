package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;

import java.util.List;

public class HostQuestionScreen extends AppCompatActivity {
    TextView questionTxt, emptyReminder;
    Button controlBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_question_screen);

        questionTxt = findViewById(R.id.question_txt);
        emptyReminder = findViewById(R.id.emptyReminder);
        controlBtn = findViewById(R.id.controlBtn);

        Intent intent = getIntent();
        int id = intent.getIntExtra("Id", 0);

        List<Question> questions = Teacher.getClassroom().getQuestions();
        for (final Question question : questions) {
            // set question description
            if (question.getQuestionId() == id) {
                questionTxt.setText(question.getQuestionDescription());
            }
            // populate answer options
            List<String> choices = question.getChoices();
            if (choices != null && choices.size() != 0) {
                emptyReminder.setVisibility(View.INVISIBLE);
                LinearLayout questionList = findViewById(R.id.question_list);
                questionList.removeAllViews();

                for (String choice : choices) {
                    View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                            questionList, false);
                    final Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                    questionTxt.setText(choice);
                    questionTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (controlBtn.getText().equals("Start")) {
                                if (((ColorDrawable) questionTxt.getBackground()).getColor() ==
                                        android.graphics.Color.parseColor("#99ff99")) {
                                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#edb879"));
                                    Teacher.deleteCorrectAnswer(question, questionTxt.getText().toString());
                                } else {
                                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                                    Teacher.sendCorrectAnswer(question, questionTxt.getText().toString());

                                }
                            }
                            // TODO: when stop button is activated, the correct answer should be the one clicked
                        }
                    });

                    questionList.addView(questionChunk);
                }
            } else {
                emptyReminder.setText("There's no question added.");
                emptyReminder.setVisibility(View.VISIBLE);
            }
        }

        controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controlBtn.getText().equals("Start")) {
                    controlBtn.setText("Stop");
                } else {
                    controlBtn.setText("Start");
                }
            }
        });
    }
}