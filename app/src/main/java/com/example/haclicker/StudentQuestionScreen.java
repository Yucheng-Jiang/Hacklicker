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
    private int curQuestionID;
    private String classID;
    private List<String> curChoice = new ArrayList<>();
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_question_screen);

        // set UI component
        questionTxt = findViewById(R.id.question_txt);
        // retrieve intent extra
        Intent intent = getIntent();
        curQuestionID = intent.getIntExtra("QuestionID", 0);
        classID = intent.getStringExtra("ClassID");
        // update current question answer history
        // if there's no history, make a new list
        curChoice = Student.getMyAnswerHistory(curQuestionID);
        if (curChoice == null) {
            curChoice = new ArrayList<>();
        }
        // display question and choices
        question = Student.getQuestionById(curQuestionID);
        updateUI();
        // check if there's update on question correct answer on current question
        // if yes, update UI
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseReference ref = FirebaseDatabase.getInstance()
                                        .getReference("ClassRooms")
                                        .child(classID)
                                        .child("Questions").child(curQuestionID + "")
                                        .child("answer");

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // get previous correct answer list
                                        List<String> ans = Student.getQuestionById(curQuestionID)
                                                .getCorrectAns();
                                        // retrieve new correct answer list from firebase
                                        List<String> retrievedAns = new ArrayList<>();
                                        for (DataSnapshot curAns : snapshot.getChildren()) {
                                            retrievedAns.add(curAns.getValue().toString());
                                        }
                                        // update correct answer list
                                        Student.setCorrectAns(retrievedAns, curQuestionID);
                                        // if there's difference, update UI
                                        if (!retrievedAns.equals(ans)) {
                                            updateUI();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
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
     * update question display UI depend on correct answers and student answers
     */
    private void updateUI() {
        // set question description
        if (question.getQuestionId() == curQuestionID) {
            questionTxt.setText("Question Description: \n" + question.getQuestionDescription());
            // populate answer options
            List<String> choices = question.getChoices();
            if (choices != null && choices.size() != 0) {
                // remove all previous views
                LinearLayout questionListLayout = findViewById(R.id.question_list);
                questionListLayout.removeAllViews();
                // get correct answer list
                List<String> correctAnswer = Student.getQuestionById(curQuestionID).getCorrectAns();
                // create a button to each choice
                for (int i = 0; i < choices.size(); i++) {
                    String choice = choices.get(i);
                    View choiceChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                            questionListLayout, false);
                    final Button choiceTxt = choiceChunk.findViewById(R.id.question_txt);
                    final String index =Character.toString((char) (((int) 'A') + i));
                    choiceTxt.setText(choice);
                    choiceTxt.setBackgroundColor(Color.GRAY);
                    // if there's no correct answer, display previous student answers
                    // allow student to update his/her answers
                    if (correctAnswer == null || correctAnswer.size() == 0) {
                        choiceTxt.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {

                                DatabaseReference ref = FirebaseDatabase.getInstance()
                                        .getReference("ClassRooms")
                                        .child(classID + "")
                                        .child("Questions")
                                        .child(curQuestionID + "")
                                        .child("canAnswer");

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean canAnswer = Boolean.parseBoolean(snapshot.getValue()
                                                .toString());
                                        if (canAnswer) {
                                            // if the answer is already selected, return it into gray
                                            // and remove from choice history
                                            if (((ColorDrawable) choiceTxt.getBackground()).getColor() ==
                                                    android.graphics.Color.parseColor("#fed8b1")) {
                                                choiceTxt.setBackgroundColor(Color.GRAY);
                                                curChoice.remove(index);
                                            } else {
                                                // otherwise add new answers
                                                choiceTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                                                curChoice.add(index);
                                            }
                                            Student.updateQuestionAnswer(curQuestionID, curChoice);
                                            // send student response to server
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            StudentResponse studentResponse = new StudentResponse(
                                                    user.getDisplayName(),
                                                    user.getEmail(),
                                                    curChoice,
                                                    curQuestionID,
                                                    System.currentTimeMillis());
                                            Student.sendResponse(studentResponse, classID);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });
                    } else {
                        // if there are correct answers, show student's result
                        if (correctAnswer.contains(index)) {
                            // make correct answer choices background green
                            choiceTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                            if (!curChoice.contains(index)) {
                                // if student did not select, mark the text as red
                                choiceTxt.setTextColor(Color.RED);
                            }
                        } else {
                            // mark incorrect questions as gray
                            choiceTxt.setBackgroundColor(Color.GRAY);
                            if (curChoice.contains(index)) {
                                // if student selected, mark the text as red
                                choiceTxt.setTextColor(Color.RED);
                            }
                        }
                    }
                    questionListLayout.addView(choiceChunk);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), StudentScreen.class);
        intent.putExtra("ClassID", classID);
        startActivity(intent);
        finish();
    }
}