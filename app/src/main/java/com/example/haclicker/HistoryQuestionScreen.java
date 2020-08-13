package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.StudentHistoryEntity;
import com.example.haclicker.DataStructure.Teacher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryQuestionScreen extends AppCompatActivity {
    TextView emptyReminder;
    String role;
    private final int MAX_DESCRIPTION_LENGTH = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_question_screen);
        role = getIntent().getStringExtra("role");
        emptyReminder = findViewById(R.id.emptyReminder);
        updateUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        List<StudentHistoryEntity> studentHistoryEntityList = null;
        // get all existing questions on the firebase server
        if (studentHistoryEntityList != null) {
            // clear previous UI components
            LinearLayout questionListLayout = findViewById(R.id.question_list);
            questionListLayout.removeAllViews();
            // combine questions on the server and questions not published yet.
            // if there's no question, set empty question reminder
            if (studentHistoryEntityList.size() != 0) {
                emptyReminder.setVisibility(View.INVISIBLE);
            } else {
                emptyReminder.setVisibility(View.VISIBLE);
            }

            for (final StudentHistoryEntity entry : studentHistoryEntityList) {
                // inflate from chunk_question
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionListLayout, false);
                // set question text view with description
                // set question description
                String displayedTxt = entry.getQuestionDescription();
                if (displayedTxt.length() > MAX_DESCRIPTION_LENGTH) {
                    displayedTxt = displayedTxt.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
                }
                // Answer History
                // set correct answer
                String answerHistory = "Correct Answer(s): ";
                answerHistory += entry.getCorrectAnswer() == null ? "Not available" : entry.getCorrectAnswer().toString();
                answerHistory += "\n Chosen Answer(s): ";
                answerHistory += entry.getAnswers() == null ? "Not available" : entry.getAnswers().toString();
                final String finalAnswerHistory = answerHistory;
                // set displayed txt
                Button questionDisplayBtn = questionChunk.findViewById(R.id.question_txt);
                questionDisplayBtn.setText(displayedTxt + "\n\n" + answerHistory);
                // set each question chunk onClickListener
                questionDisplayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), HistoryQuestionScreen.class);
                        String detailTxt = entry.getQuestionDescription() + "\n\n";
                        List<String> options = entry.getChoices();
                        for (String option : options) {
                            detailTxt += option.trim() + "\n";
                        }
                        detailTxt += finalAnswerHistory;
                        intent.putExtra("detail", detailTxt);
                        intent.putExtra("role", role);
                        startActivity(intent);
                        finish();
                    }
                });
                // populate the chunk to linear layout
                questionListLayout.addView(questionChunk);
            }
        } else {
            // if the question list is null, set empty reminder.
            emptyReminder.setText("No answer record found");
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HistoryClassScreen.class);
        intent.putExtra("role", role);
        startActivity(intent);
        finish();
    }
}