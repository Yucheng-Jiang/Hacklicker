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
        // display question and choices
        Question question = Student.getQuestionById(questionId);
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
                            answers.remove(questionTxt.getId());
                        } else {
                            questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                            answers.add(questionTxt.getId() + "");
                        }
                        // TODO: when stop button is activated, the correct answer should be the one clicked
                    }
                });

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