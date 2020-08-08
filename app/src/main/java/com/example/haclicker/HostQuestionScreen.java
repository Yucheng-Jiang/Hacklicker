package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    int curQuestionID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_question_screen);

        questionTxt = findViewById(R.id.question_txt);
        controlBtn = findViewById(R.id.controlBtn);
        test = findViewById(R.id.test);
        resultBarChart = findViewById(R.id.resultBarChart);

        Intent intent = getIntent();
        curQuestionID = intent.getIntExtra("Id", 0);
        // display question and choices
        List<Question> questions = Teacher.getClassroom().getQuestions();
        for (final Question question : questions) {
            // set question description
            if (question.getQuestionId() == curQuestionID) {
                questionTxt.setText(question.getQuestionDescription());
                // populate answer options
                List<String> choices = question.getChoices();
                if (choices != null && choices.size() != 0) {
                    LinearLayout questionList = findViewById(R.id.question_list);
                    questionList.removeAllViews();
                    // create a button to each choice
                    for (String choice : choices) {
                        View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                                questionList, false);
                        final Button questionTxt = questionChunk.findViewById(R.id.question_txt);
                        questionTxt.setText(choice);
                        questionTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (controlBtn.getText().equals("Start")) {
                                    if (((ColorDrawable) questionTxt.getBackground()).getColor() ==
                                            android.graphics.Color.parseColor("#99ff99")) {
                                        questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#fed8b1"));
                                        Teacher.deleteCorrectAnswer(question, questionTxt.getText().toString());
                                    } else {
                                        questionTxt.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                                        Teacher.sendCorrectAnswer(question, questionTxt.getText().toString());
                                    }
                                }
                                // TODO: when stop button is activated, the correct answer should be the one clicked
                            }
                        });

                        questionList.addView(questionChunk);
                    }
                }
                break;
            }

        }
        // control button logic
        controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controlBtn.getText().equals("Start")) {
                    controlBtn.setText("Stop");
                    resultBarChart.setVisibility(View.INVISIBLE);
                    Teacher.addQuestion(curQuestionID);
                } else {
                    controlBtn.setText("Start");
                    //fetch result from server
                    Map<String, Integer> result = showResult();
//                    Map<String, Integer> result = new HashMap<>();
//                    result.put("A", 10);
//                    result.put("B", 50);
//                    result.put("C", 35);
//                    result.put("D", 40);
//                    result.put("E", 55);
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
                    resultBarChart.animateXY(1500, 1500);
                    XAxis xAxis = resultBarChart.getXAxis();
                    xAxis.setValueFormatter(new MyXAxisValueFormatter(horizontalAxisSet));
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

    private void updateUI() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms")
                .child(Teacher.getClassroom().getClassID())
                .child("StudentResponse").child(curQuestionID + "");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                test.setText(snapshot.getChildrenCount() + " stu voted");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Map<String, Integer> showResult() {

        final Map<String, Integer> result = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms")
                .child(Teacher.getClassroom().getClassID())
                .child("StudentResponse").child(curQuestionID + "");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleResponse : snapshot.getChildren()) {
                    for (DataSnapshot singleAnswer : singleResponse.child("answer").getChildren()) {
                        if (!result.containsKey(singleAnswer.toString())) {
                            result.put(singleAnswer.toString(), 1);
                        } else {
                            result.put(singleAnswer.toString(), result.get(singleAnswer.toString()) + 1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return result;
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
}