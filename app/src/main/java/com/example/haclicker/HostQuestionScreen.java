package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostQuestionScreen extends AppCompatActivity {
    TextView questionTxt, test;
    Button controlBtn;
    BarChart resultBarChart;
    ScrollView scrollView;
    int curQuestionID;
    Question curQuestion;
    List<String> correctAnswers;

        @SuppressLint("SetTextI18n")
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_question_screen);
        // set UI component
        questionTxt = findViewById(R.id.question_txt);
        controlBtn = findViewById(R.id.sendAnswer);
        test = findViewById(R.id.test);
        scrollView = findViewById(R.id.scrollView);
        resultBarChart = findViewById(R.id.resultBarChart);
        // get intent extra
        Intent intent = getIntent();
        curQuestionID = intent.getIntExtra("Id", 0);
        // get current question info
        curQuestion = Teacher.getClassroom().getQuestionById(curQuestionID);
        // set control button

        if (curQuestion != null) {
            // display question and choices
            questionTxt.setText("Question Description: \n" + curQuestion.getQuestionDescription());
            // populate answer options
            List<String> choices = curQuestion.getChoices();
            if (choices != null && choices.size() != 0) {
                LinearLayout questionList = findViewById(R.id.question_list);
                questionList.removeAllViews();
                // create a button to each choice
                for (int i = 0; i < choices.size(); i++) {
                    String choice = choices.get(i);
                    View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                            questionList, false);
                    final Button optionTxt = questionChunk.findViewById(R.id.question_txt);
                    final String index =Character.toString((char) (((int) 'A') + i));
                    optionTxt.setText(choice);
                    // change optionTxt color based on correct answers
                    if (correctAnswers != null && correctAnswers.size() != 0) {
                        if (correctAnswers.contains(index)) {
                            // make correct answer choices background green
                            optionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                        } else {
                            // mark incorrect questions as gray
                            optionTxt.setBackgroundColor(android.graphics.Color.parseColor("#ffc38d"));
                        }
                    } else {
                        optionTxt.setBackgroundColor(android.graphics.Color.parseColor("#ffc38d"));
                    }

                    optionTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // if there's no correct questions marked
                            if (controlBtn.getText().equals("Start")) {
                                if (((ColorDrawable) optionTxt.getBackground()).getColor() ==
                                        android.graphics.Color.parseColor("#99ff99")) {
                                    optionTxt.setBackgroundColor(android.graphics.Color.parseColor("#ffc38d"));
                                    correctAnswers.remove(index); // local history
                                    Teacher.deleteCorrectAnswer(curQuestion, index);
                                    if (correctAnswers.size() == 0) {
                                        controlBtn.setBackgroundColor(android.graphics.Color.parseColor("#ffc38d"));
                                    }
                                } else {
                                    optionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                                    Teacher.sendCorrectAnswer(curQuestion, index);
                                    correctAnswers.remove(index);
                                    correctAnswers.add(index); // local history
                                    // set control button to gray
                                    controlBtn.setBackgroundColor(Color.GRAY);
                                }
                            }
                        }
                    });
                    questionList.addView(questionChunk);
                }
            }
        }
        // if there's correct answer, mark control button as Gray
        if (correctAnswers != null && correctAnswers.size() != 0) {
            controlBtn.setBackgroundColor(Color.GRAY);
        }
        // control button onClickListener
        correctAnswers = curQuestion.getCorrectAns();
        if (curQuestion.getCanAnswer()) {
            controlBtn.setText("Stop");
        } else {
            controlBtn.setText("Start");
        }

        controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if there's no correct question marked
                if (curQuestion.getCorrectAns() == null || curQuestion.getCorrectAns().size() == 0) {
                    if (controlBtn.getText().equals("Start")) {
                        controlBtn.setText("Stop");
                        // update firebase
                        Teacher.addQuestion(curQuestionID);
                        Teacher.setStudentAccessibility(true, curQuestionID);
                    } else {
                        controlBtn.setText("Start");
                        // update firebase
                        Teacher.setStudentAccessibility(false, curQuestionID);
                        showResult();
                    }
                } else {
                    // if there's already question marked
                    controlBtn.setText("Start");
                    Toast.makeText(HostQuestionScreen.this, "Cannot Start Completed Question!", Toast.LENGTH_LONG).show();
                }


            }
        });

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Teacher.getClassroom() != null)
                                    updateUI();
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

    /**
     * update number of students voted
     */
    private void updateUI() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms")
                .child(Teacher.getClassroom().getClassID())
                .child("StudentResponse").child(curQuestionID + "");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                test.setText(snapshot.getChildrenCount() + " stu voted");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showResult() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms")
                .child(Teacher.getClassroom().getClassID())
                .child("StudentResponse").child(curQuestionID + "");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> result = new HashMap<>();
                for (DataSnapshot singleResponse : snapshot.getChildren()) {
                    for (DataSnapshot singleAnswer : singleResponse.child("answer").getChildren()) {
                        if (!result.containsKey(singleAnswer.toString())) {
                            result.put(singleAnswer.getValue().toString(), 1);
                        } else {
                            result.put(singleAnswer.getValue().toString(), result.get(singleAnswer.toString()) + 1);
                        }
                    }
                }
                //draw result bar chart
                resultBarChart.setVisibility(View.VISIBLE);
                resultBarChart.setDrawBarShadow(false);
                resultBarChart.setDrawValueAboveBar(true);
                resultBarChart.setMaxVisibleValueCount(50);
                resultBarChart.setPinchZoom(false);
                resultBarChart.setDrawGridBackground(true);

                //set bar chart entry
                ArrayList<BarEntry> resultEntries = new ArrayList<>();
                String[] horizontalAxisSet = new String[result.size()];
                String label = "";
                int i = 0;
                for (String entry : result.keySet()) {
                    resultEntries.add(new BarEntry(i, result.get(entry)));
                    horizontalAxisSet[i] = entry;
                    label = label + entry + ",";
                    i++;
                }
                BarDataSet barDataSet = new BarDataSet(resultEntries, label);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData data = new BarData(barDataSet);
                resultBarChart.setData(data);
                resultBarChart.animateXY(500, 500);
                XAxis xAxis = resultBarChart.getXAxis();
                xAxis.setValueFormatter(new MyXAxisValueFormatter(horizontalAxisSet));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class MyXAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HostScreen.class);
        startActivity(intent);
        finish();
    }


}