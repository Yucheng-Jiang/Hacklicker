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

        questionTxt = findViewById(R.id.question_txt);

        Intent intent = getIntent();
        curQuestionID = intent.getIntExtra("QuestionID", 0);
        classID = intent.getStringExtra("ClassID");
        curChoice = Student.getMyAnswerHistory(curQuestionID);

        if (curChoice == null) {
            curChoice = new ArrayList<>();
        }

        // display question and choices
        question = Student.getQuestionById(curQuestionID);
        updateUI();
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
                                        List<String> ans = Student.getQuestionById(curQuestionID)
                                                .getCorrectAns();
                                        List<String> retrievedAns = new ArrayList<>();
                                        for (DataSnapshot curAns : snapshot.getChildren()) {
                                            retrievedAns.add(curAns.getValue().toString());
                                        }
                                        Student.setCorrectAns(retrievedAns, curQuestionID);
                                        if (!retrievedAns.equals(ans)) {
                                            updateUI();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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

    private void updateUI() {
        // set question description
        if (question.getQuestionId() == curQuestionID) {
            questionTxt.setText(question.getQuestionDescription());
            // populate answer options
            List<String> choices = question.getChoices();
            if (choices != null && choices.size() != 0) {
                LinearLayout questionList = findViewById(R.id.question_list);
                questionList.removeAllViews();

                List<String> correctAnswer = Student.getQuestionById(curQuestionID).getCorrectAns();
                // create a button to each choice
                for (int i = 0; i < choices.size(); i++) {
                    String choice = choices.get(i);
                    View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                            questionList, false);
                    final Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                    final String index =Character.toString((char) (((int) 'A') + i));
                    questionTxt.setText(choice);
                    questionTxt.setBackgroundColor(Color.GRAY);
                    if (correctAnswer == null || correctAnswer.size() == 0) {
                        questionTxt.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {
                                if (((ColorDrawable) questionTxt.getBackground()).getColor() ==
                                        android.graphics.Color.parseColor("#fed8b1")) {
                                    questionTxt.setBackgroundColor(Color.GRAY);
                                    // un-choose
                                    curChoice.remove(index);
                                    Student.updateQuestionAnswer(curQuestionID, curChoice);
                                } else {
                                    questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                                    curChoice.add(index);
                                    Student.updateQuestionAnswer(curQuestionID, curChoice);
                                }
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                StudentResponse studentResponse = new StudentResponse(
                                        user.getDisplayName(),
                                        user.getEmail(),
                                        curChoice,
                                        curQuestionID,
                                        System.currentTimeMillis());

                                Student.sendResponse(studentResponse, classID);
                            }
                        });
                    } else {
                        if (correctAnswer.contains(index)) {
                            questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                            if (!curChoice.contains(index)) {
                                questionTxt.setTextColor(Color.RED);
                            }
                        } else {
                            questionTxt.setBackgroundColor(Color.GRAY);
                            if (curChoice.contains(index)) {
                                questionTxt.setTextColor(Color.RED);
                            }
                        }
                    }
                    questionList.addView(questionChunk);
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