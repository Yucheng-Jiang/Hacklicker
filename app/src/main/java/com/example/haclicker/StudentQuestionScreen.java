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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.StudentResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentQuestionScreen extends AppCompatActivity {
    TextView questionTxt;
    ImageButton banBtn;
    private int curQuestionID;
    private String classID;
    private List<String> curChoice = new ArrayList<>();
    private Question question;
    private boolean canAnswer = false;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_question_screen);

        // set UI component
        questionTxt = findViewById(R.id.question_txt);
        banBtn = findViewById(R.id.banBtn);
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
                        Thread.sleep(500);
                        if (!isRunning) {
                            break;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateCorrectAns();
                                updateAccessibility();
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
        // set ban button invisible
        banBtn.setVisibility(View.INVISIBLE);
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
                    // if there's no correct answer, display previous student answers
                    // allow student to update his/her answers
                    if (curChoice.contains(index)) {
                        choiceTxt.setBackgroundColor(android.graphics.Color.parseColor("#ffc38d"));
                    } else {
                        choiceTxt.setBackgroundColor(Color.GRAY);
                    }

                    if (correctAnswer == null || correctAnswer.size() == 0) {
                        choiceTxt.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {
                                if (canAnswer) {
                                    // if the answer is already selected, return it into gray
                                    // and remove from choice history
                                    if (((ColorDrawable) choiceTxt.getBackground()).getColor() ==
                                            android.graphics.Color.parseColor("#ffc38d")) {
                                        choiceTxt.setBackgroundColor(Color.GRAY);
                                        curChoice.remove(index);
                                    } else {
                                        // otherwise add new answers
                                        choiceTxt.setBackgroundColor(android.graphics.Color.parseColor("#ffc38d"));
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
                                } else {
                                    final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                                    animation.setDuration(800); // duration - half a second
                                    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                                    animation.setRepeatCount(1); // Repeat animation infinitely
                                    banBtn.startAnimation(animation);
                                }

                            }
                        });
                    } else {
                        // if there are correct answers, show student's result
                        if (correctAnswer.contains(index)) {
                            // make correct answer choices background green
                            choiceTxt.setTextColor(android.graphics.Color.parseColor("#99ff99"));
                        } else {
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

    private void updateCorrectAns() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms")
                .child(classID);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object id = snapshot.getValue();
                //check if classroom has been closed
                if (id != null && !id.toString().equals("")) {
                    // get previous correct answer list
                    List<String> ans = Student.getQuestionById(curQuestionID).getCorrectAns();
                    // retrieve new correct answer list from firebase
                    List<String> retrievedAns = new ArrayList<>();
                    DataSnapshot answerSnapshot = snapshot.child("Questions")
                            .child(curQuestionID + "")
                            .child("answer");
                    for (DataSnapshot curAns : answerSnapshot.getChildren()) {
                        retrievedAns.add(curAns.getValue().toString());
                    }
                    // if there's difference, update UI
                    if (!retrievedAns.equals(ans)) {
                        // update correct answer list
                        Student.setCorrectAns(retrievedAns, curQuestionID);
                        updateUI();
                    }
                } else {
                    isRunning = false;
                    Toast.makeText(StudentQuestionScreen.this,
                            "Room closed by host", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), FileExportScreen.class);
                    intent.putExtra("canBack", false);
                    intent.putExtra("role", "student");
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateAccessibility() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms")
                .child(classID + "")
                .child("Questions")
                .child(curQuestionID + "")
                .child("canAnswer");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    canAnswer = Boolean.parseBoolean(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), StudentScreen.class);
        intent.putExtra("ClassID", classID);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

}