package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.Teacher;

import java.util.List;

public class StudentScreen extends AppCompatActivity {
    List<Question> questions;
    ImageButton exitRoom, shareRoom, makePost;
    String classId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_screen);
        Intent intent = getIntent();
        classId = intent.getStringExtra("Id");
        // add image buttons
        exitRoom = findViewById(R.id.leaveRoom);
        shareRoom = findViewById(R.id.shareRoom);
        makePost = findViewById(R.id.makePost);
        // exit room button set on click listener
        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: store data and delete cloud data
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent);
            }
        });

        shareRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShareRoomScreen.class);
                startActivity(intent);
            }
        });
        // update UI
        upDateUI();

    }

    private void upDateUI() {
        questions = Student.retrieveQuestions(classId);
        if (questions != null) {
            LinearLayout questionList = findViewById(R.id.question_list);
            questionList.removeAllViews();

            if (Teacher.getQuestionsToAdd() != null) {
                questions.addAll(Teacher.getQuestionsToAdd());
            }
            for (final Question question : questions) {
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionList, false);
                Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                questionTxt.setText(question.getQuestionDescription());
                questionTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), HostQuestionScreen.class);
                        intent.putExtra("questionId", question.getQuestionId());
                        intent.putExtra("classId", classId);
                        startActivity(intent);
                    }
                });

                questionList.addView(questionChunk);
            }
        }

    }
}