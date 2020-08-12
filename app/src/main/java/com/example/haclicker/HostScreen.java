package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;

import java.util.List;

public class HostScreen extends AppCompatActivity {
    List<Question> questions; // store all existing questions
    ImageButton shareRoom, exitRoom, addQuestion, chatRoom;
    TextView emptyReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_screen);
        // add all UI component
        shareRoom = findViewById(R.id.shareRoom);
        exitRoom = findViewById(R.id.leaveRoom);
        emptyReminder = findViewById(R.id.emptyReminder);
        addQuestion = findViewById(R.id.studentChatBtn);
        chatRoom = findViewById(R.id.hostChatBtn);
        // exit room button onClickListener
        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FileExportScreen.class);
                intent.putExtra("role", "host");
                intent.putExtra("classID", Teacher.getClassroom().getClassID());
                startActivity(intent);
            }
        });
        // add question button onClickListener
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddQuestionScreen.class);
                startActivity(intent);
                finish();
            }
        });
        // share room button set on click listener
        shareRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShareRoomScreen.class);
                intent.putExtra("Id", Teacher.getClassroom().getClassID());
                startActivity(intent);
            }
        });
        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
                intent.putExtra("role", "host");
                intent.putExtra("classID", Teacher.getClassroom().getClassID());
                startActivity(intent);
            }
        });
        // update UI
        upDateUI();
    }

    /**
     * populate the screen with all the questions.
     * If there's no questions added, show empty reminder.
     */
    private void upDateUI() {
        // get all existing questions on the firebase server
        questions = Teacher.getClassroom().getQuestions();
        if (questions != null) {
            // clear previous UI components
            LinearLayout questionListLayout = findViewById(R.id.question_list);
            questionListLayout.removeAllViews();
            // combine questions on the server and questions not published yet.
            questions.removeAll(Teacher.getQuestionsToAdd());
            questions.addAll(Teacher.getQuestionsToAdd());
            // if there's no question, set empty question reminder
            if (questions.size() != 0) {
                emptyReminder.setVisibility(View.INVISIBLE);
            } else {
                emptyReminder.setVisibility(View.VISIBLE);
            }
            // populate each question UI component
            for (final Question question : questions) {
                // inflate from chunk_question
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionListLayout, false);
                // set question text view with description
                Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                questionTxt.setText(question.getQuestionDescription());
                // if the question already has correct answer, mark it as yellow
                // otherwise, mark it gray
                if (question.getCorrectAns() != null && question.getCorrectAns().size() != 0) {
                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#ffc38d"));
                } else {
                    questionTxt.setBackgroundColor(Color.GRAY);
                }
                // set each question chunk onClickListener
                questionTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), HostQuestionScreen.class);
                        intent.putExtra("Id", question.getQuestionId());
                        startActivity(intent);
                        finish();
                    }
                });
                // populate the chunk to linear layout
                questionListLayout.addView(questionChunk);
            }
        } else {
            // if the question list is null, set empty reminder.
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        exitRoom.performClick();
    }
}