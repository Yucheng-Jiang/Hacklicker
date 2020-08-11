package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentScreen extends AppCompatActivity {
    //List<Question> questions;
    ImageButton shareRoom, exitRoom, chatRoom;
    TextView emptyReminder;
    String classID;
    final Boolean[] run = new Boolean[]{new Boolean(true)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_screen);
        // set UI component
        shareRoom = findViewById(R.id.shareRoom);
        exitRoom = findViewById(R.id.leaveRoom);
        chatRoom = findViewById(R.id.studentChatBtn);
        emptyReminder = findViewById(R.id.emptyReminder);
        // get class ID
        classID = getIntent().getStringExtra("ClassID");
        //TODO: debug this
        if (classID == null) {
            Toast.makeText(this, "Class Ended", Toast.LENGTH_SHORT).show();
            finish();
        }
        // exit room button set on click listener
        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FileExportScreen.class);
                intent.putExtra("role", "student");
                intent.putExtra("classID", classID);
                startActivity(intent);
            }
        });
        // share room button set on click listener
        shareRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShareRoomScreen.class);
                intent.putExtra("Id", classID);
                startActivity(intent);
            }
        });
        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
                intent.putExtra("role", "student");
                intent.putExtra("classID", classID);
                startActivity(intent);
            }
        });

        updateUI();
        // update Question
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateQuestion();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
    }

    /**
     * update question list from firebase
     */
    private void updateQuestion() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms").child(classID).child("Questions");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> questions = new ArrayList<>();
                    for (DataSnapshot singleQuestion : snapshot.getChildren()) {
                        List<String> choices = new ArrayList<>();
                        // get all questions
                        for (long i = 0; i < singleQuestion.child("choices").getChildrenCount(); i++) {
                            choices.add(singleQuestion.child("choices").child(i + "").getValue().toString());
                        }
                        String id = singleQuestion.child("questionId").getValue().toString();
                        String description = singleQuestion.child("questionDescription").getValue().toString();
                        boolean canAnswer = Boolean.parseBoolean(singleQuestion
                                .child("canAnswer").getValue().toString());
                        questions.add(new Question(description, Integer.parseInt(id), choices, canAnswer));
                    }
                    // if questions on the server is different from questions in local
                    // update UI
                    if (!questions.equals(Student.getQuestionList())) {
                        Student.setQuestionList(questions);
                        updateUI();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * update question list by populate
     */
    private void updateUI() {
        List<Question> questions = Student.getQuestionList();
        if (questions != null) {
            // remove all current views
            LinearLayout questionListLayout = findViewById(R.id.question_list);
            questionListLayout.removeAllViews();
            if (questions.size() != 0) {
                emptyReminder.setVisibility(View.INVISIBLE);
            } else {
                emptyReminder.setVisibility(View.VISIBLE);
            }
            // iterate through every question
            for (final Question question : questions) {
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionListLayout, false);
                Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                // get current question answer history
                List<String> myHistory = Student.getMyAnswerHistory(question.getQuestionId());
                // if current question has answer, set the question background as yellow
                // otherwise set to gray
                if (myHistory != null && myHistory.size() != 0) {
                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                } else {
                    questionTxt.setBackgroundColor(Color.GRAY);
                }
                // fill in the question description
                questionTxt.setText(question.getQuestionDescription());
                questionTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), StudentQuestionScreen.class);
                        intent.putExtra("ClassID", classID);
                        intent.putExtra("QuestionID", question.getQuestionId());
                        startActivity(intent);
                        run[0] = false;
                        finish();
                    }
                });

                questionListLayout.addView(questionChunk);
            }
        } else {
            emptyReminder.setText("There's no question added.");
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        exitRoom.performClick();
    }
}