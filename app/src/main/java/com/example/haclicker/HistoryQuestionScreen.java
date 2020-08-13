package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.FireStoreHistoryEntity;
import com.example.haclicker.DataStructure.Question;

import java.util.List;

public class HistoryQuestionScreen extends AppCompatActivity {
    TextView emptyReminder;
    private final int MAX_DESCRIPTION_LENGTH = 50;
    FireStoreHistoryEntity fireStoreHistoryEntity = null;
    private String classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_question_screen);
        emptyReminder = findViewById(R.id.emptyReminder);
        classID = getIntent().getStringExtra("classID");
        fireStoreHistoryEntity = getIntent().getParcelableExtra("fireStoreHistoryEntity");
        updateUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        // get all existing questions on the firebase server
        FireStoreHistoryEntity fireStoreHistoryEntity = MainScreen.getFireStoreHistoryByID(classID);
        if (fireStoreHistoryEntity != null) {
            // clear previous UI components
            LinearLayout questionListLayout = findViewById(R.id.question_list);
            questionListLayout.removeAllViews();
            // combine questions on the server and questions not published yet.
            // if there's no question, set empty question reminder
            if (fireStoreHistoryEntity.getQuestionList().size() != 0) {
                emptyReminder.setVisibility(View.INVISIBLE);
            } else {
                emptyReminder.setVisibility(View.VISIBLE);
            }

            for (final Question question : fireStoreHistoryEntity.getQuestionList()) {
                // inflate from chunk_question
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionListLayout, false);
                // set question text view with description
                // set question description
                String displayedTxt = question.getQuestionDescription();
                if (displayedTxt.length() > MAX_DESCRIPTION_LENGTH) {
                    displayedTxt = displayedTxt.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
                }
                // Answer History
                // set correct answer
                String answerHistory = "Correct Answer(s): ";
                answerHistory += question.getCorrectAns() == null ? "Not available" : question.getCorrectAns().toString();
                if (fireStoreHistoryEntity.getStudentResponseList().size() == 1) {
                    List<String> studentAnswer = fireStoreHistoryEntity.getStudentResponseList().get(0).getAnswer();
                    answerHistory += "\n Chosen Answer(s): ";
                    answerHistory +=  studentAnswer == null ? "Not available" : studentAnswer.toString();
                }

                final String finalAnswerHistory = answerHistory;
                // set displayed txt
                Button questionDisplayBtn = questionChunk.findViewById(R.id.question_txt);
                questionDisplayBtn.setText(displayedTxt + "\n\n" + answerHistory);
                // set each question chunk onClickListener
                questionDisplayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), HistoryDetailScreen.class);
                        intent.putExtra("classID", classID);
                        intent.putExtra("questionID", question.getQuestionId());
                        startActivity(intent);
                        finish();
                    }
                });
                // populate the chunk to linear layout
                questionListLayout.addView(questionChunk);
            }
        } else {
            // if the question list is null, set empty reminder.
            emptyReminder.setText("No question found");
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HistoryClassScreen.class);
        startActivity(intent);
        finish();
    }
}