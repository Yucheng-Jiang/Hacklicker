package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.Teacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentScreen extends AppCompatActivity {
    List<Question> questions = new ArrayList<>();
    ImageButton exitRoom, shareRoom, makePost;
    String classId;
    TextView emptyReminder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_screen);
        Intent intent = getIntent();
        classId = intent.getStringExtra("Id");
        // add image buttons
        exitRoom = findViewById(R.id.leaveRoom);
        shareRoom = findViewById(R.id.shareRoom);
        makePost = findViewById(R.id.makePost);
        emptyReminder = findViewById(R.id.emptyReminder);
        // exit room button set on click listener
        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: store data and delete cloud data
                Student.clearHistory();
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent);
            }
        });

        shareRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShareRoomScreen.class);
                intent.putExtra("Id", classId);
                startActivity(intent);
            }
        });

        upDateUI();
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                upDateUI();
                                // update TextView here!
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
    }

    private void upDateUI() {
        questions.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClassRooms")
                .child(classId).child("Questions");
        ref.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot question : snapshot.getChildren()) {
                    List<String> choices = new ArrayList<>();
                    for (long i = 0; i < question.child("choices").getChildrenCount(); i++) {
                        choices.add(question.child("choices").child(i + "").getValue().toString());
                    }
                    String id = question.child("questionId").getValue().toString();
                    String description = question.child("questionDescription").getValue().toString();
                    questions.add(new Question(description, Integer.parseInt(id), choices));
                }

                Student.updateQuestionList(questions);

                if (questions != null) {
                    LinearLayout questionList = findViewById(R.id.question_list);
                    questionList.removeAllViews();
                    emptyReminder.setVisibility(View.INVISIBLE);
                    for (final Question question : questions) {
                        View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                                questionList, false);
                        Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                        if (Student.getMyAnswerHistory(question.getQuestionId()) == null
                         || Student.getMyAnswerHistory(question.getQuestionId()).size() == 0) {
                            questionTxt.setBackgroundColor(Color.GRAY);
                        } else {
                            questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                        }
                        questionTxt.setText(question.getQuestionDescription());
                        questionTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), StudentQuestionScreen.class);
                                intent.putExtra("questionId", question.getQuestionId());
                                intent.putExtra("classId", classId);
                                startActivity(intent);
                            }
                        });
                        questionList.addView(questionChunk);
                    }
                } else {
                    emptyReminder.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}