package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
        setContentView(R.layout.activity_student_question_screen);

        questionTxt = findViewById(R.id.question_txt);
        sendAnswer = findViewById(R.id.sendAnswer);

        Intent intent = getIntent();
        questionId = intent.getIntExtra("questionId", 0);
        classId = intent.getStringExtra("classId");
        // get my answer history
        if (Student.getMyAnswerHistory(questionId) != null) {
            answers = Student.getMyAnswerHistory(questionId);
        }
        // display question and choices
        final Question question = Student.getQuestionById(questionId);
        questionTxt.setText(question.getQuestionDescription());
        // populate answer options
        List<String> correctAns = question.getCorrectAns();
        List<String> choices = question.getChoices();
        // already have correct answer
        if (correctAns == null || correctAns.size() == 0) {
            LinearLayout questionList = findViewById(R.id.question_list);
            for (int i = 0; i < choices.size(); i++) {
                String choice = choices.get(i);
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionList, false);
                final Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                final char index =(char) (((int) 'A') + i);
                if (answers.contains(Character.toString(index))) {
                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                } else {
                    questionTxt.setBackgroundColor(Color.GRAY);
                }
                questionTxt.setText(choice);
                questionTxt.setId(i);
                questionTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String thisAnswerIndex = Character.toString(index);
                        if (((ColorDrawable) questionTxt.getBackground()).getColor() == Color.GRAY) {
                            questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                            System.out.println(thisAnswerIndex);
                            answers.add(thisAnswerIndex);
                            sendAnswer.setText("Send");
                            sendAnswer.setTextColor(Color.BLACK);
                        } else {
                            questionTxt.setBackgroundColor(Color.GRAY);
                            answers.remove(thisAnswerIndex);
                            sendAnswer.setText("Send");
                            sendAnswer.setTextColor(Color.BLACK);
                        }
                    }
                });

                questionList.addView(questionChunk);
            }
        } else {
            LinearLayout questionList = findViewById(R.id.question_list);
            questionList.removeAllViews();

            // if don't have correct answer, restore previous answer
            for (int i = 0; i < choices.size(); i++) {
                String choice = choices.get(i);
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionList, false);
                final Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                String strAnswer =Character.toString ((char) (((int) 'A') + i));
                if (correctAns.contains(strAnswer)) {
                    // mark correct as green
                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                } else if (answers.contains(strAnswer)) {
                    questionTxt.setBackgroundColor(Color.RED);
                } else {
                    questionTxt.setBackgroundColor(Color.GRAY);
                }
                questionTxt.setText(choice);
                questionList.addView(questionChunk);
            }
        }

        sendAnswer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Student.sendResponse(new StudentResponse(user.getDisplayName(), user.getEmail(), answers, questionId, System.currentTimeMillis()), classId);
                Student.updateQuestionAnswer(questionId, answers);
                sendAnswer.setText("Already sent!");
                sendAnswer.setTextColor(Color.GREEN);
        }
        });
    }
}