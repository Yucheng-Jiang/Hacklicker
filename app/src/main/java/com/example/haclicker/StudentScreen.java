package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.Teacher;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentScreen extends AppCompatActivity {
    //List<Question> questions;
    ImageButton shareRoom, exitRoom, addQuestion;
    TextView emptyReminder;
    String classID;
    final Boolean[] run = new Boolean[]{new Boolean(true)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_screen);
        System.out.println("fuck");
        // add image buttons
        shareRoom = findViewById(R.id.shareRoom);
        exitRoom = findViewById(R.id.leaveRoom);
        emptyReminder = findViewById(R.id.emptyReminder);
        addQuestion = findViewById(R.id.makePost);

        classID = getIntent().getStringExtra("ClassID");
        // exit room button set on click listener
        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: store data and delete cloud data
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent);
            }
        });
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddQuestionScreen.class);
                startActivity(intent);
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
        // update UI
        upDateUI();

    }

    private void upDateUI() {

        //questions = Student.retrieveQuestions(classID);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms").child(classID).child("Questions");

        /*
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                List<Question> questions = new ArrayList<>();
                for (DataSnapshot singleQuestion : snapshot.getChildren()) {
                    List<String> choices = new ArrayList<>();
                    for (long i = 0; i < singleQuestion.child("choices").getChildrenCount(); i++) {
                        choices.add(singleQuestion.child("choices").child(i + "").getValue().toString());
                    }
                    String id = singleQuestion.child("questionId").getValue().toString();
                    String description = singleQuestion.child("questionDescription").getValue().toString();
                    questions.add(new Question(description, Integer.parseInt(id), choices));
                }

                if (questions != null) {
                    emptyReminder.setVisibility(View.INVISIBLE);
                    LinearLayout questionList = findViewById(R.id.question_list);
                    questionList.removeAllViews();

                    for (final Question question : questions) {
                        View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                                questionList, false);
                        Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                        List<String> myHistory = Student.getMyAnswerHistory(question.getQuestionId());
                        if (myHistory != null && myHistory.size() != 0) {
                            questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                        } else {
                            questionTxt.setBackgroundColor(Color.GRAY);
                        }
                        questionTxt.setText(question.getQuestionDescription());
                        questionTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), HostQuestionScreen.class);
                                intent.putExtra("Id", question.getQuestionId());
                                startActivity(intent);
                            }
                        });

                        questionList.addView(questionChunk);
                    }
                } else {
                    emptyReminder.setText("There's no question added.");
                    emptyReminder.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (run[0]) {
                    List<Question> questions = new ArrayList<>();
                    for (DataSnapshot singleQuestion : snapshot.getChildren()) {
                        List<String> choices = new ArrayList<>();
                        for (long i = 0; i < singleQuestion.child("choices").getChildrenCount(); i++) {
                            choices.add(singleQuestion.child("choices").child(i + "").getValue().toString());
                        }
                        String id = singleQuestion.child("questionId").getValue().toString();
                        String description = singleQuestion.child("questionDescription").getValue().toString();
                        questions.add(new Question(description, Integer.parseInt(id), choices));
                    }

                    if (questions != null) {
                        emptyReminder.setVisibility(View.INVISIBLE);
                        LinearLayout questionList = findViewById(R.id.question_list);
                        questionList.removeAllViews();

                        for (final Question question : questions) {
                            View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                                    questionList, false);
                            Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                            List<String> myHistory = Student.getMyAnswerHistory(question.getQuestionId());
                            if (myHistory != null && myHistory.size() != 0) {
                                questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                            } else {
                                questionTxt.setBackgroundColor(Color.GRAY);
                            }
                            questionTxt.setText(question.getQuestionDescription());
                            questionTxt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), HostQuestionScreen.class);
                                    intent.putExtra("Id", question.getQuestionId());
                                    startActivity(intent);
                                    run[0] = false;
                                    finish();
                                }
                            });

                            questionList.addView(questionChunk);
                        }
                    } else {
                        emptyReminder.setText("There's no question added.");
                        emptyReminder.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}