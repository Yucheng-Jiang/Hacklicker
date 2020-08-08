package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.StudentResponse;
import com.example.haclicker.DataStructure.Teacher;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentQuestionScreen extends AppCompatActivity {
    TextView questionTxt;
    int questionId;
    String classId;
    Button sendAnswer;
    List<String> answers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_question_screen);

        questionTxt = findViewById(R.id.question_txt);
        sendAnswer = findViewById(R.id.sendAnswer);

        Intent intent = getIntent();
        questionId = intent.getIntExtra("Id", 0);
        classId = intent.getStringExtra("classId");
        // display question and choices
        final List<Question> questions = Teacher.getClassroom().getQuestions();
        for (final Question question : questions) {
            // set question description
            if (question.getQuestionId() == questionId) {
                questionTxt.setText(question.getQuestionDescription());
                // populate answer options
                List<String> choices = question.getChoices();
                if (choices != null && choices.size() != 0) {
                    LinearLayout questionList = findViewById(R.id.question_list);
                    questionList.removeAllViews();
                    // create a button to each choice
                    for (int i = 0; i < choices.size(); i++) {
                        String choice = choices.get(i);
                        View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                                questionList, false);
                        final Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                        questionTxt.setText(choice);
                        questionTxt.setId(i);
                        questionTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (((ColorDrawable) questionTxt.getBackground()).getColor() ==
                                        android.graphics.Color.parseColor("#99ff99")) {
                                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                                    Teacher.deleteCorrectAnswer(question, questionTxt.getText().toString());
                                    answers.remove(questionTxt.getId());
                                } else {
                                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                                    Teacher.sendCorrectAnswer(question, questionTxt.getText().toString());
                                    answers.add(questionTxt.getId() + "");
                                }
                                // TODO: when stop button is activated, the correct answer should be the one clicked
                            }
                        });

                        questionList.addView(questionChunk);
                    }
                }
                break;
            }

        }

        sendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Student.sendResponse(new StudentResponse(user.getDisplayName(), user.getEmail(), answers, questionId, System.currentTimeMillis()), classId);
            }
        });
    }
}